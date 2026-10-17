package com.zhwii.koreancraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Persistent learner state: XP, streaks, per-word statistics, quests and achievements. */
final class Progress {
    record Achievement(String id, String name, String desc, Predicate<Progress> test) {}

    static final Achievement[] ACHIEVEMENTS = {
        new Achievement("first_word", "Từ đầu tiên", "Đánh dấu 1 từ là đã thuộc", p -> p.learned.size() >= 1),
        new Achievement("ten_words", "Chăm chỉ", "Thuộc 10 từ", p -> p.learned.size() >= 10),
        new Achievement("fifty_words", "Vốn từ giàu có", "Thuộc 50 từ", p -> p.learned.size() >= 50),
        new Achievement("hangul", "Mọt Hangul", "Xem đủ bảng chữ cái", p -> p.jamoSeen.size() >= Data.JAMO.size()),
        new Achievement("quiz10", "Khởi động", "Trả lời đúng 10 câu", p -> p.answered >= 10),
        new Achievement("quiz100", "Cao thủ quiz", "Trả lời đúng 100 câu", p -> p.answered >= 100),
        new Achievement("combo5", "Chuỗi 5", "Đúng liên tiếp 5 câu", p -> p.bestCombo >= 5),
        new Achievement("arena", "Đấu sĩ", "Thắng đấu trường 1 lần", p -> p.wins >= 1),
        new Achievement("perfect", "Hoàn hảo", "Thắng đấu trường không sai câu nào", p -> p.perfectWins >= 1),
        new Achievement("talk", "Người giao tiếp", "Hoàn thành mọi hội thoại", p -> p.dialoguesDone.size() >= Data.DIALOGUES.size()),
        new Achievement("streak3", "Bền bỉ", "Học 3 ngày liên tiếp", p -> p.streakDays >= 3),
        new Achievement("lv5", "Cấp 5", "Đạt cấp độ 5", p -> p.level() >= 5),
        new Achievement("topik_win", "Thí sinh TOPIK", "Thắng đấu trường TOPIK bất kỳ", p -> p.topikWinsTotal() >= 1),
        new Achievement("topik_master", "Chinh phục TOPIK", "Thắng cả 6 cấp TOPIK 1-6", p -> p.topikAllLevels()),
    };

    static final String[] QUEST_NAMES = {
        "Thuộc 5 từ mới", "Trả lời đúng 10 câu", "Đáp đúng 3 hội thoại", "Thắng 1 đấu trường"
    };
    static final int[] QUEST_TARGET = {5, 10, 3, 1};
    static final int[] QUEST_REWARD = {20, 30, 20, 40};

    int xp, answered, wrong, wins, perfectWins, bestCombo, streakDays;
    final int[] topikWins = new int[8];   // index 1-6 = TOPIK level, 7 = mixed
    String lastDay = "", dayKey = "";
    int dayLearned, dayCorrect, dayDialogue, dayWins;
    final Set<String> learned = new LinkedHashSet<>();
    final Set<String> jamoSeen = new LinkedHashSet<>();
    final Set<String> dialoguesDone = new LinkedHashSet<>();
    final Set<String> unlocked = new LinkedHashSet<>();
    final Set<String> claimed = new LinkedHashSet<>();
    final Map<String, int[]> stats = new HashMap<>();   // ko -> {right, wrong}

    /** Receives short status messages (may contain legacy section-sign colour codes). */
    Consumer<String> notifier = s -> {};
    private final Path path;

    Progress(Path path) { this.path = path; load(); }

    // ---------------------------------------------------------------- derived values
    int level() { return xp / 100 + 1; }
    int levelXp() { return xp % 100; }
    String title() {
        int l = level();
        if (l < 3) return "Tân binh";
        if (l < 6) return "Học viên";
        if (l < 10) return "Thợ mỏ chữ Hàn";
        if (l < 15) return "Chiến binh Hangul";
        return "Bậc thầy 한국어";
    }
    int topikWinsTotal() { int n = 0; for (int i = 1; i <= 7; i++) n += topikWins[i]; return n; }
    boolean topikAllLevels() { for (int i = 1; i <= 6; i++) if (topikWins[i] < 1) return false; return true; }
    int accuracy() { int t = answered + wrong; return t == 0 ? 0 : Math.round(answered * 100f / t); }

    int[] stat(String ko) { return stats.computeIfAbsent(ko, k -> new int[2]); }

