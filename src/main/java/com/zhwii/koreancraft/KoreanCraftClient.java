package com.zhwii.koreancraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import org.lwjgl.glfw.GLFW;
import java.nio.file.*;
import java.io.*;
import java.util.*;

/** KoreanCraft: client-side Vietnamese learning activities. No server installation needed. */
public final class KoreanCraftClient implements ClientModInitializer {
    private static final String[][] WORDS = {
        {"나무","Cây"},{"물","Nước"},{"돌","Đá"},{"사과","Táo"},{"책","Sách"},{"검","Kiếm"},
        {"집","Nhà"},{"불","Lửa"},{"고양이","Mèo"},{"소","Bò"},{"돼지","Lợn"},{"빵","Bánh mì"},
        {"하늘","Bầu trời"},{"땅","Đất"},{"꽃","Hoa"},{"친구","Bạn bè"},{"학교","Trường học"},
        {"사람","Con người"},{"안녕하세요","Xin chào"},{"감사합니다","Cảm ơn"},{"네","Vâng"},
        {"아니요","Không"},{"좋아요","Tốt"},{"한국어","Tiếng Hàn"}
    };
    private static final String[][] HANGUL = {{"ㄱ","g/k"},{"ㄴ","n"},{"ㄷ","d/t"},{"ㄹ","r/l"},{"ㅁ","m"},{"ㅂ","b/p"},{"ㅅ","s"},{"ㅇ","âm câm/ng"},{"ㅈ","j"},{"ㅎ","h"},{"ㅏ","a"},{"ㅓ","eo"},{"ㅗ","o"},{"ㅜ","u"},{"ㅡ","eu"},{"ㅣ","i"}};
    private static final String[][] DIALOGUE = {{"안녕하세요!","Xin chào!"},{"이름이 뭐예요?","Bạn tên là gì?"},{"반가워요!","Rất vui được gặp bạn!"},{"감사합니다!","Cảm ơn!"}};
    private final Properties progress = new Properties();
    private final boolean[] previous = new boolean[512];
    private final Random random = new Random();
    private Path savePath;
    private int wordIndex, hangulIndex, dialogueIndex, question = -1, correctChoice, arenaRemaining;
    private int xp, answered, wins, streak, lastReview = -1;
    private boolean menuOpen; private boolean arena;
    private String[] choices = new String[4];

