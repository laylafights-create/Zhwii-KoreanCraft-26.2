package com.zhwii.koreancraft;

import java.util.ArrayList;
import java.util.List;

/** Static learning content: vocabulary, Hangul letters and NPC dialogues. */
final class Data {
    private Data() {}

    record Word(String ko, String roman, String vi, int cat) {}
    record Jamo(String ch, String sound, boolean vowel, String exKo, String exRoman, String exVi) {}
    record Dialogue(String npc, String npcVi, String right, String rightVi,
                    String wrongA, String wrongAVi, String wrongB, String wrongBVi) {}

    static final String[] CATS = {
        "Chào hỏi", "Khối & vật phẩm", "Sinh vật", "Thức ăn", "Thiên nhiên", "Số đếm", "Con người"
    };
    static final String[] CATS_SHORT = {
        "Chào hỏi", "Vật phẩm", "Sinh vật", "Đồ ăn", "Thiên nhiên", "Số đếm", "Con người"
    };
    static final int[] CAT_COLORS = {
        0xFFFFC857, 0xFF5EEAD4, 0xFFFB7185, 0xFFFB923C, 0xFF4ADE80, 0xFF60A5FA, 0xFFC084FC
    };

    static final List<Word> WORDS = new ArrayList<>();
    private static void w(int cat, String ko, String roman, String vi) { WORDS.add(new Word(ko, roman, vi, cat)); }

    static {
        // 0 - Chào hỏi
        w(0, "안녕하세요", "annyeonghaseyo", "Xin chào");
        w(0, "안녕히 가세요", "annyeonghi gaseyo", "Tạm biệt (người đi)");
        w(0, "안녕히 계세요", "annyeonghi gyeseyo", "Tạm biệt (người ở)");
        w(0, "감사합니다", "gamsahamnida", "Cảm ơn");
        w(0, "죄송합니다", "joesonghamnida", "Xin lỗi");
        w(0, "네", "ne", "Vâng");
        w(0, "아니요", "aniyo", "Không");
        w(0, "괜찮아요", "gwaenchanayo", "Không sao đâu");
        w(0, "좋아요", "joayo", "Tốt / Thích");
        w(0, "도와주세요", "dowajuseyo", "Xin hãy giúp tôi");
        w(0, "반가워요", "bangawoyo", "Rất vui được gặp");
        // 1 - Khối & vật phẩm
        w(1, "나무", "namu", "Cây / gỗ");
        w(1, "돌", "dol", "Đá");
        w(1, "흙", "heuk", "Đất");
        w(1, "물", "mul", "Nước");
        w(1, "불", "bul", "Lửa");
        w(1, "용암", "yongam", "Dung nham");
        w(1, "철", "cheol", "Sắt");
        w(1, "금", "geum", "Vàng");
        w(1, "다이아몬드", "daiamondeu", "Kim cương");
        w(1, "석탄", "seoktan", "Than đá");
        w(1, "검", "geom", "Kiếm");
        w(1, "곡괭이", "gokgwaengi", "Cuốc chim");
        w(1, "활", "hwal", "Cung");
        w(1, "상자", "sangja", "Rương");
        w(1, "침대", "chimdae", "Giường");
        w(1, "횃불", "hwaetbul", "Đuốc");
        w(1, "집", "jip", "Nhà");
        w(1, "책", "chaek", "Sách");
        w(1, "문", "mun", "Cửa");
        // 2 - Sinh vật
        w(2, "고양이", "goyangi", "Mèo");
        w(2, "개", "gae", "Chó");
        w(2, "소", "so", "Bò");
        w(2, "돼지", "dwaeji", "Lợn");
        w(2, "닭", "dak", "Gà");
        w(2, "양", "yang", "Cừu");
        w(2, "말", "mal", "Ngựa");
        w(2, "늑대", "neukdae", "Sói");
        w(2, "물고기", "mulgogi", "Cá");
        w(2, "좀비", "jombi", "Zombie");
        w(2, "해골", "haegol", "Bộ xương");
        w(2, "거미", "geomi", "Nhện");
        w(2, "주민", "jumin", "Dân làng");
        w(2, "용", "yong", "Rồng");
        // 3 - Thức ăn
        w(3, "사과", "sagwa", "Táo");
        w(3, "빵", "ppang", "Bánh mì");
        w(3, "당근", "danggeun", "Cà rốt");
        w(3, "감자", "gamja", "Khoai tây");
        w(3, "쌀", "ssal", "Gạo");
        w(3, "고기", "gogi", "Thịt");
        w(3, "우유", "uyu", "Sữa");
        w(3, "케이크", "keikeu", "Bánh kem");
        w(3, "수박", "subak", "Dưa hấu");
        w(3, "계란", "gyeran", "Trứng");
        // 4 - Thiên nhiên
        w(4, "하늘", "haneul", "Bầu trời");
        w(4, "땅", "ttang", "Mặt đất");
        w(4, "꽃", "kkot", "Hoa");
        w(4, "해", "hae", "Mặt trời");
        w(4, "달", "dal", "Mặt trăng");
        w(4, "별", "byeol", "Ngôi sao");
        w(4, "비", "bi", "Mưa");
        w(4, "바다", "bada", "Biển");
        w(4, "산", "san", "Núi");
        w(4, "밤", "bam", "Ban đêm");
        w(4, "낮", "nat", "Ban ngày");
        w(4, "바람", "baram", "Gió");
        // 5 - Số đếm (số thuần Hàn)
        w(5, "하나", "hana", "Một");
        w(5, "둘", "dul", "Hai");
        w(5, "셋", "set", "Ba");
        w(5, "넷", "net", "Bốn");
        w(5, "다섯", "daseot", "Năm");
        w(5, "여섯", "yeoseot", "Sáu");
        w(5, "일곱", "ilgop", "Bảy");
        w(5, "여덟", "yeodeol", "Tám");
        w(5, "아홉", "ahop", "Chín");
        w(5, "열", "yeol", "Mười");
        // 6 - Con người
        w(6, "사람", "saram", "Con người");
        w(6, "친구", "chingu", "Bạn bè");
        w(6, "학교", "hakgyo", "Trường học");
        w(6, "선생님", "seonsaengnim", "Giáo viên");
        w(6, "학생", "haksaeng", "Học sinh");
        w(6, "엄마", "eomma", "Mẹ");
        w(6, "아빠", "appa", "Bố");
        w(6, "한국어", "hangugeo", "Tiếng Hàn");
        w(6, "이름", "ireum", "Tên");
        w(6, "가족", "gajok", "Gia đình");
    }