    /** Words answered wrong more often than right. */
    List<Data.Word> weak() {
        List<Data.Word> out = new ArrayList<>();
        for (Data.Word w : Data.ALL) {
            int[] s = stats.get(w.ko());
            if (s != null && s[1] > s[0]) out.add(w);
        }
        out.sort((a, b) -> Integer.compare(stats.get(b.ko())[1] - stats.get(b.ko())[0],
                                            stats.get(a.ko())[1] - stats.get(a.ko())[0]));
        return out;
    }

    /** Sampling weight: words the learner gets wrong show up more often. */
    int weight(Data.Word w) {
        int[] s = stats.get(w.ko());
        if (s == null) return 3;
        return Math.max(1, 3 + s[1] * 3 - s[0]);
    }

    int questProgress(int i) {
        rollDay();
        return Math.min(QUEST_TARGET[i], switch (i) {
            case 0 -> dayLearned;
            case 1 -> dayCorrect;
            case 2 -> dayDialogue;
            default -> dayWins;
        });
    }
    boolean questDone(int i) { return questProgress(i) >= QUEST_TARGET[i]; }
    boolean questClaimed(int i) { rollDay(); return claimed.contains("q" + i); }

    // ---------------------------------------------------------------- mutations
    void gain(int points) {
        rollDay();
        touchStreak();
        int before = level();
        xp += points;
        if (level() > before) notifier.accept("\u00a76Lên cấp " + level() + "! \u00a7f" + title());
        checkAchievements();
        save();
    }

    boolean claimQuest(int i) {
        if (!questDone(i) || questClaimed(i)) return false;
        claimed.add("q" + i);
        gain(QUEST_REWARD[i]);
        notifier.accept("\u00a7aNhiệm vụ hoàn thành: " + QUEST_NAMES[i] + " \u00a76+" + QUEST_REWARD[i] + " XP");
        return true;
    }

    /** @return true if the word was newly marked as learned. */
    boolean markLearned(String ko) {
        rollDay();
        if (!learned.add(ko)) { save(); return false; }
        dayLearned++;
        gain(3);
        return true;
    }
    void unmarkLearned(String ko) { learned.remove(ko); save(); }

    boolean seeJamo(String ch) {
        if (!jamoSeen.add(ch)) return false;
        gain(1);
        return true;
    }

    void recordAnswer(String ko, boolean ok) {
        rollDay();
        int[] s = stat(ko);
        if (ok) { s[0]++; answered++; dayCorrect++; } else { s[1]++; wrong++; }
        save();
    }

    void noteCombo(int combo) { if (combo > bestCombo) { bestCombo = combo; checkAchievements(); } }

    /** @return XP awarded for this reply. */
    int dialogueCorrect(int id) {
        rollDay();
        dayDialogue++;
        int pts = dialoguesDone.add("d" + id) ? 12 : 4;
        gain(pts);
        return pts;
    }

    /** XP for finishing an arena run. level: 0 = basic, 1-6 = TOPIK, 7 = mixed TOPIK. */
    static int arenaXp(boolean won, boolean perfect, int level) {
        if (!won) return 5;
        int bonus = level <= 0 ? 0 : level >= 7 ? 30 : level * 8;
        return (perfect ? 70 : 40) + bonus;
    }

    void arenaFinished(boolean won, boolean perfect, int level) {
        rollDay();
        if (won) {
            wins++; dayWins++; if (perfect) perfectWins++;
            if (level >= 1 && level <= 7) topikWins[level]++;
        }
        gain(arenaXp(won, perfect, level));
    }

    void reset() {
        xp = answered = wrong = wins = perfectWins = bestCombo = streakDays = 0;
        lastDay = ""; dayKey = "";
        dayLearned = dayCorrect = dayDialogue = dayWins = 0;
        Arrays.fill(topikWins, 0);
        learned.clear(); jamoSeen.clear(); dialoguesDone.clear(); unlocked.clear(); claimed.clear(); stats.clear();
        save();
    }

    // ---------------------------------------------------------------- internals
    private void rollDay() {
        String today = LocalDate.now().toString();
        if (!today.equals(dayKey)) {
            dayKey = today;
            dayLearned = dayCorrect = dayDialogue = dayWins = 0;
            claimed.clear();
        }
    }