    @Override public void onInitializeClient() {
        savePath = Paths.get(System.getProperty("user.dir"), "config", "zhwii_koreancraft.properties");
        load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) { Arrays.fill(previous, false); return; }
            long window = client.getWindow().handle();
            boolean shift = down(window, GLFW.GLFW_KEY_LEFT_SHIFT) || down(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            // One unified menu: Shift+K opens/closes it; keys 1-8 select a feature.
            if (!(client.screen instanceof KoreanMenu) && shift && pressed(window, GLFW.GLFW_KEY_K, true)) {
                menuOpen = !menuOpen;
                if (menuOpen) client.setScreen(new KoreanMenu(this));
                else notify(client.gui, "§7[KoreanCraft] Đã đóng menu.");
            }
            if (!(client.screen instanceof KoreanMenu)) {
                for (int i=0;i<4;i++)
                    if (shift && pressed(window, GLFW.GLFW_KEY_1+i, true) && question>=0) answer(client.gui,i);
            }
        });
    }
    private void showMenu(Object gui) {
        notify(gui,"§6[KoreanCraft] §f1.Từ vựng  2.Hangul  3.Trắc nghiệm  4.Ôn tập  5.NPC  6.Đấu trường  7.Nhiệm vụ  8.Thống kê §7| ESC: đóng");
    }
    private void select(Object gui,int item) {
        switch(item) {
            case 0 -> {String[] w=WORDS[wordIndex++ % WORDS.length];notify(gui,"§a[한국어] §f"+w[0]+" §7— §e"+w[1]);gain(2);}
            case 1 -> {String[] h=HANGUL[hangulIndex++ % HANGUL.length];notify(gui,"§b[Hangul] §f"+h[0]+" §7đọc gần đúng: §e"+h[1]);gain(2);}
            case 2 -> newQuestion(gui,false);
            case 3 -> review(gui);
            case 4 -> {String[] d=DIALOGUE[dialogueIndex++ % DIALOGUE.length];notify(gui,"§d[NPC] §f"+d[0]+" §7— §e"+d[1]);gain(2);}
            case 5 -> {arena=true;arenaRemaining=5;streak=0;newQuestion(gui,true);}
            case 6 -> notify(gui,"§a[Nhiệm vụ] §fHọc 10 từ (+20 XP), trả lời đúng 5 câu (+30 XP). Tiến độ: "+wordIndex+" từ, "+answered+" câu đúng.");
            case 7 -> notify(gui,"§6[KoreanCraft] §fXP: "+xp+" | Cấp: "+(xp/100+1)+" | Đúng: "+answered+" | Thắng: "+wins);
            default -> showMenu(gui);
        }
    }
    /** Dark navy + gold dashboard, responsive 2-column cards. */
    private static final class KoreanMenu extends Screen {
        private final KoreanCraftClient mod;
        private static final String[] LABELS = {"Từ vựng", "Bảng chữ Hangul", "Trắc nghiệm", "Ôn tập", "Hội thoại NPC", "Đấu trường", "Nhiệm vụ", "Thống kê"};
        private static final String[] DETAILS = {"24 từ Hàn - Việt", "16 ký tự cơ bản", "4 lựa chọn", "Xem lại từ đã học", "Tình huống giao tiếp", "Thử thách 5 câu", "Nhận thưởng XP", "Tiến độ của bạn"};
        KoreanMenu(KoreanCraftClient mod) { super(Component.literal("KoreanCraft")); this.mod=mod; }
        @Override protected void init() {
            int w=Math.min(390,this.width-28), x=(this.width-w)/2, top=Math.max(18,(this.height-286)/2);
            int gap=8, card=(w-36-gap)/2;
            for(int i=0;i<8;i++) {
                final int id=i;
                int bx=x+18+(i%2)*(card+gap), by=top+78+(i/2)*43;
                addRenderableWidget(Button.builder(Component.literal((i+1)+". "+LABELS[i]), b -> {
                    mod.menuOpen=false;
                    this.minecraft.setScreen(null);
                    mod.select(this.minecraft.gui,id);
                }).bounds(bx,by,card,34).build());
            }
            addRenderableWidget(Button.builder(Component.literal("Đóng menu"), b -> onClose()).bounds(x+w-98,top+258,80,20).build());
        }
        @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta) {
            this.renderBackground(g,mouseX,mouseY,delta);
            int w=Math.min(390,this.width-28),x=(this.width-w)/2,top=Math.max(18,(this.height-286)/2);
            g.fill(x-2,top-2,x+w+2,top+286,0xAA000000);
            g.fill(x,top,x+w,top+284,0xF5141D30);
            g.fill(x,top,x+w,top+4,0xFFFFCA75);
            g.fill(x+12,top+13,x+16,top+58,0xFFFFCA75);
            g.drawString(this.font,"K O R E A N C R A F T",x+26,top+15,0xFFFFD990,false);
            g.drawString(this.font,"한국어  •  Học tiếng Hàn trong Minecraft",x+26,top+32,0xFFE6EBFA,false);
            g.drawString(this.font,"Cấp "+(mod.xp/100+1)+"   •   "+mod.xp+" XP   •   "+mod.answered+" câu đúng",x+26,top+49,0xFF91E4D4,false);
            g.fill(x+18,top+68,x+w-18,top+69,0xFF394963);
            int card=(w-44)/2;
            for(int i=0;i<8;i++) {
                int bx=x+18+(i%2)*(card+8),by=top+78+(i/2)*43;
                g.drawString(this.font,DETAILS[i],bx+5,by+35,0xFFAAB8D0,false);
            }
            g.drawString(this.font,"SHIFT + K  •  Bảng điều khiển học tập",x+18,top+247,0xFFFFD990,false);
            super.render(g,mouseX,mouseY,delta);
        }
        @Override public void onClose() {mod.menuOpen=false;super.onClose();}
    }
    private boolean down(long window,int key) { return GLFW.glfwGetKey(window,key)==GLFW.GLFW_PRESS; }
    private boolean pressed(long window,int key,boolean shift) { boolean now=down(window,key); boolean result=now&&!previous[key]&&(shift || (!down(window,GLFW.GLFW_KEY_LEFT_SHIFT)&&!down(window,GLFW.GLFW_KEY_RIGHT_SHIFT))); previous[key]=now; return result; }
    private void newQuestion(Object gui,boolean battle) {
        question=random.nextInt(WORDS.length); correctChoice=random.nextInt(4);
        List<String> wrong=new ArrayList<>(); for(int i=0;i<WORDS.length;i++) if(i!=question) wrong.add(WORDS[i][1]);
        Collections.shuffle(wrong,random); int n=0; for(int i=0;i<4;i++) choices[i]=i==correctChoice?WORDS[question][1]:wrong.get(n++);
        notify(gui,(battle?"§c[Đấu trường "+arenaRemaining+"/5] ":"§b[Trắc nghiệm] ")+"§f"+WORDS[question][0]+" = ? §e1."+choices[0]+" 2."+choices[1]+" 3."+choices[2]+" 4."+choices[3]+" §7(Shift+1/2/3/4)");
    }
    private void answer(Object gui,int choice) {
        boolean correct=choice==correctChoice; int old=question; question=-1;
        if(correct) { answered++; streak++; gain(arena?15:10); notify(gui,"§aĐúng! §f"+WORDS[old][0]+" = "+WORDS[old][1]+" §6+"+(arena?15:10)+" XP"); }
        else { streak=0; notify(gui,"§cChưa đúng. §f"+WORDS[old][0]+" = "+WORDS[old][1]); }
        lastReview=old;
        if(arena) { arenaRemaining--; if(arenaRemaining==0) { if(streak==5) {wins++;gain(50);} notify(gui,"§6[Đấu trường] §fHoàn thành! Chuỗi đúng: "+streak+"/5"+(streak==5?" §a+50 XP":"")); arena=false; } else newQuestion(gui,true); }
        save();
    }
    private void review(Object gui) { if(lastReview<0) {notify(gui,"§e[Ôn tập] §fHãy làm trắc nghiệm trước (Shift+J).");return;} String[] w=WORDS[lastReview]; notify(gui,"§e[Ôn tập] §f"+w[0]+" — "+w[1]);gain(1); }
    private void gain(int points) {xp+=points; if(wordIndex>=10&&!Boolean.parseBoolean(progress.getProperty("wordsReward","false"))) {xp+=20;progress.setProperty("wordsReward","true");} if(answered>=5&&!Boolean.parseBoolean(progress.getProperty("quizReward","false"))) {xp+=30;progress.setProperty("quizReward","true");}save();}
    private void load() { try {Files.createDirectories(savePath.getParent());if(Files.exists(savePath))try(InputStream in=Files.newInputStream(savePath)){progress.load(in);} xp=num("xp");answered=num("answered");wins=num("wins");wordIndex=num("wordIndex");hangulIndex=num("hangulIndex");dialogueIndex=num("dialogueIndex");lastReview=num("lastReview")-1;}catch(IOException|NumberFormatException ignored){} }
    private int num(String key) {return Integer.parseInt(progress.getProperty(key,"0"));}
    private void save() {try {Files.createDirectories(savePath.getParent());progress.setProperty("xp",""+xp);progress.setProperty("answered",""+answered);progress.setProperty("wins",""+wins);progress.setProperty("wordIndex",""+wordIndex);progress.setProperty("hangulIndex",""+hangulIndex);progress.setProperty("dialogueIndex",""+dialogueIndex);progress.setProperty("lastReview",""+(lastReview+1));try(OutputStream out=Files.newOutputStream(savePath)){progress.store(out,"KoreanCraft Vietnamese progress");}}catch(IOException ignored){} }
    private static void notify(Object gui,String message) {
        Component text=Component.literal(message);
        try {gui.getClass().getMethod("setOverlayMessage",Component.class,boolean.class).invoke(gui,text,false);return;}catch(ReflectiveOperationException ignored){}
        try {gui.getClass().getMethod("setTitle",Component.class).invoke(gui,text);}catch(ReflectiveOperationException ignored){}
    }
}