    static final List<Jamo> JAMO = List.of(
        new Jamo("ㄱ", "g / k", false, "고양이", "goyangi", "Mèo"),
        new Jamo("ㄴ", "n", false, "나무", "namu", "Cây"),
        new Jamo("ㄷ", "d / t", false, "돌", "dol", "Đá"),
        new Jamo("ㄹ", "r / l", false, "라면", "ramyeon", "Mì ăn liền"),
        new Jamo("ㅁ", "m", false, "물", "mul", "Nước"),
        new Jamo("ㅂ", "b / p", false, "불", "bul", "Lửa"),
        new Jamo("ㅅ", "s", false, "사과", "sagwa", "Táo"),
        new Jamo("ㅇ", "câm (đầu) / ng (cuối)", false, "아빠", "appa", "Bố"),
        new Jamo("ㅈ", "j", false, "집", "jip", "Nhà"),
        new Jamo("ㅊ", "ch", false, "책", "chaek", "Sách"),
        new Jamo("ㅋ", "k (bật hơi)", false, "케이크", "keikeu", "Bánh kem"),
        new Jamo("ㅌ", "t (bật hơi)", false, "토끼", "tokki", "Thỏ"),
        new Jamo("ㅍ", "p (bật hơi)", false, "포도", "podo", "Nho"),
        new Jamo("ㅎ", "h", false, "하늘", "haneul", "Bầu trời"),
        new Jamo("ㅏ", "a", true, "아기", "agi", "Em bé"),
        new Jamo("ㅑ", "ya", true, "야구", "yagu", "Bóng chày"),
        new Jamo("ㅓ", "eo (o mở)", true, "어머니", "eomeoni", "Mẹ"),
        new Jamo("ㅕ", "yeo", true, "여섯", "yeoseot", "Sáu"),
        new Jamo("ㅗ", "o", true, "오이", "oi", "Dưa chuột"),
        new Jamo("ㅛ", "yo", true, "요리", "yori", "Nấu ăn"),
        new Jamo("ㅜ", "u", true, "우유", "uyu", "Sữa"),
        new Jamo("ㅠ", "yu", true, "유리", "yuri", "Thủy tinh"),
        new Jamo("ㅡ", "eu (ư)", true, "그림", "geurim", "Bức tranh"),
        new Jamo("ㅣ", "i", true, "이름", "ireum", "Tên")
    );

