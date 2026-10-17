package com.zhwii.koreancraft;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Immediate-mode dashboard. Everything is drawn with GuiGraphicsExtractor primitives and input is polled
 * through GLFW, so the screen depends on very little of Minecraft's (frequently changing) widget API.
 */
final class KoreanScreen extends Screen {
    enum Page { HOME, FLASH, HANGUL, QUIZ, REVIEW, DIALOGUE, ARENA, QUESTS, STATS }

    // ------------------------------------------------------------------ palette
    private static final int BG = 0xF2101A2E, BORDER = 0xFF2E3F63;
    private static final int HEAD_TOP = 0xFF263A66, HEAD_BOT = 0xFF16223C;
    private static final int CARD = 0xFF1B2A47, CARD_BORDER = 0xFF35486E;
    private static final int GOLD = 0xFFFFC857, TEAL = 0xFF5EEAD4, ROSE = 0xFFFB7185, GREEN = 0xFF4ADE80;
    private static final int BLUE = 0xFF60A5FA, ORANGE = 0xFFFB923C, PURPLE = 0xFFC084FC;
    private static final int TEXT = 0xFFE8EEFC, DIM = 0xFF8FA0C0, DARK = 0xFF0B1220;

    private static final String[] CARD_ICON = {"말", "ㄱ", "?", "복", "안", "전", "!", "%"};
    private static final String[] CARD_NAME = {"Từ vựng", "Hangul", "Trắc nghiệm", "Ôn lỗi sai", "Hội thoại", "Đấu trường", "Nhiệm vụ", "Thống kê"};
    private static final int[] CARD_COLOR = {GOLD, TEAL, BLUE, ROSE, PURPLE, ORANGE, GREEN, 0xFFA3B1D6};
    private static final Page[] CARD_PAGE = {Page.FLASH, Page.HANGUL, Page.QUIZ, Page.REVIEW, Page.DIALOGUE, Page.ARENA, Page.QUESTS, Page.STATS};

    private static final int[] WATCH = {
        GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_5, GLFW.GLFW_KEY_6,
        GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_UP,
        GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_BACKSPACE
    };

    private static final class Hit {
        final int x, y, w, h; final Runnable action;
        Hit(int x, int y, int w, int h, Runnable a) { this.x = x; this.y = y; this.w = w; this.h = h; this.action = a; }
    }

    /** One multiple-choice question (shared by quiz, review and arena). */
    private static final class Question {
        Data.Word word; boolean koToVi; final String[] choices = new String[4]; int correct; int picked = -1;
        String answerText() { return koToVi ? word.vi() : word.ko(); }
    }

    // ------------------------------------------------------------------ state
    private final Progress p;
    private final Random rnd = new Random();
    private final List<Hit> hits = new ArrayList<>();
    private final boolean[] keyPrev = new boolean[512];
    private final boolean[] edge = new boolean[512];
    private boolean mousePrev, first = true;
    private final long openedAt = System.currentTimeMillis();
    private GuiGraphicsExtractor gx;
    private int mx, my, px, py, pw, ph;

    private Page page;
    private String toast = ""; private long toastUntil; private int toastColor = GOLD;
    private long resetArmedUntil;

    // flashcards
    private int fCat = -1, fPos; private boolean fFlip, fShuffle; private List<Integer> fList = new ArrayList<>();
    // hangul
    private int jSel;
    // quiz / review
    private Question quiz; private int qMode = 2, qCombo, qRight, qTotal; private boolean reviewMode;
    // dialogue
    private List<Integer> dOrder = new ArrayList<>(); private int dPos, dRight, dPicked = -1;
    private final String[][] dOpts = new String[3][3]; // ko, vi, "1"/"0"
    private boolean dDone;
    // arena
    private enum ArenaState { INTRO, PLAY, FEEDBACK, END }
    private static final int A_TOTAL = 8;
    private int aLevel;   // 0 = basic, 1-6 = TOPIK, 7 = mixed TOPIK
    private ArenaState aState = ArenaState.INTRO; private Question arena;
    private int aQ, aHearts, aScore, aCombo, aRight, aMistakes; private boolean aWon;
    private long aStart, aFeedbackUntil;
    // quests
    private int hoverAch = -1;

    KoreanScreen(Progress progress, Page start) {
        super(Component.literal("KoreanCraft"));
        this.p = progress;
        this.page = start;
        rebuildFlash();
        if (start == Page.QUIZ) nextQuiz();
    }

    /** Called by the mod when progress produces a message while this screen is open. */
    void showToast(String msg) {
        toast = msg.replaceAll("\u00a7.", "");
        toastUntil = System.currentTimeMillis() + 2600;
        toastColor = msg.contains("\u00a7c") ? ROSE : msg.contains("\u00a7a") ? GREEN : GOLD;
    }

    @Override public void onClose() {
        if (page != Page.HOME) { page = Page.HOME; return; }
        super.onClose();
    }

    // ================================================================== frame
    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        this.gx = g; this.mx = mouseX; this.my = mouseY;
        hits.clear();

        long win = this.minecraft.getWindow().handle();
        boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean click = down && !mousePrev; mousePrev = down;
        for (int k : WATCH) {
            boolean now = GLFW.glfwGetKey(win, k) == GLFW.GLFW_PRESS;
            edge[k] = now && !keyPrev[k]; keyPrev[k] = now;
        }
        if (first) { first = false; click = false; java.util.Arrays.fill(edge, false); }

        long now = System.currentTimeMillis();
        pw = Math.min(384, this.width - 8); ph = Math.min(262, this.height - 6);
        float t = Math.min(1f, (now - openedAt) / 180f);
        px = (this.width - pw) / 2;
        py = Math.max(2, (this.height - ph) / 2) + Math.round((1 - t) * (1 - t) * 14);

        handleKeys(now);
        tickArena(now);

        drawFrame();
        switch (page) {
            case HOME -> drawHome();
            case FLASH -> drawFlash();
            case HANGUL -> drawHangul();
            case QUIZ -> drawQuiz();
            case REVIEW -> drawReview();
            case DIALOGUE -> drawDialogue();
            case ARENA -> drawArena(now);
            case QUESTS -> drawQuests();
            case STATS -> drawStats(now);
        }
        drawToast(now);