    private void touchStreak() {
        String today = LocalDate.now().toString();
        if (today.equals(lastDay)) return;
        streakDays = LocalDate.now().minusDays(1).toString().equals(lastDay) ? streakDays + 1 : 1;
        lastDay = today;
    }

    private void checkAchievements() {
        for (Achievement a : ACHIEVEMENTS) {
            if (!unlocked.contains(a.id()) && a.test().test(this)) {
                unlocked.add(a.id());
                notifier.accept("\u00a7eThành tựu mới: \u00a7f" + a.name());
            }
        }
    }

    // ---------------------------------------------------------------- persistence
    private static int num(Properties p, String key) {
        try { return Math.max(0, Integer.parseInt(p.getProperty(key, "0").trim())); }
        catch (NumberFormatException e) { return 0; }
    }
    private static Set<String> set(Properties p, String key) {
        Set<String> out = new LinkedHashSet<>();
        String raw = p.getProperty(key, "");
        if (!raw.isEmpty()) for (String s : raw.split("\\|")) if (!s.isEmpty()) out.add(s);
        return out;
    }

    private void load() {
        Properties p = new Properties();
        try {
            if (Files.exists(path)) try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { p.load(r); }
        } catch (IOException | IllegalArgumentException ignored) { return; }
        xp = num(p, "xp"); answered = num(p, "answered"); wrong = num(p, "wrong");
        wins = num(p, "wins"); perfectWins = num(p, "perfectWins"); bestCombo = num(p, "bestCombo");
        streakDays = num(p, "streakDays");
        String[] tw = p.getProperty("topikWins", "").split(",");
        for (int i = 0; i < tw.length && i < topikWins.length; i++) { try { topikWins[i] = Math.max(0, Integer.parseInt(tw[i].trim())); } catch (NumberFormatException ignored) { } }
        lastDay = p.getProperty("lastDay", ""); dayKey = p.getProperty("dayKey", "");
        dayLearned = num(p, "dayLearned"); dayCorrect = num(p, "dayCorrect");
        dayDialogue = num(p, "dayDialogue"); dayWins = num(p, "dayWins");
        learned.addAll(set(p, "learned")); jamoSeen.addAll(set(p, "jamoSeen"));
        dialoguesDone.addAll(set(p, "dialoguesDone")); unlocked.addAll(set(p, "unlocked"));
        claimed.addAll(set(p, "claimed"));
        for (String key : p.stringPropertyNames()) {
            if (!key.startsWith("stat.")) continue;
            String[] v = p.getProperty(key, "0,0").split(",");
            try { stats.put(key.substring(5), new int[]{Integer.parseInt(v[0].trim()), Integer.parseInt(v[1].trim())}); }
            catch (RuntimeException ignored) { }
        }
        rollDay();
    }

    void save() {
        Properties p = new Properties();
        p.setProperty("xp", "" + xp); p.setProperty("answered", "" + answered); p.setProperty("wrong", "" + wrong);
        p.setProperty("wins", "" + wins); p.setProperty("perfectWins", "" + perfectWins);
        p.setProperty("bestCombo", "" + bestCombo);
        StringBuilder twSb = new StringBuilder();
        for (int i = 0; i < topikWins.length; i++) twSb.append(i == 0 ? "" : ",").append(topikWins[i]);
        p.setProperty("topikWins", twSb.toString()); p.setProperty("streakDays", "" + streakDays);
        p.setProperty("lastDay", lastDay); p.setProperty("dayKey", dayKey);
        p.setProperty("dayLearned", "" + dayLearned); p.setProperty("dayCorrect", "" + dayCorrect);
        p.setProperty("dayDialogue", "" + dayDialogue); p.setProperty("dayWins", "" + dayWins);
        p.setProperty("learned", String.join("|", learned)); p.setProperty("jamoSeen", String.join("|", jamoSeen));
        p.setProperty("dialoguesDone", String.join("|", dialoguesDone));
        p.setProperty("unlocked", String.join("|", unlocked)); p.setProperty("claimed", String.join("|", claimed));
        for (Map.Entry<String, int[]> e : stats.entrySet())
            p.setProperty("stat." + e.getKey(), e.getValue()[0] + "," + e.getValue()[1]);
        try {
            Files.createDirectories(path.getParent());
            Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
            try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) { p.store(w, "KoreanCraft progress"); }
            try { Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (IOException atomicFail) { Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException ignored) { }
    }
}