    static final List<Dialogue> DIALOGUES = List.of(
        new Dialogue("안녕하세요!", "Xin chào!",
            "안녕하세요!", "Xin chào!", "감사합니다!", "Cảm ơn!", "아니요!", "Không!"),
        new Dialogue("이름이 뭐예요?", "Bạn tên là gì?",
            "제 이름은 스티브예요.", "Tên tôi là Steve.", "네, 좋아요.", "Vâng, tốt.", "안녕히 가세요.", "Tạm biệt."),
        new Dialogue("사과 드세요.", "Mời bạn ăn táo.",
            "감사합니다!", "Cảm ơn!", "안녕하세요?", "Xin chào?", "다이아몬드예요.", "Là kim cương."),
        new Dialogue("죄송합니다!", "Tôi xin lỗi!",
            "괜찮아요.", "Không sao đâu.", "감사합니다.", "Cảm ơn.", "반가워요.", "Rất vui được gặp."),
        new Dialogue("안녕히 가세요!", "Tạm biệt nhé!",
            "안녕히 계세요!", "Tạm biệt (người ở lại)!", "이름이 뭐예요?", "Bạn tên là gì?", "사과 주세요.", "Cho tôi táo."),
        new Dialogue("한국어 좋아해요?", "Bạn có thích tiếng Hàn không?",
            "네, 좋아해요!", "Vâng, tôi thích!", "죄송합니다!", "Xin lỗi!", "안녕히 가세요!", "Tạm biệt!"),
        new Dialogue("도와주세요!", "Hãy giúp tôi với!",
            "네, 도와줄게요!", "Vâng, tôi sẽ giúp!", "빵이에요.", "Là bánh mì.", "안녕히 계세요.", "Tạm biệt.")
    );

    // ------------------------------------------------------------------ TOPIK I-VI vocabulary
    static final String[] TOPIK_DESC = {
        "Từ vựng Minecraft và đời sống cơ bản",
        "TOPIK 1 · sơ cấp: từ thông dụng hằng ngày",
        "TOPIK 2 · sơ cấp: đời sống, du lịch, giao tiếp",
        "TOPIK 3 · trung cấp: xã hội, văn hóa, công việc",
        "TOPIK 4 · trung cấp: kinh tế, chính sách, lập luận",
        "TOPIK 5 · cao cấp: từ học thuật, báo chí",
        "TOPIK 6 · cao cấp: từ trừu tượng, văn viết",
        "Tổng hợp: trộn từ của cả 6 cấp TOPIK"
    };
    /** TOPIK_LEVELS.get(0) is TOPIK 1 ... get(5) is TOPIK 6; Word.cat holds the level index (0-5). */
    static final List<List<Word>> TOPIK_LEVELS = new ArrayList<>();
    static final List<Word> TOPIK_ALL = new ArrayList<>();
    static final List<Word> ALL = new ArrayList<>();
    private static void t(int level, String ko, String roman, String vi) {
        while (TOPIK_LEVELS.size() < level) TOPIK_LEVELS.add(new ArrayList<>());
        Word w = new Word(ko, roman, vi, level - 1);
        TOPIK_LEVELS.get(level - 1).add(w);
        TOPIK_ALL.add(w);
    }
    /** Arena question pool: 0 = basic, 1-6 = TOPIK level, 7 = all TOPIK levels. */
    static List<Word> arenaPool(int level) {
        if (level <= 0) return WORDS;
        if (level >= 7) return TOPIK_ALL;
        return TOPIK_LEVELS.get(level - 1);
    }
    static String levelName(int level) {
        return level <= 0 ? "Cơ bản" : level >= 7 ? "Tổng hợp" : "TOPIK " + level;
    }