        if (click) for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (mx >= h.x && mx < h.x + h.w && my >= h.y && my < h.y + h.h) { h.action.run(); break; }
        }
    }

    private boolean e(int k) { return edge[k]; }
    private boolean enter() { return e(GLFW.GLFW_KEY_ENTER) || e(GLFW.GLFW_KEY_KP_ENTER) || e(GLFW.GLFW_KEY_SPACE); }

    private void handleKeys(long now) {
        if (page != Page.HOME && e(GLFW.GLFW_KEY_BACKSPACE)) { page = Page.HOME; return; }
        switch (page) {
            case HOME -> { for (int i = 0; i < 8; i++) if (e(GLFW.GLFW_KEY_1 + i)) open(i); }
            case FLASH -> {
                if (e(GLFW.GLFW_KEY_LEFT)) flashMove(-1);
                if (e(GLFW.GLFW_KEY_RIGHT)) flashMove(1);
                if (e(GLFW.GLFW_KEY_UP) || e(GLFW.GLFW_KEY_DOWN) || enter()) fFlip = !fFlip;
                if (e(GLFW.GLFW_KEY_1)) flashMark(false);
                if (e(GLFW.GLFW_KEY_2)) flashMark(true);
            }
            case HANGUL -> {
                int n = Data.JAMO.size();
                if (e(GLFW.GLFW_KEY_LEFT)) selectJamo((jSel + n - 1) % n);
                if (e(GLFW.GLFW_KEY_RIGHT)) selectJamo((jSel + 1) % n);
                if (e(GLFW.GLFW_KEY_UP)) selectJamo(Math.max(0, jSel - 8));
                if (e(GLFW.GLFW_KEY_DOWN)) selectJamo(Math.min(n - 1, jSel + 8));
            }
            case QUIZ -> {
                for (int i = 0; i < 4; i++) if (e(GLFW.GLFW_KEY_1 + i)) answerQuiz(i);
                if (enter() && quiz != null && quiz.picked >= 0) nextQuiz();
            }
            case REVIEW -> { if (enter()) startReview(); }
            case DIALOGUE -> {
                for (int i = 0; i < 3; i++) if (e(GLFW.GLFW_KEY_1 + i)) answerDialogue(i);
                if (enter()) { if (dDone) startDialogue(); else if (dPicked >= 0) nextDialogue(); }
            }
            case ARENA -> {
                if (aState == ArenaState.PLAY) for (int i = 0; i < 4; i++) if (e(GLFW.GLFW_KEY_1 + i)) answerArena(i, now);
                if (aState == ArenaState.INTRO) {
                    for (int i = 0; i < 8; i++) if (e(GLFW.GLFW_KEY_1 + i)) aLevel = i;
                    if (e(GLFW.GLFW_KEY_LEFT)) aLevel = (aLevel + 7) % 8;
                    if (e(GLFW.GLFW_KEY_RIGHT)) aLevel = (aLevel + 1) % 8;
                }
                if (enter()) {
                    if (aState == ArenaState.INTRO || aState == ArenaState.END) startArena();
                    else if (aState == ArenaState.FEEDBACK) advanceArena(now);
                }
            }
            default -> { }
        }
    }

    private void open(int card) {
        Page target = CARD_PAGE[card];
        switch (target) {
            case FLASH -> rebuildFlash();
            case QUIZ -> { reviewMode = false; qCombo = qRight = qTotal = 0; nextQuiz(); }
            case DIALOGUE -> startDialogue();
            case ARENA -> aState = ArenaState.INTRO;
            default -> { }
        }
        page = target;
    }

    // ================================================================== primitives
    private void rect(int x, int y, int w, int h, int c) { gx.fill(x, y, x + w, y + h, c); }
    private void round(int x, int y, int w, int h, int c) { rect(x + 1, y, w - 2, h, c); rect(x, y + 1, w, h - 2, c); }
    private void box(int x, int y, int w, int h, int fill, int border) {
        round(x, y, w, h, border); round(x + 1, y + 1, w - 2, h - 2, fill);
    }
    private static int mix(int c, int to, float f) {
        int a = (c >>> 24) & 255;
        int r = Math.round(((c >> 16) & 255) * (1 - f) + ((to >> 16) & 255) * f);
        int g = Math.round(((c >> 8) & 255) * (1 - f) + ((to >> 8) & 255) * f);
        int b = Math.round((c & 255) * (1 - f) + (to & 255) * f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    private static int alpha(int c, int a) { return (a << 24) | (c & 0xFFFFFF); }
    private boolean over(int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }

    private void txt(String s, int x, int y, int c) { gx.text(this.font, s, x, y, c, false); }
    private int tw(String s) { return this.font.width(s); }
    private void ctr(String s, int cx, int y, int c) { txt(s, cx - tw(s) / 2, y, c); }
    private void right(String s, int rx, int y, int c) { txt(s, rx - tw(s), y, c); }
    private String fit(String s, int maxW) {
        if (tw(s) <= maxW) return s;
        while (s.length() > 1 && tw(s + "..") > maxW) s = s.substring(0, s.length() - 1);
        return s + "..";
    }
    /** Scaled text, horizontally centred at cx. Scale is reduced automatically to fit maxW. */
    private void big(String s, int cx, int y, float scale, int c, int maxW) {
        float w = tw(s) * scale;
        if (w > maxW) { scale *= maxW / w; w = maxW; }
        var m = gx.pose();
        m.pushMatrix();
        m.translate(cx - w / 2f, (float) y);
        m.scale(scale, scale);
        gx.text(this.font, s, 0, 0, c, true);
        m.popMatrix();
    }
    private void bar(int x, int y, int w, int h, float f, int color) {
        round(x, y, w, h, 0xFF0B1220);
        int fw = Math.round((w - 2) * Math.max(0, Math.min(1, f)));
        if (fw > 0) { rect(x + 1, y + 1, fw, h - 2, color); rect(x + 1, y + 1, fw, 1, mix(color, 0xFFFFFFFF, .35f)); }
    }
    private void heart(int x, int y, int c) {
        rect(x + 1, y, 2, 1, c); rect(x + 4, y, 2, 1, c); rect(x, y + 1, 7, 2, c);
        rect(x + 1, y + 3, 5, 1, c); rect(x + 2, y + 4, 3, 1, c); rect(x + 3, y + 5, 1, 1, c);
    }
    private void grass(int x, int y, int s) {
        rect(x, y, s, s, 0xFF8B5A2B);
        rect(x, y, s, s / 3, 0xFF5DAA3A);
        rect(x, y + s / 3, s, 2, 0xFF4A8C2E);
        int[][] dots = {{3, 12}, {9, 15}, {15, 11}, {20, 17}, {6, 20}, {13, 22}, {22, 23}, {18, 26}};
        for (int[] d : dots) if (d[0] < s - 2 && d[1] < s - 2) rect(x + d[0], y + d[1], 2, 2, 0xFF6B4423);
    }

    private void button(int x, int y, int w, int h, String label, int accent, boolean enabled, Runnable r) {
        boolean hv = enabled && over(x, y, w, h);
        box(x, y, w, h, hv ? mix(CARD, 0xFFFFFFFF, .12f) : CARD, hv ? accent : CARD_BORDER);
        if (enabled) rect(x + 3, y + h - 2, w - 6, 1, alpha(accent, hv ? 255 : 110));
        ctr(fit(label, w - 6), x + w / 2, y + (h - 8) / 2, enabled ? (hv ? 0xFFFFFFFF : TEXT) : 0xFF56637F);
        if (enabled && r != null) hits.add(new Hit(x, y, w, h, r));
    }

    // ================================================================== frame & header
    private void drawFrame() {
        rect(px + 3, py + 4, pw, ph, 0x55000000);
        box(px, py, pw, ph, BG, BORDER);
        int hh = page == Page.HOME ? 66 : 26;
        gx.fillGradient(px + 1, py + 1, px + pw - 1, py + hh, HEAD_TOP, HEAD_BOT);
        rect(px + 1, py + hh, pw - 2, 1, 0xFF3C5486);
        rect(px + 1, py + 1, pw - 2, 2, GOLD);
        if (page != Page.HOME) {
            button(px + 8, py + 6, 54, 16, "< Menu", GOLD, true, () -> page = Page.HOME);
            String title = switch (page) {
                case FLASH -> "Từ vựng"; case HANGUL -> "Bảng chữ Hangul";
                case QUIZ -> reviewMode ? "Ôn lỗi sai" : "Trắc nghiệm";
                case REVIEW -> "Ôn lỗi sai"; case DIALOGUE -> "Hội thoại với dân làng";
                case ARENA -> "Đấu trường"; case QUESTS -> "Nhiệm vụ & thành tựu"; default -> "Thống kê";
            };
            ctr(title, px + pw / 2, py + 9, GOLD);
            right("Lv " + p.level() + "  " + p.xp + " XP", px + pw - 10, py + 9, TEAL);
        }
    }

    private int cy0() { return py + 34; }

    // ================================================================== HOME
    private void drawHome() {
        grass(px + 14, py + 10, 28);
        txtBig("KoreanCraft", px + 50, py + 9, 2f, GOLD);
        txt("한국어 · Học tiếng Hàn trong Minecraft", px + 50, py + 28, DIM);
        int bx = px + pw - 62;
        box(bx, py + 9, 48, 26, 0xFF0F1A30, GOLD);
        ctr("CẤP", bx + 24, py + 12, DIM);
        ctr("" + p.level(), bx + 24, py + 22, GOLD);
        txt("Cấp " + p.level() + " · " + p.title(), px + 14, py + 41, TEXT);
        right(p.levelXp() + "/100 XP · tổng " + p.xp, px + pw - 14, py + 41, DIM);
        bar(px + 14, py + 52, pw - 28, 7, p.levelXp() / 100f, GOLD);

        int cw = (pw - 28 - 8) / 3, y = py + 74;
        chip(px + 14, y, cw, "Chuỗi " + p.streakDays + " ngày", ORANGE);
        chip(px + 14 + cw + 4, y, cw, "Hôm nay " + Math.min(10, p.dayCorrect) + "/10 câu", GREEN);
        chip(px + 14 + 2 * (cw + 4), y, cw, "Thuộc " + p.learned.size() + "/" + Data.WORDS.size() + " từ", TEAL);

        int gap = 8, card = (pw - 28 - 3 * gap) / 4;
        for (int i = 0; i < 8; i++) {
            int x = px + 14 + (i % 4) * (card + gap);
            int y0 = py + 98 + (i / 4) * 66;
            final int id = i;
            boolean hv = over(x, y0, card, 60);
            int yy = hv ? y0 - 1 : y0;
            int col = CARD_COLOR[i];
            box(x, yy, card, 60, hv ? mix(CARD, col, .18f) : CARD, hv ? col : CARD_BORDER);
            rect(x + 3, yy + 1, card - 6, 2, col);
            txt("" + (i + 1), x + 5, yy + 6, DIM);
            big(CARD_ICON[i], x + card / 2, yy + 8, 2f, col, card - 10);
            ctr(fit(CARD_NAME[i], card - 6), x + card / 2, yy + 32, TEXT);
            ctr(fit(cardDetail(i), card - 6), x + card / 2, yy + 44, DIM);
            hits.add(new Hit(x, y0, card, 60, () -> open(id)));
        }
        ctr("Phím 1-8 chọn nhanh · Shift+K đóng · ESC quay lại", px + pw / 2, py + ph - 14, 0xFF6C7C9C);
    }
    private void txtBig(String s, int x, int y, float sc, int c) {
        var m = gx.pose(); m.pushMatrix(); m.translate((float) x, (float) y); m.scale(sc, sc);
        gx.text(this.font, s, 0, 0, c, true); m.popMatrix();
    }
    private void chip(int x, int y, int w, String s, int c) {
        box(x, y, w, 16, 0xFF131E36, 0xFF2A3A5E);
        rect(x + 4, y + 5, 3, 6, c);
        txt(fit(s, w - 16), x + 11, y + 4, TEXT);
    }
    private String cardDetail(int i) {
        return switch (i) {
            case 0 -> Data.WORDS.size() + " từ";
            case 1 -> Data.JAMO.size() + " ký tự";
            case 2 -> "Hàn <-> Việt";
            case 3 -> p.weak().size() + " từ cần ôn";
            case 4 -> Data.DIALOGUES.size() + " tình huống";
            case 5 -> "TOPIK 1-6";
            case 6 -> countClaimable() > 0 ? countClaimable() + " thưởng mới!" : "4 nhiệm vụ";
            default -> p.accuracy() + "% chính xác";
        };
    }
    private int countClaimable() {
        int n = 0;
        for (int i = 0; i < Progress.QUEST_NAMES.length; i++) if (p.questDone(i) && !p.questClaimed(i)) n++;
        return n;
    }

    // ================================================================== FLASHCARDS
    private void rebuildFlash() {
        fList = new ArrayList<>();
        for (int i = 0; i < Data.WORDS.size(); i++) if (fCat < 0 || Data.WORDS.get(i).cat() == fCat) fList.add(i);
        if (fShuffle) Collections.shuffle(fList, rnd);
        fPos = 0; fFlip = false;
    }
    private void flashMove(int d) { if (fList.isEmpty()) return; fPos = (fPos + d + fList.size()) % fList.size(); fFlip = false; }
    private void flashMark(boolean known) {
        if (fList.isEmpty()) return;
        Data.Word w = Data.WORDS.get(fList.get(fPos));
        if (known) { if (p.markLearned(w.ko())) showToast("\u00a7a+3 XP · đã thuộc " + w.ko()); }
        else p.unmarkLearned(w.ko());
        flashMove(1);
    }
    private void cycleCat(int d) {
        int n = Data.CATS.length + 1;
        fCat = ((fCat + 1 + d + n) % n) - 1;
        rebuildFlash();
    }

    private void drawFlash() {
        int y = cy0(), x = px + 14, w = pw - 28;
        button(x, y, 18, 16, "<", GOLD, true, () -> cycleCat(-1));
        String cat = fCat < 0 ? "Tất cả" : Data.CATS[fCat];
        box(x + 22, y, 170, 16, 0xFF131E36, 0xFF2A3A5E);
        ctr("Chủ đề: " + cat + " (" + fList.size() + ")", x + 22 + 85, y + 4, TEXT);
        button(x + 196, y, 18, 16, ">", GOLD, true, () -> cycleCat(1));
        button(x + w - 96, y, 96, 16, "Xáo trộn: " + (fShuffle ? "Bật" : "Tắt"), TEAL, true, () -> { fShuffle = !fShuffle; rebuildFlash(); });

        int cardY = y + 22, cardH = 116;
        if (fList.isEmpty()) { ctr("Không có từ nào.", px + pw / 2, cardY + 50, DIM); return; }
        Data.Word wd = Data.WORDS.get(fList.get(fPos));
        int col = Data.CAT_COLORS[wd.cat()];
        boolean hv = over(x, cardY, w, cardH);
        box(x, cardY, w, cardH, hv ? mix(CARD, col, .10f) : CARD, hv ? col : CARD_BORDER);
        rect(x + 4, cardY + 1, w - 8, 2, col);
        hits.add(new Hit(x, cardY, w, cardH, () -> fFlip = !fFlip));
        txt(Data.CATS_SHORT[wd.cat()], x + 8, cardY + 8, col);
        if (p.learned.contains(wd.ko())) right("Đã thuộc", x + w - 8, cardY + 8, GREEN);
        int cx = x + w / 2;
        if (!fFlip) {
            big(wd.ko(), cx, cardY + 34, 4f, TEXT, w - 40);
            ctr("Chạm vào thẻ hoặc nhấn Space để lật", cx, cardY + cardH - 16, DIM);
        } else {
            big(wd.ko(), cx, cardY + 20, 2f, TEXT, w - 40);
            ctr("[ " + wd.roman() + " ]", cx, cardY + 44, TEAL);
            big(wd.vi(), cx, cardY + 64, 2.5f, GOLD, w - 40);
            ctr("1: chưa thuộc · 2: đã thuộc · ←/→ chuyển thẻ", cx, cardY + cardH - 16, DIM);
        }

        int by = cardY + cardH + 8;
        button(x, by, 70, 20, "< Trước", GOLD, true, () -> flashMove(-1));
        button(x + 78, by, 96, 20, "Chưa thuộc (1)", ROSE, true, () -> flashMark(false));
        button(x + w - 174, by, 96, 20, "Đã thuộc (2)", GREEN, true, () -> flashMark(true));
        button(x + w - 70, by, 70, 20, "Sau >", GOLD, true, () -> flashMove(1));

        int learnedHere = 0;
        for (int i : fList) if (p.learned.contains(Data.WORDS.get(i).ko())) learnedHere++;
        txt("Thẻ " + (fPos + 1) + "/" + fList.size() + " · Đã thuộc " + learnedHere + "/" + fList.size(), x, by + 27, DIM);
        bar(x, by + 38, w, 5, fList.isEmpty() ? 0 : learnedHere / (float) fList.size(), TEAL);
    }

    // ================================================================== HANGUL
    private void selectJamo(int i) {
        jSel = i;
        if (p.seeJamo(Data.JAMO.get(i).ch())) showToast("\u00a7a+1 XP · " + p.jamoSeen.size() + "/" + Data.JAMO.size() + " ký tự");
    }
    private void drawHangul() {
        int y = cy0(), tile = 40, gap = 4;
        int x0 = px + (pw - (8 * tile + 7 * gap)) / 2;
        for (int i = 0; i < Data.JAMO.size(); i++) {
            Data.Jamo j = Data.JAMO.get(i);
            int x = x0 + (i % 8) * (tile + gap), yy = y + (i / 8) * (34 + gap);
            int col = j.vowel() ? ROSE : TEAL;
            boolean sel = i == jSel, hv = over(x, yy, tile, 34);
            box(x, yy, tile, 34, sel ? mix(CARD, col, .30f) : hv ? mix(CARD, col, .15f) : CARD, sel || hv ? col : CARD_BORDER);
            if (p.jamoSeen.contains(j.ch())) rect(x + tile - 7, yy + 3, 4, 4, GREEN);
            big(j.ch(), x + tile / 2, yy + 9, 2f, sel ? 0xFFFFFFFF : TEXT, tile - 8);
            final int id = i;
            hits.add(new Hit(x, yy, tile, 34, () -> selectJamo(id)));
        }
        Data.Jamo j = Data.JAMO.get(jSel);
        int col = j.vowel() ? ROSE : TEAL;
        int dy = y + 3 * (34 + gap) + 4, dx = px + 14, dw = pw - 28, dh = 88;
        box(dx, dy, dw, dh, CARD, CARD_BORDER);
        rect(dx + 4, dy + 1, dw - 8, 2, col);
        box(dx + 8, dy + 8, 64, 72, 0xFF0F1A30, col);
        big(j.ch(), dx + 40, dy + 28, 4f, col, 56);
        int tx = dx + 84;
        txt(j.vowel() ? "Nguyên âm" : "Phụ âm", tx, dy + 10, col);
        txt("Cách đọc gần đúng: ", tx, dy + 24, DIM);
        txt(fit(j.sound(), dw - 84 - 100 - 8), tx + tw("Cách đọc gần đúng: "), dy + 24, GOLD);
        txt("Ví dụ:", tx, dy + 40, DIM);
        txt(j.exKo() + "  [" + j.exRoman() + "]", tx + 40, dy + 40, TEXT);
        txt("Nghĩa: " + j.exVi(), tx, dy + 54, TEAL);
        txt("Đã xem " + p.jamoSeen.size() + "/" + Data.JAMO.size() + " · dùng phím mũi tên để duyệt", tx, dy + 70, 0xFF6C7C9C);
    }

    // ================================================================== QUIZ
    private Data.Word pickWord(List<Data.Word> pool) {
        int total = 0;
        for (Data.Word w : pool) total += p.weight(w);
        int r = rnd.nextInt(Math.max(1, total));
        for (Data.Word w : pool) { r -= p.weight(w); if (r < 0) return w; }
        return pool.get(0);
    }
    private Question makeQuestion(List<Data.Word> pool, List<Data.Word> distractors, int mode) {
        Question q = new Question();
        q.word = pickWord(pool);
        q.koToVi = mode == 0 || (mode == 2 && rnd.nextBoolean());
        List<String> options = new ArrayList<>();
        String right = q.answerText();
        List<Data.Word> others = new ArrayList<>(distractors);
        Collections.shuffle(others, rnd);
        // prefer distractors from the same category so the quiz is not trivial
        others.sort((a, b) -> Boolean.compare(b.cat() == q.word.cat(), a.cat() == q.word.cat()));
        List<Data.Word> head = new ArrayList<>(others.subList(0, Math.min(6, others.size())));
        List<Data.Word> tail = new ArrayList<>(others.subList(Math.min(6, others.size()), others.size()));
        Collections.shuffle(head, rnd); head.addAll(tail);
        for (Data.Word w : head) {
            String s = q.koToVi ? w.vi() : w.ko();
            if (!s.equals(right) && !options.contains(s)) options.add(s);
            if (options.size() == 3) break;
        }
        q.correct = rnd.nextInt(4);
        int k = 0;
        for (int i = 0; i < 4; i++) q.choices[i] = i == q.correct ? right : options.get(k++);
        return q;
    }
    private void nextQuiz() {
        List<Data.Word> pool = Data.WORDS;
        if (reviewMode) {
            pool = p.weak();
            if (pool.isEmpty()) { page = Page.REVIEW; showToast("\u00a7aĐã ôn xong mọi từ yếu!"); quiz = null; return; }
        }
        quiz = makeQuestion(pool, reviewMode ? Data.ALL : Data.WORDS, qMode);
    }
    private void answerQuiz(int i) {
        if (quiz == null || quiz.picked >= 0) return;
        quiz.picked = i;
        boolean ok = i == quiz.correct;
        p.recordAnswer(quiz.word.ko(), ok);
        qTotal++;
        if (ok) {
            qRight++; qCombo++; p.noteCombo(qCombo);
            int pts = 10 + Math.min(qCombo - 1, 5) * 2;
            p.gain(pts);
            showToast("\u00a7a+" + pts + " XP" + (qCombo > 1 ? " · chuỗi x" + qCombo : ""));
        } else { qCombo = 0; showToast("\u00a7cChưa đúng"); }
    }

    private void drawQuestionCard(Question q, int y, boolean reveal) {
        int x = px + 14, w = pw - 28, h = 62;
        box(x, y, w, h, CARD, CARD_BORDER);
        rect(x + 4, y + 1, w - 8, 2, q.koToVi ? GOLD : BLUE);
        ctr(q.koToVi ? "Nghĩa của từ này là gì?" : "Từ tiếng Hàn nào có nghĩa là:", x + w / 2, y + 7, DIM);
        big(q.koToVi ? q.word.ko() : q.word.vi(), x + w / 2, y + 22, 2.5f, TEXT, w - 30);
        if (reveal) ctr("[ " + q.word.roman() + " ]   " + q.word.ko() + " = " + q.word.vi(), x + w / 2, y + h - 12, TEAL);
    }
    private void drawOptions(Question q, int y, boolean locked, java.util.function.IntConsumer pick) {
        int x = px + 14, w = (pw - 28 - 8) / 2, h = 28;
        for (int i = 0; i < 4; i++) {
            int bx = x + (i % 2) * (w + 8), by = y + (i / 2) * (h + 6);
            boolean answered = q.picked != -1;
            boolean isRight = answered && i == q.correct, isWrong = answered && i == q.picked && i != q.correct;
            boolean hv = !locked && !answered && over(bx, by, w, h);
            int fill = isRight ? 0xFF14532D : isWrong ? 0xFF7F1D1D : hv ? mix(CARD, 0xFFFFFFFF, .12f) : CARD;
            int border = isRight ? GREEN : isWrong ? ROSE : hv ? GOLD : CARD_BORDER;
            box(bx, by, w, h, fill, border);
            box(bx + 4, by + 7, 14, 14, 0xFF0F1A30, border);
            ctr("" + (i + 1), bx + 11, by + 10, border == CARD_BORDER ? DIM : border);
            ctr(fit(q.choices[i], w - 30), bx + 22 + (w - 22) / 2, by + 10, TEXT);
            if (!locked && !answered) { final int id = i; hits.add(new Hit(bx, by, w, h, () -> pick.accept(id))); }
        }
    }

    private void drawQuiz() {
        int y = cy0(), x = px + 14, w = pw - 28;
        String[] modes = {"Hàn -> Việt", "Việt -> Hàn", "Trộn"};
        button(x, y, 116, 16, "Hướng: " + modes[qMode], GOLD, true, () -> { qMode = (qMode + 1) % 3; if (quiz != null && quiz.picked < 0) nextQuiz(); });
        right("Đúng " + qRight + "/" + qTotal + " · Chuỗi x" + qCombo, x + w, y + 4, qCombo >= 3 ? ORANGE : DIM);
        if (quiz == null) { nextQuiz(); if (quiz == null) return; }
        drawQuestionCard(quiz, y + 22, quiz.picked >= 0);
        drawOptions(quiz, y + 22 + 62 + 8, false, this::answerQuiz);
        int fy = y + 22 + 62 + 8 + 2 * 34 + 2;
        if (quiz.picked >= 0) {
            boolean ok = quiz.picked == quiz.correct;
            txt(ok ? "Chính xác!" : "Đáp án đúng: " + fit(quiz.answerText(), 140), x, fy + 5, ok ? GREEN : ROSE);
            button(x + w - 130, fy, 130, 20, "Tiếp tục (Enter)", GOLD, true, this::nextQuiz);
        } else {
            txt("Chọn đáp án bằng chuột hoặc phím 1-4", x, fy + 5, 0xFF6C7C9C);
        }
        bar(x, py + ph - 10, w, 4, qTotal == 0 ? 0 : qRight / (float) qTotal, GREEN);
    }

    // ================================================================== REVIEW
    private void startReview() {
        if (p.weak().isEmpty()) return;
        reviewMode = true; qCombo = qRight = qTotal = 0;
        page = Page.QUIZ; nextQuiz();
    }
    private void drawReview() {
        int y = cy0(), x = px + 14, w = pw - 28;
        List<Data.Word> weak = p.weak();
        if (weak.isEmpty()) {
            box(x, y + 10, w, 110, CARD, CARD_BORDER);
            big("잘했어요!", x + w / 2, y + 28, 3f, GREEN, w - 20);
            ctr("Chưa có từ nào cần ôn.", x + w / 2, y + 66, TEXT);
            ctr("Làm Trắc nghiệm hoặc Đấu trường để hệ thống ghi nhận từ bạn hay nhầm.", x + w / 2, y + 80, DIM);
            button(x + w / 2 - 70, y + 130, 140, 20, "Đến Trắc nghiệm", BLUE, true, () -> open(2));
            return;
        }
        txt("Từ bạn hay nhầm nhất (" + weak.size() + "):", x, y + 2, GOLD);
        int rows = Math.min(8, weak.size());
        for (int i = 0; i < rows; i++) {
            Data.Word wd = weak.get(i); int[] s = p.stats.get(wd.ko());
            int ry = y + 16 + i * 17;
            box(x, ry, w, 15, i % 2 == 0 ? 0xFF16213A : CARD, 0xFF26365A);
            txt(fit(wd.ko(), 70), x + 6, ry + 4, TEXT);
            txt(fit(wd.roman(), 80), x + 80, ry + 4, TEAL);
            txt(fit(wd.vi(), 110), x + 166, ry + 4, GOLD);
            right("sai " + s[1] + " · đúng " + s[0], x + w - 6, ry + 4, ROSE);
        }
        if (weak.size() > rows) txt("... và " + (weak.size() - rows) + " từ khác", x, y + 16 + rows * 17 + 2, DIM);
        button(x + w / 2 - 80, py + ph - 32, 160, 20, "Bắt đầu ôn tập (Enter)", ROSE, true, this::startReview);
    }

    // ================================================================== DIALOGUE
    private void startDialogue() {
        dOrder = new ArrayList<>();
        for (int i = 0; i < Data.DIALOGUES.size(); i++) dOrder.add(i);
        Collections.shuffle(dOrder, rnd);
        dPos = 0; dRight = 0; dDone = false;
        buildDialogue();
    }
    private void buildDialogue() {
        Data.Dialogue d = Data.DIALOGUES.get(dOrder.get(dPos));
        List<String[]> opts = new ArrayList<>();
        opts.add(new String[]{d.right(), d.rightVi(), "1"});
        opts.add(new String[]{d.wrongA(), d.wrongAVi(), "0"});
        opts.add(new String[]{d.wrongB(), d.wrongBVi(), "0"});
        Collections.shuffle(opts, rnd);
        for (int i = 0; i < 3; i++) dOpts[i] = opts.get(i);
        dPicked = -1;
    }
    private void answerDialogue(int i) {
        if (dDone || dPicked >= 0) return;
        dPicked = i;
        if ("1".equals(dOpts[i][2])) {
            dRight++;
            int pts = p.dialogueCorrect(dOrder.get(dPos));
            showToast("\u00a7a+" + pts + " XP");
        } else showToast("\u00a7cChưa phù hợp");
    }
    private void nextDialogue() {
        if (dPos + 1 >= dOrder.size()) { dDone = true; p.gain(dRight == dOrder.size() ? 15 : 0); return; }
        dPos++; buildDialogue();
    }
    private void villager(int x, int y) {
        rect(x, y, 30, 34, 0xFFC8946A);
        rect(x, y, 30, 5, 0xFF8A5A3A);
        rect(x + 4, y + 12, 8, 4, 0xFFFFFFFF); rect(x + 18, y + 12, 8, 4, 0xFFFFFFFF);
        rect(x + 7, y + 12, 4, 4, 0xFF3B8B3B); rect(x + 19, y + 12, 4, 4, 0xFF3B8B3B);
        rect(x + 3, y + 9, 10, 2, 0xFF4A3426); rect(x + 17, y + 9, 10, 2, 0xFF4A3426);
        rect(x + 12, y + 16, 6, 12, 0xFFA8764F);
        rect(x + 8, y + 30, 14, 2, 0xFF8A5A3A);
    }
    private void drawDialogue() {
        int y = cy0(), x = px + 14, w = pw - 28;
        if (dOrder.isEmpty()) startDialogue();
        if (dDone) {
            box(x, y + 8, w, 120, CARD, CARD_BORDER);
            big("완료!", x + w / 2, y + 22, 3f, GOLD, w - 20);
            ctr("Bạn trả lời đúng " + dRight + "/" + dOrder.size() + " tình huống", x + w / 2, y + 62, TEXT);
            ctr(dRight == dOrder.size() ? "Hoàn hảo! +15 XP thưởng" : "Thử lại để đạt điểm tuyệt đối nhé.", x + w / 2, y + 78, dRight == dOrder.size() ? GREEN : DIM);
            button(x + w / 2 - 60, y + 140, 120, 20, "Chơi lại (Enter)", GOLD, true, this::startDialogue);
            return;
        }
        Data.Dialogue d = Data.DIALOGUES.get(dOrder.get(dPos));
        villager(x + 2, y + 6);
        txt("Dân làng", x + 2, y + 44, DIM);
        int bx = x + 44, bw = w - 44;
        box(bx, y + 2, bw, 54, CARD, GOLD);
        rect(bx - 3, y + 16, 4, 6, GOLD);
        big(d.npc(), bx + bw / 2, y + 10, 2f, TEXT, bw - 20);
        ctr(d.npcVi(), bx + bw / 2, y + 38, DIM);
        String fb = "Chọn câu trả lời phù hợp (1-3):"; int fc = 0xFF6C7C9C;
        if (dPicked >= 0) {
            boolean ok = "1".equals(dOpts[dPicked][2]);
            fb = ok ? "잘했어요! Giỏi lắm!" : "다시 해 봐요. Chưa phù hợp, thử đáp án xanh nhé.";
            fc = ok ? GREEN : ROSE;
        }
        txt(fb, x, y + 62, fc);
        for (int i = 0; i < 3; i++) {
            int by = y + 76 + i * 34;
            boolean good = "1".equals(dOpts[i][2]);
            boolean answered = dPicked >= 0, isPick = i == dPicked;
            boolean hv = !answered && over(x, by, w, 30);
            int border = answered ? (good ? GREEN : isPick ? ROSE : CARD_BORDER) : hv ? GOLD : CARD_BORDER;
            int fill = answered ? (good ? 0xFF14532D : isPick ? 0xFF7F1D1D : CARD) : hv ? mix(CARD, 0xFFFFFFFF, .12f) : CARD;
            box(x, by, w, 30, fill, border);
            box(x + 5, by + 8, 14, 14, 0xFF0F1A30, border);
            ctr("" + (i + 1), x + 12, by + 11, border == CARD_BORDER ? DIM : border);
            txt(fit(dOpts[i][0], w - 36), x + 26, by + 5, TEXT);
            txt(fit(dOpts[i][1], w - 36), x + 26, by + 17, DIM);
            if (!answered) { final int id = i; hits.add(new Hit(x, by, w, 30, () -> answerDialogue(id))); }
        }
        for (int i = 0; i < dOrder.size(); i++)
            rect(x + i * 10, py + ph - 14, 7, 5, i < dPos ? GREEN : i == dPos ? GOLD : 0xFF2A3A5E);
        if (dPicked >= 0) button(x + w - 120, py + ph - 28, 120, 18, "Tiếp tục (Enter)", GOLD, true, this::nextDialogue);
    }

    // ================================================================== ARENA
    private int secs() { return aLevel >= 5 ? 15 : 12; }

    private void startArena() {
        aState = ArenaState.PLAY; aQ = 0; aHearts = 3; aScore = 0; aCombo = 0; aRight = 0; aMistakes = 0; aWon = false;
        newArenaQuestion(System.currentTimeMillis());
    }
    private void newArenaQuestion(long now) {
        List<Data.Word> pool = Data.arenaPool(aLevel);
        arena = makeQuestion(pool, pool, 2);
        aStart = now; aState = ArenaState.PLAY;
    }
    private void answerArena(int i, long now) {
        if (aState != ArenaState.PLAY || arena == null) return;
        arena.picked = i;
        boolean ok = i == arena.correct;
        p.recordAnswer(arena.word.ko(), ok);
        if (ok) {
            aRight++; aCombo++; p.noteCombo(aCombo);
            int left = (int) Math.max(0, (secs() * 1000L - (now - aStart)) / 1000);
            int pts = 10 + Math.min(aCombo - 1, 5) * 2 + left / 3 + (aLevel >= 7 ? 8 : aLevel * 2);
            aScore += pts; p.gain(pts);
        } else { aCombo = 0; aMistakes++; aHearts--; }
        aState = ArenaState.FEEDBACK; aFeedbackUntil = now + 1300;
    }
    private void tickArena(long now) {
        if (page != Page.ARENA) return;
        if (aState == ArenaState.PLAY && arena != null && now - aStart >= secs() * 1000L) {
            arena.picked = -2; aCombo = 0; aMistakes++; aHearts--;
            p.recordAnswer(arena.word.ko(), false);
            aState = ArenaState.FEEDBACK; aFeedbackUntil = now + 1500;
        } else if (aState == ArenaState.FEEDBACK && now >= aFeedbackUntil) advanceArena(now);
    }
    private void advanceArena(long now) {
        aQ++;
        if (aHearts <= 0 || aQ >= A_TOTAL) {
            aWon = aHearts > 0;
            boolean perfect = aWon && aMistakes == 0;
            p.arenaFinished(aWon, perfect, aLevel);
            aState = ArenaState.END;
        } else newArenaQuestion(now);
    }

    private void drawArena(long now) {
        int y = cy0(), x = px + 14, w = pw - 28;
        if (aState == ArenaState.INTRO) {
            box(x, y, w, 198, CARD, CARD_BORDER);
            rect(x + 4, y + 1, w - 8, 2, ORANGE);
            big("전투!", x + w / 2, y + 6, 2.2f, ORANGE, w - 20);
            ctr("Chọn cấp độ (chuột, phím 1-8 hoặc ←/→)", x + w / 2, y + 28, DIM);
            int bw = (w - 12 - 3 * 4) / 4;
            for (int i = 0; i < 8; i++) {
                int bx = x + 6 + (i % 4) * (bw + 4), by = y + 40 + (i / 4) * 26;
                int col = i == 0 ? GOLD : i == 7 ? 0xFFA3B1D6 : Data.CAT_COLORS[i - 1];
                boolean sel = i == aLevel, hv = over(bx, by, bw, 22);
                box(bx, by, bw, 22, sel ? mix(CARD, col, .35f) : hv ? mix(CARD, col, .15f) : CARD, sel || hv ? col : CARD_BORDER);
                ctr(Data.levelName(i), bx + bw / 2, by + 7, sel ? 0xFFFFFFFF : TEXT);
                final int id = i;
                hits.add(new Hit(bx, by, bw, 22, () -> aLevel = id));
            }
            ctr(Data.TOPIK_DESC[aLevel], x + w / 2, y + 98, TEAL);
            ctr(fit(Data.arenaPool(aLevel).size() + " từ · " + A_TOTAL + " câu · " + secs() + " giây/câu · 3 tim", w - 20), x + w / 2, y + 112, DIM);
            ctr("Sai hoặc hết giờ mất 1 tim. Đúng liên tiếp và trả lời nhanh được thêm điểm.", x + w / 2, y + 126, DIM);
            ctr("Thắng +" + Progress.arenaXp(true, false, aLevel) + " XP · không sai câu nào +" + Progress.arenaXp(true, true, aLevel) + " XP", x + w / 2, y + 140, GOLD);
            int won = aLevel >= 1 ? p.topikWins[aLevel] : p.wins;
            ctr((aLevel >= 1 ? "Đã thắng cấp này: " : "Tổng số trận thắng: ") + won, x + w / 2, y + 154, 0xFF6C7C9C);
            button(x + w / 2 - 70, y + 170, 140, 22, "Bắt đầu (Enter)", ORANGE, true, this::startArena);
            return;
        }
        if (aState == ArenaState.END) {
            box(x, y + 4, w, 150, CARD, aWon ? GREEN : ROSE);
            big(aWon ? "승리!" : "패배", x + w / 2, y + 14, 3.5f, aWon ? GREEN : ROSE, w - 20);
            ctr(aWon ? (aMistakes == 0 ? "Hoàn hảo! Không sai câu nào" : "Bạn đã chiến thắng!") : "Hết tim rồi, thử lại nhé!", x + w / 2, y + 56, TEXT);
            ctr("Đúng " + aRight + "/" + A_TOTAL + " · Điểm " + aScore + " · Tim còn " + Math.max(0, aHearts), x + w / 2, y + 74, DIM);
            ctr("+" + Progress.arenaXp(aWon, aWon && aMistakes == 0, aLevel) + " XP " + (aWon ? "thưởng" : "an ủi") + " · " + Data.levelName(aLevel), x + w / 2, y + 90, GOLD);
            for (int i = 0; i < 3; i++) heart(x + w / 2 - 30 + i * 22, y + 108, i < aHearts ? ROSE : 0xFF3A2A38);
            button(x + w / 2 - 70, y + 164, 140, 22, "Chơi lại (Enter)", ORANGE, true, this::startArena);
            return;
        }
        for (int i = 0; i < 3; i++) heart(x + i * 10, y + 2, i < aHearts ? ROSE : 0xFF3A2A38);
        ctr(Data.levelName(aLevel) + " · Câu " + Math.min(A_TOTAL, aQ + 1) + "/" + A_TOTAL, x + w / 2, y + 2, TEXT);
        right("Điểm " + aScore + (aCombo > 1 ? " · x" + aCombo : ""), x + w, y + 2, aCombo > 1 ? ORANGE : GOLD);
        float frac = aState == ArenaState.PLAY ? 1f - (now - aStart) / (secs() * 1000f) : 0f;
        int tc = frac > .5f ? GREEN : frac > .25f ? GOLD : ROSE;
        bar(x, y + 14, w, 7, Math.max(0, frac), tc);
        boolean reveal = aState == ArenaState.FEEDBACK;
        drawQuestionCard(arena, y + 28, reveal);
        drawOptions(arena, y + 28 + 62 + 8, reveal, i -> answerArena(i, System.currentTimeMillis()));
        int fy = y + 28 + 62 + 8 + 2 * 34 + 2;
        if (reveal) {
            String msg = arena.picked == -2 ? "Hết giờ!" : arena.picked == arena.correct ? "Chính xác!" : "Sai rồi!";
            txt(msg, x, fy + 5, arena.picked == arena.correct ? GREEN : ROSE);
            button(x + w - 130, fy, 130, 20, "Tiếp tục (Enter)", ORANGE, true, () -> advanceArena(System.currentTimeMillis()));
        } else txt("Nhanh lên! Phím 1-4 để trả lời", x, fy + 5, 0xFF6C7C9C);
    }

    // ================================================================== QUESTS
    private void drawQuests() {
        int y = cy0(), x = px + 14, w = pw - 28;
        txt("Nhiệm vụ hôm nay", x, y, GOLD);
        for (int i = 0; i < Progress.QUEST_NAMES.length; i++) {
            int ry = y + 12 + i * 22;
            boolean done = p.questDone(i), claimed = p.questClaimed(i);
            box(x, ry, w, 19, CARD, done && !claimed ? GREEN : CARD_BORDER);
            txt(Progress.QUEST_NAMES[i], x + 6, ry + 5, claimed ? DIM : TEXT);
            bar(x + 150, ry + 6, 90, 7, p.questProgress(i) / (float) Progress.QUEST_TARGET[i], claimed ? 0xFF4B5C7D : GREEN);
            txt(p.questProgress(i) + "/" + Progress.QUEST_TARGET[i], x + 246, ry + 5, DIM);
            final int id = i;
            if (claimed) right("Đã nhận", x + w - 6, ry + 5, DIM);
            else button(x + w - 62, ry + 2, 58, 15, done ? "+" + Progress.QUEST_REWARD[i] + " XP" : "+" + Progress.QUEST_REWARD[i], GREEN, done, () -> p.claimQuest(id));
        }
        int ay = y + 12 + 4 * 22 + 4;
        txt("Thành tựu (" + p.unlocked.size() + "/" + Progress.ACHIEVEMENTS.length + ")", x, ay, GOLD);
        int cw = (w - 6) / 2; hoverAch = -1;
        for (int i = 0; i < Progress.ACHIEVEMENTS.length; i++) {
            Progress.Achievement a = Progress.ACHIEVEMENTS[i];
            int bx = x + (i % 2) * (cw + 6), by = ay + 12 + (i / 2) * 13;
            boolean on = p.unlocked.contains(a.id());
            if (over(bx, by, cw, 12)) hoverAch = i;
            box(bx, by, cw, 12, on ? 0xFF2A2A14 : 0xFF131E36, on ? GOLD : 0xFF2A3A5E);
            rect(bx + 4, by + 3, 6, 6, on ? GOLD : 0xFF3A4A6A);
            txt(fit(a.name(), cw - 20), bx + 14, by + 2, on ? TEXT : 0xFF6C7C9C);
        }
        String hint = hoverAch >= 0 ? Progress.ACHIEVEMENTS[hoverAch].desc() : "Di chuột vào một thành tựu để xem điều kiện.";
        txt(fit(hint, w), x, py + ph - 13, hoverAch >= 0 ? TEAL : 0xFF6C7C9C);
    }

    // ================================================================== STATS
    private void drawStats(long now) {
        int y = cy0(), x = px + 14, w = pw - 28;
        String[][] tiles = {
            {"" + p.xp, "Tổng XP"}, {"" + p.level(), "Cấp độ"}, {p.streakDays + " ngày", "Chuỗi học"}, {p.accuracy() + "%", "Chính xác"},
            {"" + p.answered, "Câu đúng"}, {p.learned.size() + "/" + Data.WORDS.size(), "Từ đã thuộc"}, {"" + p.wins, "Thắng đấu trường"}, {"x" + p.bestCombo, "Chuỗi cao nhất"}};
        int[] tc = {GOLD, GOLD, ORANGE, GREEN, TEAL, TEAL, ROSE, PURPLE};
        int tileW = (w - 3 * 6) / 4;
        for (int i = 0; i < 8; i++) {
            int bx = x + (i % 4) * (tileW + 6), by = y + (i / 4) * 38;
            box(bx, by, tileW, 34, CARD, CARD_BORDER);
            rect(bx + 3, by + 1, tileW - 6, 2, tc[i]);
            ctr(fit(tiles[i][0], tileW - 6), bx + tileW / 2, by + 9, tc[i]);
            ctr(fit(tiles[i][1], tileW - 4), bx + tileW / 2, by + 21, DIM);
        }
        int cy = y + 82;
        txt("Mức thành thạo theo chủ đề", x, cy, GOLD);
        for (int c = 0; c < Data.CATS.length; c++) {
            int total = 0, known = 0;
            for (Data.Word wd : Data.WORDS) if (wd.cat() == c) { total++; if (p.learned.contains(wd.ko())) known++; }
            int ry = cy + 13 + c * 14;
            txt(Data.CATS_SHORT[c], x, ry, TEXT);
            bar(x + 80, ry + 1, w - 80 - 44, 7, total == 0 ? 0 : known / (float) total, Data.CAT_COLORS[c]);
            right(known + "/" + total, x + w, ry, DIM);
        }
        boolean armed = now < resetArmedUntil;
        button(x + w - 130, py + ph - 24, 130, 16, armed ? "Bấm lại để xác nhận!" : "Đặt lại tiến độ", ROSE, true, () -> {
            if (System.currentTimeMillis() < resetArmedUntil) { p.reset(); resetArmedUntil = 0; showToast("\u00a7cĐã đặt lại tiến độ"); }
            else resetArmedUntil = System.currentTimeMillis() + 3000;
        });
    }

    // ================================================================== toast
    private void drawToast(long now) {
        if (now >= toastUntil || toast.isEmpty()) return;
        int w = tw(toast) + 20, x = px + (pw - w) / 2, y = py + ph - 44;
        long left = toastUntil - now;
        int a = left < 300 ? (int) (255 * left / 300f) : 255;
        round(x, y, w, 16, alpha(0xFF0B1220, Math.max(8, a)));
        round(x, y, w, 1, alpha(toastColor, a));
        if (a > 40) txt(toast, x + 10, y + 4, alpha(toastColor, a));
    }
}