    static {
        // TOPIK 1
        t(1, "가방", "gabang", "Cặp / túi"); t(1, "시간", "sigan", "Thời gian"); t(1, "오늘", "oneul", "Hôm nay");
        t(1, "내일", "naeil", "Ngày mai"); t(1, "어제", "eoje", "Hôm qua"); t(1, "음식", "eumsik", "Đồ ăn");
        t(1, "커피", "keopi", "Cà phê"); t(1, "전화", "jeonhwa", "Điện thoại"); t(1, "영화", "yeonghwa", "Phim");
        t(1, "음악", "eumak", "Âm nhạc"); t(1, "운동", "undong", "Vận động"); t(1, "병원", "byeongwon", "Bệnh viện");
        t(1, "시장", "sijang", "Chợ"); t(1, "은행", "eunhaeng", "Ngân hàng"); t(1, "공부", "gongbu", "Học tập");
        t(1, "길", "gil", "Con đường"); t(1, "버스", "beoseu", "Xe buýt"); t(1, "지하철", "jihacheol", "Tàu điện ngầm");
        t(1, "가다", "gada", "Đi"); t(1, "먹다", "meokda", "Ăn"); t(1, "보다", "boda", "Xem / nhìn");
        t(1, "마시다", "masida", "Uống"); t(1, "크다", "keuda", "To / lớn"); t(1, "작다", "jakda", "Nhỏ");
        // TOPIK 2
        t(2, "약속", "yaksok", "Cuộc hẹn / lời hứa"); t(2, "준비", "junbi", "Chuẩn bị"); t(2, "여행", "yeohaeng", "Du lịch");
        t(2, "계획", "gyehoek", "Kế hoạch"); t(2, "경험", "gyeongheom", "Kinh nghiệm"); t(2, "사진", "sajin", "Ảnh");
        t(2, "선물", "seonmul", "Quà tặng"); t(2, "생일", "saengil", "Sinh nhật"); t(2, "날씨", "nalssi", "Thời tiết");
        t(2, "계절", "gyejeol", "Mùa"); t(2, "교통", "gyotong", "Giao thông"); t(2, "도착하다", "dochakhada", "Đến nơi");
        t(2, "출발하다", "chulbalhada", "Xuất phát"); t(2, "기분", "gibun", "Tâm trạng"); t(2, "건강", "geongang", "Sức khỏe");
        t(2, "취미", "chwimi", "Sở thích"); t(2, "이사", "isa", "Chuyển nhà"); t(2, "예약", "yeyak", "Đặt trước");
        t(2, "주문", "jumun", "Gọi món / đặt hàng"); t(2, "이유", "iyu", "Lý do"); t(2, "방법", "bangbeop", "Phương pháp");
        t(2, "연락", "yeollak", "Liên lạc"); t(2, "부탁", "butak", "Nhờ vả"); t(2, "소개", "sogae", "Giới thiệu");
        // TOPIK 3
        t(3, "환경", "hwangyeong", "Môi trường"); t(3, "문화", "munhwa", "Văn hóa"); t(3, "사회", "sahoe", "Xã hội");
        t(3, "경제", "gyeongje", "Kinh tế"); t(3, "정치", "jeongchi", "Chính trị"); t(3, "역사", "yeoksa", "Lịch sử");
        t(3, "관계", "gwangye", "Mối quan hệ"); t(3, "문제", "munje", "Vấn đề"); t(3, "해결", "haegyeol", "Giải quyết");
        t(3, "결정", "gyeoljeong", "Quyết định"); t(3, "변화", "byeonhwa", "Biến đổi"); t(3, "영향", "yeonghyang", "Ảnh hưởng");
        t(3, "필요", "piryo", "Cần thiết"); t(3, "기회", "gihoe", "Cơ hội"); t(3, "노력", "noryeok", "Nỗ lực");
        t(3, "성공", "seonggong", "Thành công"); t(3, "실패", "silpae", "Thất bại"); t(3, "의견", "uigyeon", "Ý kiến");
        t(3, "경쟁", "gyeongjaeng", "Cạnh tranh"); t(3, "발전", "baljeon", "Phát triển"); t(3, "직업", "jigeop", "Nghề nghiệp");
        t(3, "교육", "gyoyuk", "Giáo dục"); t(3, "정보", "jeongbo", "Thông tin"); t(3, "습관", "seupgwan", "Thói quen");
        // TOPIK 4
        t(4, "경향", "gyeonghyang", "Xu hướng"); t(4, "현상", "hyeonsang", "Hiện tượng"); t(4, "원인", "wonin", "Nguyên nhân");
        t(4, "결과", "gyeolgwa", "Kết quả"); t(4, "제도", "jedo", "Chế độ / thể chế"); t(4, "정책", "jeongchaek", "Chính sách");
        t(4, "기술", "gisul", "Kỹ thuật / công nghệ"); t(4, "자원", "jawon", "Tài nguyên"); t(4, "산업", "saneop", "Công nghiệp");
        t(4, "소비", "sobi", "Tiêu dùng"); t(4, "투자", "tuja", "Đầu tư"); t(4, "갈등", "galdeung", "Bất hòa");
        t(4, "협력", "hyeomnyeok", "Hợp tác"); t(4, "책임", "chaegim", "Trách nhiệm"); t(4, "권리", "gwolli", "Quyền lợi");
        t(4, "의무", "uimu", "Nghĩa vụ"); t(4, "가치", "gachi", "Giá trị"); t(4, "태도", "taedo", "Thái độ");
        t(4, "관심", "gwansim", "Sự quan tâm"); t(4, "증가", "jeungga", "Gia tăng"); t(4, "감소", "gamso", "Giảm sút");
        t(4, "확인", "hwagin", "Xác nhận"); t(4, "예측", "yecheuk", "Dự đoán"); t(4, "적응", "jeogeung", "Thích nghi");
        // TOPIK 5
        t(5, "추세", "chuse", "Xu thế"); t(5, "양상", "yangsang", "Diện mạo / hình thái"); t(5, "쟁점", "jaengjeom", "Điểm tranh luận");
        t(5, "성찰", "seongchal", "Suy ngẫm"); t(5, "모순", "mosun", "Mâu thuẫn"); t(5, "편견", "pyeongyeon", "Định kiến");
        t(5, "혁신", "hyeoksin", "Đổi mới"); t(5, "지속", "jisok", "Tiếp diễn / bền vững"); t(5, "균형", "gyunhyeong", "Cân bằng");
        t(5, "한계", "hangye", "Giới hạn"); t(5, "극복", "geukbok", "Vượt qua"); t(5, "추구", "chugu", "Theo đuổi");
        t(5, "위축", "wichuk", "Co lại / sa sút"); t(5, "침체", "chimche", "Đình trệ"); t(5, "활성화", "hwalseonghwa", "Thúc đẩy");
        t(5, "규명", "gyumyeong", "Làm sáng tỏ"); t(5, "공존", "gongjon", "Cùng tồn tại"); t(5, "양극화", "yanggeukhwa", "Phân cực");
        t(5, "불가피", "bulgapi", "Không thể tránh khỏi"); t(5, "통념", "tongnyeom", "Quan niệm chung"); t(5, "대안", "daean", "Phương án thay thế");
        t(5, "규제", "gyuje", "Quy chế / kiểm soát"); t(5, "도모", "domo", "Mưu cầu"); t(5, "시사", "sisa", "Gợi ý / thời sự");
        // TOPIK 6
        t(6, "함의", "hamui", "Hàm ý"); t(6, "변천", "byeoncheon", "Biến thiên"); t(6, "패러다임", "paereodaim", "Mô hình tư duy");
        t(6, "상충", "sangchung", "Va chạm / trái ngược"); t(6, "초래", "chorae", "Gây ra"); t(6, "간과", "gangwa", "Coi nhẹ / bỏ sót");
        t(6, "통찰", "tongchal", "Thấu hiểu sâu sắc"); t(6, "일관", "ilgwan", "Nhất quán"); t(6, "방증", "bangjeung", "Bằng chứng gián tiếp");
        t(6, "지양", "jiyang", "Tránh / vượt qua"); t(6, "지향", "jihyang", "Hướng tới"); t(6, "상쇄", "sangswae", "Triệt tiêu");
        t(6, "근간", "geungan", "Nền tảng"); t(6, "난제", "nanje", "Vấn đề nan giải"); t(6, "파급", "pageup", "Lan tỏa");
        t(6, "편향", "pyeonhyang", "Thiên lệch"); t(6, "맹신", "maengsin", "Tin mù quáng"); t(6, "총체적", "chongchejeok", "Mang tính tổng thể");
        t(6, "고착", "gochak", "Cố hữu / đóng khuôn"); t(6, "점진적", "jeomjinjeok", "Tuần tự từng bước"); t(6, "위상", "wisang", "Vị thế");
        t(6, "당면", "dangmyeon", "Đối mặt trước mắt"); t(6, "자각", "jagak", "Tự nhận thức"); t(6, "도출", "dochul", "Rút ra");

        java.util.Set<String> seen = new java.util.HashSet<>();
        for (Word w : WORDS) if (seen.add(w.ko())) ALL.add(w);
        for (Word w : TOPIK_ALL) if (seen.add(w.ko())) ALL.add(w);
    }
}
