package com.zhwii.koreancraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Static learning content: vocabulary (Minecraft + real life), Hangul letters and NPC dialogues. */
final class Data {
    private Data() {}

    /** sec: 0 = Minecraft, 1 = đời sống. cat indexes into that section's category list. */
    record Word(String ko, String roman, String vi, int cat, int sec) {
        Word(String ko, String roman, String vi, int cat) { this(ko, roman, vi, cat, 1); }
    }
    record Jamo(String ch, String sound, boolean vowel, String exKo, String exRoman, String exVi) {}
    record Dialogue(String npc, String npcVi, String right, String rightVi,
                    String wrongA, String wrongAVi, String wrongB, String wrongBVi, int sec) {}

    static final String[] SEC_NAMES = {"Minecraft", "Đời sống"};

    static final String[] MC_CATS = {
        "Khối & tài nguyên", "Công cụ & giáp", "Đồ vật & công trình", "Nguyên liệu", "Sinh vật & quái",
        "Thức ăn", "Địa hình & thế giới", "Chế độ & hệ thống", "Redstone & cơ chế"
    };
    static final String[] MC_SHORT = {
        "Khối", "Công cụ", "Đồ vật", "Nguyên liệu", "Sinh vật", "Đồ ăn", "Địa hình", "Hệ thống", "Redstone"
    };
    static final String[] LIFE_CATS = {
        "Chào hỏi & giao tiếp", "Gia đình & con người", "Cơ thể & cảm xúc", "Ăn uống", "Số & thời gian",
        "Màu sắc & tính từ", "Nhà cửa & đồ dùng", "Địa điểm & giao thông", "Trường học & nghề nghiệp",
        "Động từ thường dùng", "Thiên nhiên & thời tiết"
    };
    static final String[] LIFE_SHORT = {
        "Chào hỏi", "Gia đình", "Cơ thể", "Ăn uống", "Số & giờ", "Màu & tính từ", "Nhà cửa", "Địa điểm",
        "Trường & nghề", "Động từ", "Thiên nhiên"
    };
    static final int[] CAT_COLORS = {
        0xFFFFC857, 0xFF5EEAD4, 0xFFFB7185, 0xFFFB923C, 0xFF4ADE80, 0xFF60A5FA, 0xFFC084FC, 0xFFF472B6
    };

    static String[] catsOf(int sec) { return sec == 0 ? MC_CATS : LIFE_CATS; }
    static String[] shortOf(int sec) { return sec == 0 ? MC_SHORT : LIFE_SHORT; }
    static int catColor(int cat) { return CAT_COLORS[Math.floorMod(cat, CAT_COLORS.length)]; }
    static List<Word> wordsOf(int sec) { return sec == 0 ? MC_WORDS : LIFE_WORDS; }
    /** src: 0 = tất cả, 1 = Minecraft, 2 = đời sống. */
    static List<Word> pool(int src) { return src == 1 ? MC_WORDS : src == 2 ? LIFE_WORDS : WORDS; }
    static String srcName(int src) { return src == 1 ? "Minecraft" : src == 2 ? "Đời sống" : "Tất cả"; }

    static final List<Word> MC_WORDS = new ArrayList<>();
    static final List<Word> LIFE_WORDS = new ArrayList<>();
    /** Minecraft words first, then real-life words. Korean spelling is unique across the list. */
    static final List<Word> WORDS = new ArrayList<>();
    private static final Set<String> SEEN = new HashSet<>();

    private static void add(int sec, int cat, String item) {
        String[] a = item.split("\\|");
        if (a.length != 3 || !SEEN.add(a[0])) return;
        Word w = new Word(a[0], a[1], a[2], cat, sec);
        (sec == 0 ? MC_WORDS : LIFE_WORDS).add(w);
        WORDS.add(w);
    }
    private static void mc(int cat, String... items) { for (String s : items) add(0, cat, s); }
    private static void life(int cat, String... items) { for (String s : items) add(1, cat, s); }

    static {
        // ====================================================================== MINECRAFT
        // 0 - Khối & tài nguyên
        mc(0, "나무|namu|Cây / gỗ", "돌|dol|Đá", "흙|heuk|Đất", "물|mul|Nước", "불|bul|Lửa", "용암|yongam|Dung nham",
            "철|cheol|Sắt", "금|geum|Vàng", "다이아몬드|daiamondeu|Kim cương", "석탄|seoktan|Than đá",
            "잔디 블록|jandi beullok|Khối cỏ", "모래|morae|Cát", "자갈|jagal|Sỏi", "조약돌|joyakdol|Đá cuội",
            "흑요석|heugyoseok|Hắc diện thạch", "유리|yuri|Kính", "양털|yangteol|Len", "벽돌|byeokdol|Gạch",
            "점토|jeomto|Đất sét", "얼음|eoreum|Băng", "눈덩이|nundeongi|Cục tuyết", "선인장|seoninjang|Xương rồng",
            "호박|hobak|Bí ngô", "사탕수수|satangsusu|Mía", "밀|mil|Lúa mì", "씨앗|ssiat|Hạt giống",
            "판자|panja|Ván gỗ", "원목|wonmok|Khúc gỗ", "구리|guri|Đồng", "에메랄드|emeraldeu|Ngọc lục bảo",
            "청금석|cheonggeumseok|Lưu ly", "석영|seogyeong|Thạch anh", "네더라이트|nedeoraiteu|Netherite",
            "기반암|gibanam|Đá nền", "네더랙|nedeoraek|Đá Nether", "영혼 모래|yeonghon morae|Cát linh hồn",
            "발광석|balgwangseok|Đá phát sáng", "엔드 돌|endeu dol|Đá End", "심층암|simcheungam|Đá tầng sâu",
            "안산암|ansanam|Đá andesit", "화강암|hwagangam|Đá granit", "섬록암|seomnogam|Đá diorit",
            "사암|saam|Sa thạch", "광석|gwangseok|Quặng", "주괴|jugoe|Thỏi kim loại", "덩굴|deonggul|Dây leo",
            "이끼|ikki|Rêu", "꽃잎|kkonnip|Cánh hoa");
        // 1 - Công cụ & giáp
        mc(1, "검|geom|Kiếm", "곡괭이|gokgwaengi|Cuốc chim", "활|hwal|Cung", "도끼|dokki|Rìu", "삽|sap|Xẻng",
            "괭이|gwaengi|Cuốc đất", "화살|hwasal|Mũi tên", "방패|bangpae|Khiên", "석궁|seokgung|Nỏ",
            "삼지창|samjichang|Đinh ba", "갑옷|gabot|Áo giáp", "투구|tugu|Mũ giáp", "흉갑|hyunggap|Giáp ngực",
            "각반|gakban|Giáp chân", "부츠|bucheu|Giày giáp", "겉날개|geonnalgae|Cánh Elytra",
            "낚싯대|naksitdae|Cần câu", "가위|gawi|Kéo", "부싯돌|busitdol|Đá lửa", "양동이|yangdongi|Xô",
            "나침반|nachimban|La bàn", "시계|sigye|Đồng hồ", "지도|jido|Bản đồ", "이름표|ireumpyo|Thẻ tên",
            "끈|kkeun|Dây dắt", "안장|anjang|Yên ngựa", "망원경|mangwongyeong|Ống nhòm", "토템|totem|Bùa bất tử");
        // 2 - Đồ vật & công trình
        mc(2, "상자|sangja|Rương", "침대|chimdae|Giường", "횃불|hwaetbul|Đuốc", "집|jip|Nhà", "책|chaek|Sách",
            "문|mun|Cửa", "작업대|jageopdae|Bàn chế tạo", "화로|hwaro|Lò nung", "모루|moru|Đe",
            "마법 부여대|mabeop buyeodae|Bàn phù phép", "양조기|yangjogi|Giá pha chế", "가마솥|gamasot|Vạc",
            "책장|chaekjang|Giá sách", "사다리|sadari|Thang", "울타리|ultari|Hàng rào", "표지판|pyojipan|Biển báo",
            "깃발|gitbal|Cờ", "그림|geurim|Bức tranh", "계단|gyedan|Cầu thang", "반 블록|ban beullok|Nửa khối",
            "함정문|hamjeongmun|Cửa sập", "양초|yangcho|Nến", "등불|deungbul|Đèn lồng", "엔더 상자|endeo sangja|Rương Ender",
            "카펫|kapet|Thảm", "화분|hwabun|Chậu cây", "신호기|sinhogi|Đèn hiệu", "광산 수레|gwangsan sure|Xe goòng",
            "레일|reil|Đường ray", "배|bae|Thuyền (còn nghĩa: bụng, lê)", "탑|tap|Tháp", "다리|dari|Cầu / chân",
            "벽|byeok|Tường", "지붕|jibung|Mái nhà", "바닥|badak|Sàn", "창고|changgo|Nhà kho");
        // 3 - Nguyên liệu
        mc(3, "막대기|makdaegi|Gậy", "화약|hwayak|Thuốc súng", "뼈|ppyeo|Xương", "실|sil|Sợi dây", "깃털|gitteol|Lông vũ",
            "가죽|gajuk|Da thuộc", "거미줄|geomijul|Mạng nhện", "엔더 진주|endeo jinju|Ngọc Ender",
            "블레이즈 막대|beulleijeu makdae|Cây Blaze", "슬라임볼|seullaimbol|Cục slime",
            "가스트의 눈물|gaseuteuui nunmul|Nước mắt Ghast", "경험치 구슬|gyeonghyeomchi guseul|Cầu kinh nghiệm",
            "물약|mulyak|Thuốc", "마법 부여된 책|mabeop buyeodoen chaek|Sách phù phép", "먹물 주머니|meongmul jumeoni|Túi mực",
            "종이|jongi|Giấy", "금 조각|geum jogak|Mảnh vàng", "철 조각|cheol jogak|Mảnh sắt",
            "염료|yeomnyo|Thuốc nhuộm", "로드스톤|rodeuseuton|Đá nam châm");
        // 4 - Sinh vật & quái
        mc(4, "고양이|goyangi|Mèo", "개|gae|Chó", "소|so|Bò", "돼지|dwaeji|Lợn", "닭|dak|Gà", "양|yang|Cừu", "말|mal|Ngựa",
            "늑대|neukdae|Sói", "물고기|mulgogi|Cá", "좀비|jombi|Zombie", "해골|haegol|Bộ xương", "거미|geomi|Nhện",
            "주민|jumin|Dân làng", "용|yong|Rồng", "크리퍼|keuripeo|Creeper", "엔더맨|endeomaen|Enderman",
            "스켈레톤|seukelleton|Skeleton", "마녀|manyeo|Phù thủy", "약탈자|yaktalja|Kẻ cướp phá", "블레이즈|beulleijeu|Blaze",
            "가스트|gaseuteu|Ghast", "슬라임|seullaim|Slime", "위더|wideo|Wither", "엔더 드래곤|endeo deuraegon|Rồng Ender",
            "철 골렘|cheol gollem|Golem sắt", "박쥐|bakjwi|Dơi", "앵무새|aengmusae|Vẹt", "거북|geobuk|Rùa",
            "판다|panda|Gấu trúc", "여우|yeou|Cáo", "벌|beol|Ong", "토끼|tokki|Thỏ", "오징어|ojingeo|Mực",
            "돌고래|dolgorae|Cá heo", "북극곰|bukgeukgom|Gấu bắc cực", "라마|rama|Lạc đà không bướu",
            "당나귀|dangnagwi|Lừa", "노새|nosae|La", "오셀롯|osellot|Mèo rừng", "개구리|gaeguri|Ếch",
            "아홀로틀|aholloteul|Axolotl", "좀벌레|jombeolle|Bọ bạc", "익사한 자|iksahan ja|Zombie chết đuối",
            "팬텀|paenteom|Phantom", "셜커|syeolkeo|Shulker", "방랑 상인|bangnang sangin|Thương nhân lang thang",
            "피글린|piglin|Piglin", "호글린|hogeullin|Hoglin", "몬스터|monseuteo|Quái vật", "동물|dongmul|Động vật");
        // 5 - Thức ăn
        mc(5, "사과|sagwa|Táo", "빵|ppang|Bánh mì", "당근|danggeun|Cà rốt", "감자|gamja|Khoai tây", "쌀|ssal|Gạo",
            "고기|gogi|Thịt", "우유|uyu|Sữa", "케이크|keikeu|Bánh kem", "수박|subak|Dưa hấu", "계란|gyeran|Trứng",
            "쿠키|kuki|Bánh quy", "호박 파이|hobak pai|Bánh bí ngô", "황금 사과|hwanggeum sagwa|Táo vàng",
            "구운 감자|guun gamja|Khoai tây nướng", "스테이크|seuteikeu|Bít tết", "돼지고기|dwaejigogi|Thịt lợn",
            "소고기|sogogi|Thịt bò", "닭고기|dakgogi|Thịt gà", "양고기|yanggogi|Thịt cừu", "썩은 살점|sseogeun saljeom|Thịt thối",
            "연어|yeoneo|Cá hồi", "대구|daegu|Cá tuyết", "복어|bogeo|Cá nóc", "사탕무|satangmu|Củ dền",
            "멜론|mellon|Dưa lưới", "달콤한 열매|dalkomhan yeolmae|Quả mọng ngọt", "꿀|kkul|Mật ong",
            "버섯 스튜|beoseot seutyu|Súp nấm", "버섯|beoseot|Nấm", "설탕|seoltang|Đường", "해초|haecho|Rong biển",
            "황금 당근|hwanggeum danggeun|Cà rốt vàng", "코코아 콩|kokoa kong|Hạt ca cao");
        // 6 - Địa hình & thế giới
        mc(6, "오버월드|obeowoldeu|Overworld", "네더|nedeo|Nether", "엔드|endeu|End", "동굴|donggul|Hang động",
            "광산|gwangsan|Mỏ", "숲|sup|Rừng", "사막|samak|Sa mạc", "정글|jeonggeul|Rừng rậm", "늪|neup|Đầm lầy",
            "평원|pyeongwon|Đồng bằng", "설원|seorwon|Đồng tuyết", "타이가|taiga|Taiga", "버섯 들판|beoseot deulpan|Đồng nấm",
            "섬|seom|Đảo", "강|gang|Sông", "호수|hosu|Hồ", "절벽|jeolbyeok|Vách đá", "계곡|gyegok|Thung lũng",
            "마을|maeul|Làng", "요새|yosae|Pháo đài", "사원|sawon|Đền", "던전|deonjeon|Ngục tối",
            "해저 신전|haejeo sinjeon|Đền dưới biển", "삼림 대저택|samnim daejeotaek|Dinh thự rừng",
            "폐광|pyegwang|Mỏ bỏ hoang", "포털|poteol|Cổng dịch chuyển", "스폰 지점|seupon jijeom|Điểm hồi sinh",
            "지하|jiha|Dưới lòng đất", "고대 도시|godae dosi|Thành phố cổ đại", "전초기지|jeonchogiji|Tiền đồn");
        // 7 - Chế độ & hệ thống
        mc(7, "생존|saengjon|Sinh tồn", "창작|changjak|Sáng tạo", "모험|moheom|Phiêu lưu", "관전|gwanjeon|Quan sát",
            "하드코어|hadeukoeo|Hardcore", "난이도|nanido|Độ khó", "평화로움|pyeonghwaroum|Hòa bình", "쉬움|swium|Dễ (độ khó game)",
            "보통|botong|Bình thường", "어려움|eoryeoum|Khó (độ khó game)", "체력|cheryeok|Máu / thể lực",
            "배고픔|baegopeum|Độ đói", "경험치|gyeonghyeomchi|Điểm kinh nghiệm", "레벨|rebel|Cấp độ", "인벤토리|inbentori|Túi đồ",
            "핫바|hatba|Thanh vật phẩm", "채굴|chaegul|Đào mỏ", "제작|jejak|Chế tạo", "건축|geonchuk|Xây dựng",
            "농사|nongsa|Làm nông", "낚시|naksi|Câu cá", "교환|gyohwan|Trao đổi", "마법 부여|mabeop buyeo|Phù phép",
            "양조|yangjo|Pha chế", "부활|buhwal|Hồi sinh", "서버|seobeo|Máy chủ", "월드|woldeu|Thế giới",
            "시드|sideu|Mã thế giới", "좌표|jwapyo|Tọa độ", "명령어|myeongnyeongeo|Lệnh", "모드|modeu|Mod",
            "업적|eopjeok|Thành tựu", "멀티플레이|meoltipeullei|Nhiều người chơi", "싱글 플레이|singgeul peullei|Chơi đơn",
            "플레이어|peulleieo|Người chơi", "피해|pihae|Sát thương", "방어력|bangeoryeok|Phòng thủ",
            "내구도|naeguda|Độ bền", "죽음|jugeum|Cái chết", "저장|jeojang|Lưu game", "설정|seoljeong|Cài đặt");
        // 8 - Redstone & cơ chế
        mc(8, "레드스톤|redeuseuton|Đá đỏ", "피스톤|piseuton|Piston", "끈끈이 피스톤|kkeunkkeuni piseuton|Piston dính",
            "레버|rebeo|Cần gạt", "버튼|beoteun|Nút bấm", "발사기|balsagi|Máy bắn", "공급기|gonggeupgi|Máy thả",
            "깔때기|kkalttaegi|Phễu", "관측기|gwancheukgi|Bộ quan sát", "중계기|jjunggyegi|Bộ lặp", "비교기|bigyogi|Bộ so sánh",
            "압력판|amnyeokpan|Tấm áp suất", "폭죽|pokjuk|Pháo hoa", "폭발|pokbal|Vụ nổ", "함정|hamjeong|Bẫy",
            "자동화|jadonghwa|Tự động hóa", "농장|nongjang|Nông trại", "신호|sinho|Tín hiệu", "회로|hoero|Mạch điện",
            "작동|jakdong|Hoạt động");

        // ====================================================================== ĐỜI SỐNG
        // 0 - Chào hỏi & giao tiếp
        life(0, "안녕하세요|annyeonghaseyo|Xin chào", "안녕히 가세요|annyeonghi gaseyo|Tạm biệt (người đi)",
            "안녕히 계세요|annyeonghi gyeseyo|Tạm biệt (người ở)", "감사합니다|gamsahamnida|Cảm ơn",
            "죄송합니다|joesonghamnida|Xin lỗi", "네|ne|Vâng", "아니요|aniyo|Không", "괜찮아요|gwaenchanayo|Không sao đâu",
            "좋아요|joayo|Tốt / Thích", "도와주세요|dowajuseyo|Xin hãy giúp tôi", "반가워요|bangawoyo|Rất vui được gặp",
            "안녕|annyeong|Chào (thân mật)", "처음 뵙겠습니다|cheoeum boepgetseumnida|Rất hân hạnh được gặp lần đầu",
            "잘 지냈어요|jal jinaesseoyo|Bạn khỏe không", "잘 먹겠습니다|jal meokgetseumnida|Tôi xin phép ăn",
            "잘 먹었습니다|jal meogeotseumnida|Cảm ơn bữa ăn", "실례합니다|sillyehamnida|Xin phép / xin lỗi làm phiền",
            "어서 오세요|eoseo oseyo|Chào mừng quý khách", "축하합니다|chukahamnida|Chúc mừng", "잠시만요|jamsimanyo|Chờ một chút",
            "천천히 말해 주세요|cheoncheonhi malhae juseyo|Hãy nói chậm lại", "모르겠어요|moreugesseoyo|Tôi không biết",
            "알겠어요|algesseoyo|Tôi hiểu rồi", "맞아요|majayo|Đúng rồi", "정말요|jeongmallyo|Thật sao", "화이팅|hwaiting|Cố lên",
            "잘 자요|jal jayo|Chúc ngủ ngon", "안녕히 주무세요|annyeonghi jumuseyo|Chúc ngủ ngon (kính ngữ)",
            "얼마예요|eolmayeyo|Bao nhiêu tiền", "주세요|juseyo|Cho tôi", "여기요|yeogiyo|Xin lỗi (gọi nhân viên)",
            "미안해요|mianhaeyo|Xin lỗi (thân mật)", "고마워요|gomawoyo|Cảm ơn (thân mật)", "사랑해요|saranghaeyo|Yêu bạn",
            "보고 싶어요|bogo sipeoyo|Nhớ bạn", "생일 축하해요|saengil chukahaeyo|Chúc mừng sinh nhật",
            "새해 복 많이 받으세요|saehae bok mani badeuseyo|Chúc mừng năm mới", "뭐예요|mwoyeyo|Là cái gì",
            "누구예요|nuguyeyo|Là ai", "왜요|waeyo|Tại sao", "언제예요|eonjeyeyo|Khi nào");
        // 1 - Gia đình & con người
        life(1, "사람|saram|Con người", "친구|chingu|Bạn bè", "엄마|eomma|Mẹ", "아빠|appa|Bố", "가족|gajok|Gia đình",
            "어머니|eomeoni|Mẹ (trang trọng)", "아버지|abeoji|Bố (trang trọng)", "부모님|bumonim|Bố mẹ", "형|hyeong|Anh (em trai gọi)",
            "오빠|oppa|Anh (em gái gọi)", "누나|nuna|Chị (em trai gọi)", "언니|eonni|Chị (em gái gọi)", "동생|dongsaeng|Em",
            "남동생|namdongsaeng|Em trai", "여동생|yeodongsaeng|Em gái", "할머니|halmeoni|Bà", "할아버지|harabeoji|Ông",
            "아들|adeul|Con trai", "딸|ttal|Con gái", "아기|agi|Em bé", "남자|namja|Đàn ông / con trai",
            "여자|yeoja|Phụ nữ / con gái", "남편|nampyeon|Chồng", "아내|anae|Vợ", "아이|ai|Đứa trẻ", "어른|eoreun|Người lớn",
            "손님|sonnim|Khách", "이웃|iut|Hàng xóm", "외국인|oegugin|Người nước ngoài", "한국 사람|hanguk saram|Người Hàn Quốc",
            "베트남 사람|beteunam saram|Người Việt Nam", "나|na|Tôi (thân mật)", "저|jeo|Tôi (lịch sự)", "너|neo|Bạn (thân mật)",
            "우리|uri|Chúng ta / chúng tôi", "그 사람|geu saram|Người đó");
        // 2 - Cơ thể & cảm xúc
        life(2, "머리|meori|Đầu", "얼굴|eolgul|Khuôn mặt", "눈|nun|Mắt / tuyết", "코|ko|Mũi", "입|ip|Miệng", "귀|gwi|Tai",
            "치아|chia|Răng", "목|mok|Cổ", "어깨|eokkae|Vai", "팔|pal|Cánh tay / số tám", "손|son|Bàn tay",
            "손가락|songarak|Ngón tay", "가슴|gaseum|Ngực", "허리|heori|Eo / lưng dưới", "발|bal|Bàn chân", "무릎|mureup|Đầu gối",
            "등|deung|Lưng", "피|pi|Máu", "몸|mom|Cơ thể", "마음|maeum|Tấm lòng / tâm trí", "머리카락|meorikarak|Tóc",
            "기쁘다|gippeuda|Vui mừng", "슬프다|seulpeuda|Buồn", "화나다|hwanada|Tức giận", "무섭다|museopda|Đáng sợ / sợ",
            "행복하다|haengbokhada|Hạnh phúc", "피곤하다|pigonhada|Mệt mỏi", "아프다|apeuda|Đau / ốm", "배고프다|baegopeuda|Đói bụng",
            "배부르다|baebureuda|No bụng", "졸리다|jollida|Buồn ngủ", "외롭다|oeropda|Cô đơn", "놀라다|nollada|Ngạc nhiên",
            "걱정하다|geokjeonghada|Lo lắng", "재미있다|jaemiitda|Thú vị", "심심하다|simsimhada|Chán");
        // 3 - Ăn uống
        life(3, "밥|bap|Cơm", "김치|gimchi|Kim chi", "라면|ramyeon|Mì ăn liền", "김밥|gimbap|Cơm cuộn", "비빔밥|bibimbap|Cơm trộn",
            "불고기|bulgogi|Thịt bò nướng kiểu Hàn", "삼겹살|samgyeopsal|Thịt ba chỉ", "떡볶이|tteokbokki|Bánh gạo cay",
            "냉면|naengmyeon|Mì lạnh", "국|guk|Canh", "찌개|jjigae|Lẩu / canh đậm", "차|cha|Trà", "주스|juseu|Nước ép",
            "콜라|kolla|Coca", "과일|gwail|Trái cây", "바나나|banana|Chuối", "딸기|ttalgi|Dâu tây", "포도|podo|Nho",
            "오렌지|orenji|Cam", "귤|gyul|Quýt", "복숭아|boksunga|Đào", "채소|chaeso|Rau", "양파|yangpa|Hành tây",
            "마늘|maneul|Tỏi", "오이|oi|Dưa chuột", "토마토|tomato|Cà chua", "고추|gochu|Ớt", "배추|baechu|Cải thảo",
            "생선|saengseon|Cá (làm món)", "소금|sogeum|Muối", "기름|gireum|Dầu ăn", "간장|ganjang|Nước tương",
            "아침|achim|Buổi sáng / bữa sáng", "점심|jeomsim|Buổi trưa / bữa trưa", "저녁|jeonyeok|Buổi tối / bữa tối",
            "식당|sikdang|Nhà hàng", "맛있다|masitda|Ngon", "맵다|maepda|Cay", "짜다|jjada|Mặn", "달다|dalda|Ngọt",
            "시다|sida|Chua", "숟가락|sutgarak|Thìa", "젓가락|jeotgarak|Đũa", "그릇|geureut|Bát", "컵|keop|Cốc",
            "접시|jeopsi|Đĩa", "메뉴|menyu|Thực đơn", "요리|yori|Nấu ăn", "아이스크림|aiseukeurim|Kem", "과자|gwaja|Bánh snack");
        // 4 - Số & thời gian
        life(4, "하나|hana|Một", "둘|dul|Hai", "셋|set|Ba", "넷|net|Bốn", "다섯|daseot|Năm", "여섯|yeoseot|Sáu",
            "일곱|ilgop|Bảy", "여덟|yeodeol|Tám", "아홉|ahop|Chín", "열|yeol|Mười", "스물|seumul|Hai mươi", "서른|seoreun|Ba mươi",
            "마흔|maheun|Bốn mươi", "쉰|swin|Năm mươi", "영|yeong|Không (số 0)", "일|il|Một (số Hán-Hàn)", "이|i|Hai (số Hán-Hàn)",
            "삼|sam|Ba (số Hán-Hàn)", "사|sa|Bốn (số Hán-Hàn)", "오|o|Năm (số Hán-Hàn)", "육|yuk|Sáu (số Hán-Hàn)",
            "칠|chil|Bảy (số Hán-Hàn)", "구|gu|Chín (số Hán-Hàn)", "십|sip|Mười (số Hán-Hàn)", "백|baek|Một trăm",
            "천|cheon|Một nghìn", "만|man|Mười nghìn", "시|si|Giờ", "분|bun|Phút", "초|cho|Giây", "오전|ojeon|Buổi sáng (AM)",
            "오후|ohu|Buổi chiều (PM)", "월요일|woryoil|Thứ hai", "화요일|hwayoil|Thứ ba", "수요일|suyoil|Thứ tư",
            "목요일|mogyoil|Thứ năm", "금요일|geumyoil|Thứ sáu", "토요일|toyoil|Thứ bảy", "일요일|iryoil|Chủ nhật",
            "주말|jumal|Cuối tuần", "평일|pyeongil|Ngày thường", "지금|jigeum|Bây giờ", "나중에|najunge|Sau này",
            "항상|hangsang|Luôn luôn", "가끔|gakkeum|Thỉnh thoảng", "매일|maeil|Mỗi ngày", "올해|olhae|Năm nay",
            "작년|jangnyeon|Năm ngoái", "내년|naenyeon|Năm sau", "월|wol|Tháng", "년|nyeon|Năm (thời gian)", "주|ju|Tuần",
            "명|myeong|Người (đếm người)", "마리|mari|Con (đếm động vật)", "권|gwon|Quyển (đếm sách)", "잔|jan|Ly / cốc (đếm)");
        // 5 - Màu sắc & tính từ
        life(5, "빨간색|ppalgansaek|Màu đỏ", "파란색|paransaek|Màu xanh dương", "노란색|noransaek|Màu vàng",
            "초록색|choroksaek|Màu xanh lá", "검은색|geomeunsaek|Màu đen", "하얀색|hayansaek|Màu trắng",
            "주황색|juhwangsaek|Màu cam", "보라색|borasaek|Màu tím", "분홍색|bunhongsaek|Màu hồng", "갈색|galsaek|Màu nâu",
            "회색|hoesaek|Màu xám", "색깔|saekkkal|Màu sắc", "크다|keuda|To / lớn", "작다|jakda|Nhỏ", "많다|manta|Nhiều",
            "적다|jeokda|Ít", "길다|gilda|Dài", "짧다|jjalpda|Ngắn", "높다|nopda|Cao", "낮다|natda|Thấp", "넓다|neolda|Rộng",
            "좁다|jopda|Hẹp", "무겁다|mugeopda|Nặng", "가볍다|gabyeopda|Nhẹ", "빠르다|ppareuda|Nhanh", "느리다|neurida|Chậm",
            "새롭다|saeropda|Mới", "오래되다|oraedoeda|Cũ", "예쁘다|yeppeuda|Đẹp", "멋있다|meositda|Ngầu / phong độ",
            "귀엽다|gwiyeopda|Dễ thương", "쉽다|swipda|Dễ", "어렵다|eoryeopda|Khó", "좋다|jota|Tốt", "나쁘다|nappeuda|Xấu / tệ",
            "뜨겁다|tteugeopda|Nóng (đồ vật)", "차갑다|chagapda|Lạnh (đồ vật)", "덥다|deopda|Nóng (thời tiết)",
            "춥다|chupda|Lạnh (thời tiết)", "비싸다|bissada|Đắt", "싸다|ssada|Rẻ", "깨끗하다|kkaekkeuthada|Sạch sẽ",
            "더럽다|deoreopda|Bẩn", "조용하다|joyonghada|Yên tĩnh", "시끄럽다|sikkeureopda|Ồn ào", "위험하다|wiheomhada|Nguy hiểm",
            "안전하다|anjeonhada|An toàn", "강하다|ganghada|Mạnh", "약하다|yakada|Yếu", "같다|gatda|Giống", "다르다|dareuda|Khác");
        // 6 - Nhà cửa & đồ dùng
        life(6, "방|bang|Phòng", "거실|geosil|Phòng khách", "부엌|bueok|Bếp", "화장실|hwajangsil|Nhà vệ sinh", "욕실|yoksil|Phòng tắm",
            "창문|changmun|Cửa sổ", "의자|uija|Ghế", "책상|chaeksang|Bàn học", "탁자|takja|Cái bàn", "소파|sopa|Ghế sofa",
            "냉장고|naengjanggo|Tủ lạnh", "텔레비전|tellebijeon|Ti vi", "컴퓨터|keompyuteo|Máy tính", "휴대폰|hyudaepon|Điện thoại di động",
            "우산|usan|Cái ô", "열쇠|yeolsoe|Chìa khóa", "거울|geoul|Gương", "칼|kal|Con dao", "옷|ot|Quần áo", "바지|baji|Quần",
            "치마|chima|Váy", "셔츠|syeocheu|Áo sơ mi", "모자|moja|Mũ", "신발|sinbal|Giày dép", "양말|yangmal|Tất",
            "안경|angyeong|Kính mắt", "지갑|jigap|Ví", "돈|don|Tiền", "편지|pyeonji|Thư", "비누|binu|Xà phòng", "수건|sugeon|Khăn tắm",
            "이불|ibul|Chăn", "베개|begae|Gối", "전등|jeondeung|Đèn điện", "에어컨|eeokeon|Điều hòa", "선풍기|seonpunggi|Quạt máy",
            "세탁기|setakgi|Máy giặt", "청소|cheongso|Dọn dẹp", "아파트|apateu|Chung cư", "주소|juso|Địa chỉ");
        // 7 - Địa điểm & giao thông
        life(7, "도서관|doseogwan|Thư viện", "공원|gongwon|Công viên", "가게|gage|Cửa hàng", "편의점|pyeonuijeom|Cửa hàng tiện lợi",
            "백화점|baekhwajeom|Trung tâm thương mại", "마트|mateu|Siêu thị", "약국|yakguk|Hiệu thuốc", "우체국|ucheguk|Bưu điện",
            "경찰서|gyeongchalseo|Đồn công an", "공항|gonghang|Sân bay", "역|yeok|Nhà ga", "호텔|hotel|Khách sạn", "회사|hoesa|Công ty",
            "극장|geukjang|Rạp chiếu phim", "박물관|bangmulgwan|Bảo tàng", "도시|dosi|Thành phố", "시골|sigol|Nông thôn",
            "나라|nara|Đất nước", "한국|hanguk|Hàn Quốc", "베트남|beteunam|Việt Nam", "서울|seoul|Seoul", "일본|ilbon|Nhật Bản",
            "중국|jungguk|Trung Quốc", "미국|miguk|Mỹ", "자동차|jadongcha|Ô tô", "택시|taeksi|Taxi", "자전거|jajeongeo|Xe đạp",
            "기차|gicha|Tàu hỏa", "비행기|bihaenggi|Máy bay", "오른쪽|oreunjjok|Bên phải", "왼쪽|oenjjok|Bên trái", "앞|ap|Phía trước",
            "뒤|dwi|Phía sau", "위|wi|Phía trên", "아래|arae|Phía dưới", "옆|yeop|Bên cạnh", "안|an|Bên trong", "밖|bak|Bên ngoài",
            "여기|yeogi|Ở đây", "거기|geogi|Ở đó", "어디|eodi|Ở đâu", "신호등|sinhodeung|Đèn giao thông");
        // 8 - Trường học & nghề nghiệp
        life(8, "학교|hakgyo|Trường học", "선생님|seonsaengnim|Giáo viên", "학생|haksaeng|Học sinh", "한국어|hangugeo|Tiếng Hàn",
            "이름|ireum|Tên", "교실|gyosil|Lớp học", "수업|sueop|Giờ học", "숙제|sukje|Bài tập về nhà", "시험|siheom|Kỳ thi",
            "질문|jilmun|Câu hỏi", "대답|daedap|Câu trả lời", "단어|daneo|Từ vựng", "문장|munjang|Câu văn", "발음|bareum|Phát âm",
            "연필|yeonpil|Bút chì", "공책|gongchaek|Vở", "사전|sajeon|Từ điển", "대학교|daehakgyo|Đại học", "의사|uisa|Bác sĩ",
            "간호사|ganhosa|Y tá", "경찰|gyeongchal|Cảnh sát", "군인|gunin|Quân nhân", "요리사|yorisa|Đầu bếp", "가수|gasu|Ca sĩ",
            "배우|baeu|Diễn viên", "회사원|hoesawon|Nhân viên công ty", "농부|nongbu|Nông dân", "운전사|unjeonsa|Tài xế",
            "기자|gija|Phóng viên", "변호사|byeonhosa|Luật sư", "공무원|gongmuwon|Công chức", "월급|wolgeup|Lương tháng",
            "사장님|sajangnim|Giám đốc", "직원|jigwon|Nhân viên");
        // 9 - Động từ thường dùng
        life(9, "하다|hada|Làm", "오다|oda|Đến", "가다|gada|Đi", "먹다|meokda|Ăn", "보다|boda|Xem / nhìn", "마시다|masida|Uống",
            "자다|jada|Ngủ", "일어나다|ireonada|Thức dậy", "앉다|anda|Ngồi", "서다|seoda|Đứng", "걷다|geotda|Đi bộ",
            "뛰다|ttwida|Chạy", "말하다|malhada|Nói", "듣다|deutda|Nghe", "읽다|ikda|Đọc", "쓰다|sseuda|Viết / dùng / đắng",
            "사다|sada|Mua", "팔다|palda|Bán", "주다|juda|Cho", "받다|batda|Nhận", "만나다|mannada|Gặp", "알다|alda|Biết",
            "모르다|moreuda|Không biết", "배우다|baeuda|Học (tiếp thu)", "가르치다|gareuchida|Dạy", "좋아하다|joahada|Thích",
            "싫어하다|sireohada|Ghét", "사랑하다|saranghada|Yêu", "놀다|nolda|Chơi", "일하다|ilhada|Làm việc",
            "공부하다|gongbuhada|Học bài", "웃다|utda|Cười", "울다|ulda|Khóc", "열다|yeolda|Mở", "닫다|datda|Đóng",
            "기다리다|gidarida|Chờ đợi", "찾다|chatda|Tìm", "만들다|mandeulda|Làm ra / tạo", "있다|itda|Có / ở",
            "없다|eopda|Không có", "되다|doeda|Trở thành", "시작하다|sijakhada|Bắt đầu", "끝나다|kkeutnada|Kết thúc",
            "씻다|ssitda|Rửa", "입다|ipda|Mặc", "신다|sinda|Đi (giày / tất)", "타다|tada|Đi (xe) / lên xe",
            "내리다|naerida|Xuống", "사용하다|sayonghada|Sử dụng", "생각하다|saenggakhada|Suy nghĩ");
        // 10 - Thiên nhiên & thời tiết
        life(10, "하늘|haneul|Bầu trời", "땅|ttang|Mặt đất", "꽃|kkot|Hoa", "해|hae|Mặt trời", "달|dal|Mặt trăng", "별|byeol|Ngôi sao",
            "비|bi|Mưa", "바다|bada|Biển", "산|san|Núi", "밤|bam|Ban đêm", "낮|nat|Ban ngày", "바람|baram|Gió", "구름|gureum|Mây",
            "맑다|makda|Trong xanh / quang đãng", "흐리다|heurida|U ám", "천둥|cheondung|Sấm", "번개|beongae|Sét",
            "안개|angae|Sương mù", "태풍|taepung|Bão", "무지개|mujigae|Cầu vồng", "봄|bom|Mùa xuân", "여름|yeoreum|Mùa hè",
            "가을|gaeul|Mùa thu", "겨울|gyeoul|Mùa đông", "풀|pul|Cỏ", "잎|ip|Lá cây", "뿌리|ppuri|Rễ cây", "새|sae|Chim",
            "곤충|gonchung|Côn trùng", "나비|nabi|Bướm", "호랑이|horangi|Hổ", "사자|saja|Sư tử", "곰|gom|Gấu", "원숭이|wonsungi|Khỉ",
            "코끼리|kokkiri|Voi", "뱀|baem|Rắn", "쥐|jwi|Chuột", "오리|ori|Vịt", "강아지|gangaji|Chó con", "지구|jigu|Trái đất",
            "우주|uju|Vũ trụ", "공기|gonggi|Không khí", "온도|ondo|Nhiệt độ", "동쪽|dongjjok|Phía đông", "서쪽|seojjok|Phía tây",
            "남쪽|namjjok|Phía nam", "북쪽|bukjjok|Phía bắc");
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

    static final List<Dialogue> DIALOGUES = new ArrayList<>();
    private static void d(int sec, String npc, String npcVi, String right, String rightVi,
                          String wa, String waVi, String wb, String wbVi) {
        DIALOGUES.add(new Dialogue(npc, npcVi, right, rightVi, wa, waVi, wb, wbVi, sec));
    }
    static List<Integer> dialogueIds(int src) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < DIALOGUES.size(); i++) {
            int s = DIALOGUES.get(i).sec();
            if (src == 0 || (src == 1 && s == 0) || (src == 2 && s == 1)) out.add(i);
        }
        return out;
    }

    static {
        // --- 7 hội thoại gốc (giữ nguyên thứ tự để tiến độ cũ vẫn hợp lệ)
        d(1, "안녕하세요!", "Xin chào!", "안녕하세요!", "Xin chào!", "감사합니다!", "Cảm ơn!", "아니요!", "Không!");
        d(1, "이름이 뭐예요?", "Bạn tên là gì?", "제 이름은 스티브예요.", "Tên tôi là Steve.", "네, 좋아요.", "Vâng, tốt.", "안녕히 가세요.", "Tạm biệt.");
        d(0, "사과 드세요.", "Mời bạn ăn táo.", "감사합니다!", "Cảm ơn!", "안녕하세요?", "Xin chào?", "다이아몬드예요.", "Là kim cương.");
        d(1, "죄송합니다!", "Tôi xin lỗi!", "괜찮아요.", "Không sao đâu.", "감사합니다.", "Cảm ơn.", "반가워요.", "Rất vui được gặp.");
        d(1, "안녕히 가세요!", "Tạm biệt nhé!", "안녕히 계세요!", "Tạm biệt (người ở lại)!", "이름이 뭐예요?", "Bạn tên là gì?", "사과 주세요.", "Cho tôi táo.");
        d(1, "한국어 좋아해요?", "Bạn có thích tiếng Hàn không?", "네, 좋아해요!", "Vâng, tôi thích!", "죄송합니다!", "Xin lỗi!", "안녕히 가세요!", "Tạm biệt!");
        d(0, "도와주세요!", "Hãy giúp tôi với!", "네, 도와줄게요!", "Vâng, tôi sẽ giúp!", "빵이에요.", "Là bánh mì.", "안녕히 계세요.", "Tạm biệt.");

        // --- Minecraft
        d(0, "다이아몬드를 찾았어요?", "Bạn tìm thấy kim cương chưa?", "네, 동굴에서 찾았어요!", "Vâng, tôi tìm thấy trong hang!", "안녕히 계세요.", "Tạm biệt.", "저는 학생이에요.", "Tôi là học sinh.");
        d(0, "크리퍼가 왔어요! 조심하세요!", "Creeper đến rồi! Cẩn thận!", "고마워요! 도망갈게요!", "Cảm ơn! Tôi sẽ chạy!", "맛있어요!", "Ngon quá!", "생일 축하해요!", "Chúc mừng sinh nhật!");
        d(0, "밤이에요. 침대에서 자요!", "Đêm rồi. Hãy ngủ trên giường!", "네, 잠을 잘게요.", "Vâng, tôi sẽ ngủ.", "아니요, 학교에 가요.", "Không, tôi đi học.", "물을 마셔요.", "Tôi uống nước.");
        d(0, "무엇이 필요해요?", "Bạn cần gì?", "곡괭이가 필요해요.", "Tôi cần cuốc chim.", "저는 열 살이에요.", "Tôi mười tuổi.", "비가 와요.", "Trời mưa.");
        d(0, "철 검은 얼마예요?", "Kiếm sắt giá bao nhiêu?", "에메랄드 다섯 개예요.", "Năm viên ngọc lục bảo.", "내일 만나요.", "Mai gặp nhé.", "저는 고양이를 좋아해요.", "Tôi thích mèo.");
        d(0, "배가 고파요.", "Tôi đói.", "빵을 드세요.", "Mời ăn bánh mì.", "침대가 커요.", "Giường to quá.", "안녕하세요?", "Xin chào?");
        d(0, "집이 어디에 있어요?", "Nhà bạn ở đâu?", "산 옆에 있어요.", "Ở cạnh núi.", "네, 맞아요.", "Vâng, đúng rồi.", "삼천 원이에요.", "Ba nghìn won.");
        d(0, "좀비가 무서워요?", "Bạn có sợ zombie không?", "조금 무서워요.", "Hơi sợ.", "맛있어요.", "Ngon.", "책을 읽어요.", "Tôi đọc sách.");
        d(0, "횃불이 있어요?", "Bạn có đuốc không?", "네, 여기 있어요.", "Vâng, đây ạ.", "아니요, 어제예요.", "Không, là hôm qua.", "다이아몬드는 비싸요.", "Kim cương đắt.");
        d(0, "같이 집을 지어요!", "Cùng xây nhà nhé!", "좋아요! 같이 해요.", "Được! Cùng làm nhé.", "안녕히 계세요.", "Tạm biệt.", "저는 의사예요.", "Tôi là bác sĩ.");
        d(0, "엔더 드래곤이 무서워요!", "Rồng Ender đáng sợ quá!", "괜찮아요. 같이 싸워요!", "Không sao. Cùng chiến đấu nhé!", "감사합니다.", "Cảm ơn.", "내일은 월요일이에요.", "Mai là thứ hai.");
        d(0, "어느 모드로 놀아요?", "Bạn chơi chế độ nào?", "저는 생존 모드로 놀아요.", "Tôi chơi chế độ sinh tồn.", "김치를 좋아해요.", "Tôi thích kim chi.", "화장실이 어디예요?", "Nhà vệ sinh ở đâu?");

        // --- Đời sống
        d(1, "어디에서 왔어요?", "Bạn đến từ đâu?", "베트남에서 왔어요.", "Tôi đến từ Việt Nam.", "커피를 마셔요.", "Tôi uống cà phê.", "오늘은 월요일이에요.", "Hôm nay là thứ hai.");
        d(1, "몇 살이에요?", "Bạn bao nhiêu tuổi?", "스무 살이에요.", "Tôi hai mươi tuổi.", "서울에 살아요.", "Tôi sống ở Seoul.", "김치를 좋아해요.", "Tôi thích kim chi.");
        d(1, "무슨 일을 해요?", "Bạn làm nghề gì?", "저는 학생이에요.", "Tôi là học sinh.", "네, 맞아요.", "Vâng, đúng rồi.", "지금 세 시예요.", "Bây giờ là ba giờ.");
        d(1, "지금 몇 시예요?", "Bây giờ là mấy giờ?", "두 시 삼십 분이에요.", "Hai giờ ba mươi phút.", "만 원이에요.", "Mười nghìn won.", "친구가 있어요.", "Tôi có bạn.");
        d(1, "뭐 드시겠어요?", "Bạn dùng món gì ạ?", "비빔밥 주세요.", "Cho tôi cơm trộn.", "안녕히 가세요.", "Tạm biệt.", "화장실이 어디예요?", "Nhà vệ sinh ở đâu?");
        d(1, "화장실이 어디예요?", "Nhà vệ sinh ở đâu?", "저기 오른쪽에 있어요.", "Ở bên phải đằng kia.", "아이스크림이에요.", "Là kem.", "네, 괜찮아요.", "Vâng, không sao.");
        d(1, "오늘 날씨가 어때요?", "Hôm nay thời tiết thế nào?", "맑고 따뜻해요.", "Trời trong và ấm.", "삼천 원이에요.", "Ba nghìn won.", "저는 의사예요.", "Tôi là bác sĩ.");
        d(1, "주말에 뭐 해요?", "Cuối tuần bạn làm gì?", "친구를 만나요.", "Tôi gặp bạn.", "맛이 달아요.", "Vị ngọt.", "죄송합니다.", "Xin lỗi.");
        d(1, "생일이 언제예요?", "Sinh nhật bạn khi nào?", "시월 오 일이에요.", "Ngày 5 tháng 10.", "학교에 가요.", "Tôi đi học.", "고양이가 귀여워요.", "Mèo dễ thương.");
        d(1, "이거 얼마예요?", "Cái này bao nhiêu tiền?", "오천 원이에요.", "Năm nghìn won.", "내일 만나요.", "Mai gặp nhé.", "안녕하세요?", "Xin chào?");
        d(1, "한국어가 어려워요?", "Tiếng Hàn có khó không?", "조금 어렵지만 재미있어요.", "Hơi khó nhưng thú vị.", "오늘은 월요일이에요.", "Hôm nay là thứ hai.", "저는 가수예요.", "Tôi là ca sĩ.");
        d(1, "배고파요?", "Bạn đói không?", "네, 배고파요. 밥 먹어요.", "Vâng, đói rồi. Ăn cơm thôi.", "저는 열 살이에요.", "Tôi mười tuổi.", "안녕히 가세요.", "Tạm biệt.");
        d(1, "도서관에 가요?", "Bạn đi thư viện à?", "네, 책을 읽어요.", "Vâng, tôi đọc sách.", "맛있어요.", "Ngon.", "삼 월이에요.", "Là tháng ba.");
        d(1, "가족이 몇 명이에요?", "Gia đình bạn có mấy người?", "네 명이에요.", "Có bốn người.", "맵지 않아요.", "Không cay.", "저기에 있어요.", "Ở đằng kia.");

        // --- TOPIK 1-2: đời sống (làm quen, mua sắm, giao thông, ăn uống, sức khỏe, điện thoại, du lịch)
        d(1, "성함이 어떻게 되세요?", "Quý danh của bạn là gì ạ?", "김민수라고 해요.", "Tôi tên là Kim Minsu.", "네, 잘 먹겠습니다.", "Vâng, tôi xin phép ăn.", "지하철을 타요.", "Tôi đi tàu điện ngầm.");
        d(1, "만나서 반갑습니다.", "Rất vui được gặp bạn.", "저도 만나서 반갑습니다.", "Tôi cũng rất vui được gặp bạn.", "아니요, 없어요.", "Không, không có.", "삼천 원이에요.", "Ba nghìn won.");
        d(1, "한국 사람이에요?", "Bạn là người Hàn Quốc à?", "아니요, 베트남 사람이에요.", "Không, tôi là người Việt Nam.", "네, 맛있어요.", "Vâng, ngon lắm.", "오늘 비가 와요.", "Hôm nay trời mưa.");
        d(1, "전화번호가 뭐예요?", "Số điện thoại của bạn là gì?", "공일공 일이삼사 오육칠팔이에요.", "Là 010-1234-5678.", "저는 의사예요.", "Tôi là bác sĩ.", "내일 갈게요.", "Mai tôi sẽ đi.");
        d(1, "집이 어디예요?", "Nhà bạn ở đâu?", "서울에 있어요.", "Ở Seoul.", "열 시예요.", "Mười giờ.", "맛이 짜요.", "Vị mặn.");
        d(1, "무엇을 좋아해요?", "Bạn thích gì?", "저는 음악을 좋아해요.", "Tôi thích âm nhạc.", "안녕히 가세요.", "Tạm biệt.", "오른쪽에 있어요.", "Ở bên phải.");
        d(1, "취미가 뭐예요?", "Sở thích của bạn là gì?", "제 취미는 영화 보기예요.", "Sở thích của tôi là xem phim.", "지금 두 시예요.", "Bây giờ là hai giờ.", "아주 비싸요.", "Rất đắt.");
        d(1, "어제 뭐 했어요?", "Hôm qua bạn đã làm gì?", "친구를 만났어요.", "Tôi đã gặp bạn.", "내일 갈 거예요.", "Mai tôi sẽ đi.", "네, 맛있어요.", "Vâng, ngon.");
        d(1, "내일 시간 있어요?", "Mai bạn có rảnh không?", "네, 시간 있어요.", "Vâng, tôi rảnh.", "네, 사과예요.", "Vâng, là quả táo.", "저는 학생이에요.", "Tôi là học sinh.");
        d(1, "몇 시에 만나요?", "Mấy giờ chúng ta gặp nhau?", "일곱 시에 만나요.", "Bảy giờ gặp nhé.", "시장에 가요.", "Tôi đi chợ.", "삼천 원이에요.", "Ba nghìn won.");
        d(1, "어디에서 만나요?", "Chúng ta gặp ở đâu?", "학교 앞에서 만나요.", "Gặp trước cổng trường nhé.", "여섯 시예요.", "Sáu giờ.", "맛있게 드세요.", "Chúc ngon miệng.");
        d(1, "무엇을 드릴까요?", "Tôi lấy gì cho bạn ạ?", "커피 한 잔 주세요.", "Cho tôi một ly cà phê.", "저는 열 살이에요.", "Tôi mười tuổi.", "안녕히 계세요.", "Tạm biệt.");
        d(1, "여기에서 드세요, 가져가세요?", "Bạn ăn ở đây hay mang đi?", "가져갈게요.", "Tôi mang đi.", "삼 월이에요.", "Là tháng ba.", "아주 커요.", "Rất to.");
        d(1, "계산은 어떻게 하세요?", "Bạn thanh toán thế nào?", "카드로 할게요.", "Tôi trả bằng thẻ.", "산에 가요.", "Tôi đi núi.", "한국어를 배워요.", "Tôi học tiếng Hàn.");
        d(1, "더 필요한 것 있어요?", "Bạn cần thêm gì không?", "아니요, 괜찮아요.", "Không, ổn rồi.", "내일은 일요일이에요.", "Mai là Chủ nhật.", "네, 학교예요.", "Vâng, là trường học.");
        d(1, "얼마나 걸려요?", "Mất bao lâu?", "삼십 분쯤 걸려요.", "Mất khoảng ba mươi phút.", "오천 원이에요.", "Năm nghìn won.", "저는 가수예요.", "Tôi là ca sĩ.");
        d(1, "이 버스는 시청에 가요?", "Xe buýt này có đến tòa thị chính không?", "네, 가요.", "Có, nó đi.", "아니요, 맛있어요.", "Không, ngon lắm.", "저는 열 살이에요.", "Tôi mười tuổi.");
        d(1, "어디에서 내려요?", "Bạn xuống ở đâu?", "다음 역에서 내려요.", "Tôi xuống ở ga tiếp theo.", "빨간색이에요.", "Màu đỏ.", "고양이가 있어요.", "Có một con mèo.");
        d(1, "길을 잃었어요.", "Tôi bị lạc đường.", "제가 도와드릴게요.", "Để tôi giúp bạn.", "안녕히 주무세요.", "Chúc ngủ ngon.", "맛이 매워요.", "Vị cay.");
        d(1, "지하철역이 어디예요?", "Ga tàu điện ngầm ở đâu?", "똑바로 가세요.", "Hãy đi thẳng.", "생일이에요.", "Là sinh nhật.", "사과 한 개예요.", "Một quả táo.");
        d(1, "우체국이 어디에 있어요?", "Bưu điện ở đâu?", "은행 옆에 있어요.", "Ở cạnh ngân hàng.", "열두 시예요.", "Mười hai giờ.", "한국어는 재미있어요.", "Tiếng Hàn thú vị.");
        d(1, "여기에서 멀어요?", "Chỗ đó có xa đây không?", "아니요, 가까워요.", "Không, gần thôi.", "네, 반가워요.", "Vâng, rất vui được gặp.", "책을 읽어요.", "Tôi đọc sách.");
        d(1, "무슨 음식을 좋아해요?", "Bạn thích món ăn nào?", "저는 김치찌개를 좋아해요.", "Tôi thích canh kim chi.", "지금 도착했어요.", "Tôi vừa đến nơi.", "병원에 가요.", "Tôi đi bệnh viện.");
        d(1, "매운 음식을 먹을 수 있어요?", "Bạn ăn được đồ cay không?", "아니요, 매워서 못 먹어요.", "Không, cay quá tôi không ăn được.", "네, 학교에 가요.", "Vâng, tôi đi học.", "저는 선생님이에요.", "Tôi là giáo viên.");
        d(1, "물 좀 주세요.", "Cho tôi ít nước.", "네, 여기 있어요.", "Vâng, đây ạ.", "아니요, 내일이에요.", "Không, là ngày mai.", "생일 축하해요.", "Chúc mừng sinh nhật.");
        d(1, "맛이 어때요?", "Vị thế nào?", "아주 맛있어요.", "Rất ngon.", "저는 가수예요.", "Tôi là ca sĩ.", "집이 커요.", "Nhà to.");
        d(1, "배불러요?", "Bạn no chưa?", "네, 많이 먹었어요.", "Vâng, tôi đã ăn nhiều.", "아니요, 월요일이에요.", "Không, là thứ hai.", "안녕하세요?", "Xin chào?");
        d(1, "머리가 아파요.", "Tôi bị đau đầu.", "약을 드세요.", "Hãy uống thuốc.", "수박을 주세요.", "Cho tôi dưa hấu.", "기차를 타요.", "Tôi đi tàu hỏa.");
        d(1, "병원에 가요?", "Bạn đi bệnh viện à?", "네, 배가 아파요.", "Vâng, tôi đau bụng.", "아니요, 연필이에요.", "Không, là bút chì.", "저는 요리사예요.", "Tôi là đầu bếp.");
        d(1, "어디가 아파요?", "Bạn đau ở đâu?", "목이 아파요.", "Tôi đau cổ.", "서울에 가요.", "Tôi đi Seoul.", "열두 시예요.", "Mười hai giờ.");
        d(1, "감기에 걸렸어요.", "Tôi bị cảm.", "푹 쉬세요.", "Hãy nghỉ ngơi cho khỏe.", "맛있게 드세요.", "Chúc ngon miệng.", "삼 월이에요.", "Là tháng ba.");
        d(1, "오늘 기분이 어때요?", "Hôm nay bạn thấy thế nào?", "아주 좋아요.", "Rất tốt.", "도서관이에요.", "Là thư viện.", "일곱 개예요.", "Bảy cái.");
        d(1, "왜 늦었어요?", "Sao bạn đến muộn?", "길이 막혔어요.", "Đường bị tắc.", "사과가 맛있어요.", "Táo ngon.", "저는 학생이에요.", "Tôi là học sinh.");
        d(1, "숙제 했어요?", "Bạn làm bài tập chưa?", "네, 다 했어요.", "Rồi, tôi làm xong hết rồi.", "아니요, 서울이에요.", "Không, là Seoul.", "빨간색이에요.", "Màu đỏ.");
        d(1, "시험이 언제예요?", "Khi nào thi?", "다음 주 금요일이에요.", "Thứ sáu tuần sau.", "백화점에 가요.", "Tôi đi trung tâm thương mại.", "저는 형이에요.", "Tôi là anh.");
        d(1, "한국어를 어디에서 배웠어요?", "Bạn đã học tiếng Hàn ở đâu?", "학교에서 배웠어요.", "Tôi học ở trường.", "삼천 원이에요.", "Ba nghìn won.", "맵고 짜요.", "Cay và mặn.");
        d(1, "한국어를 잘하시네요!", "Bạn giỏi tiếng Hàn quá!", "아니에요, 아직 부족해요.", "Không đâu, tôi còn kém lắm.", "네, 안녕히 가세요.", "Vâng, tạm biệt.", "내일은 비가 와요.", "Mai trời mưa.");
        d(1, "다시 말해 주세요.", "Hãy nói lại lần nữa.", "네, 다시 말할게요.", "Vâng, tôi sẽ nói lại.", "아니요, 사과예요.", "Không, là quả táo.", "저는 의사예요.", "Tôi là bác sĩ.");
        d(1, "무슨 뜻이에요?", "Nghĩa là gì vậy?", "친구라는 뜻이에요.", "Nghĩa là bạn bè.", "일곱 시예요.", "Bảy giờ.", "버스를 타요.", "Tôi đi xe buýt.");
        d(1, "날씨가 더워요.", "Trời nóng.", "에어컨을 켤까요?", "Bật điều hòa nhé?", "감사합니다, 안녕히 가세요.", "Cảm ơn, tạm biệt.", "책이 많아요.", "Có nhiều sách.");
        d(1, "내일 비가 올까요?", "Mai có mưa không nhỉ?", "네, 비가 올 거예요.", "Vâng, chắc sẽ mưa.", "저는 가수예요.", "Tôi là ca sĩ.", "두 개 주세요.", "Cho tôi hai cái.");
        d(1, "무슨 계절을 좋아해요?", "Bạn thích mùa nào?", "저는 가을을 좋아해요.", "Tôi thích mùa thu.", "일곱 시에 가요.", "Bảy giờ tôi đi.", "화장실에 있어요.", "Ở nhà vệ sinh.");
        d(1, "겨울에 뭐 해요?", "Mùa đông bạn làm gì?", "스키를 타요.", "Tôi trượt tuyết.", "열 개예요.", "Mười cái.", "수박을 먹었어요.", "Tôi đã ăn dưa hấu.");
        d(1, "이 옷 입어 봐도 돼요?", "Tôi mặc thử bộ này được không?", "네, 입어 보세요.", "Được, mời bạn mặc thử.", "아니요, 맛있어요.", "Không, ngon lắm.", "저는 선생님이에요.", "Tôi là giáo viên.");
        d(1, "다른 색깔 있어요?", "Có màu khác không?", "네, 파란색도 있어요.", "Có, còn có màu xanh dương.", "아니요, 월요일이에요.", "Không, là thứ hai.", "사십 분이에요.", "Bốn mươi phút.");
        d(1, "좀 깎아 주세요.", "Giảm giá cho tôi chút đi.", "죄송해요, 안 돼요.", "Xin lỗi, không được ạ.", "오늘은 맑아요.", "Hôm nay trời quang.", "열 시에 일어나요.", "Mười giờ tôi dậy.");
        d(1, "봉투 필요하세요?", "Bạn có cần túi không?", "네, 하나 주세요.", "Có, cho tôi một cái.", "내일 만나요.", "Mai gặp nhé.", "맛이 달아요.", "Vị ngọt.");
        d(1, "영수증 드릴까요?", "Bạn có lấy hóa đơn không?", "네, 주세요.", "Vâng, cho tôi.", "저는 열 살이에요.", "Tôi mười tuổi.", "서울 지하철이에요.", "Là tàu điện ngầm Seoul.");
        d(1, "몇 층이에요?", "Tầng mấy vậy?", "삼 층이에요.", "Tầng ba.", "일곱 시예요.", "Bảy giờ.", "매운 김치예요.", "Là kim chi cay.");
        d(1, "누구랑 같이 살아요?", "Bạn sống cùng ai?", "가족이랑 같이 살아요.", "Tôi sống cùng gia đình.", "버스로 가요.", "Tôi đi bằng xe buýt.", "오후 세 시예요.", "Ba giờ chiều.");
        d(1, "형제가 있어요?", "Bạn có anh chị em không?", "네, 여동생이 있어요.", "Có, tôi có một em gái.", "아니요, 비싸요.", "Không, đắt.", "저는 빨간색을 좋아해요.", "Tôi thích màu đỏ.");
        d(1, "아버지는 무슨 일을 하세요?", "Bố bạn làm nghề gì?", "회사원이세요.", "Bố tôi là nhân viên công ty.", "어제 갔어요.", "Hôm qua tôi đã đi.", "아주 매워요.", "Rất cay.");
        d(1, "몇 시에 일어나요?", "Bạn dậy lúc mấy giờ?", "여섯 시에 일어나요.", "Tôi dậy lúc sáu giờ.", "학교에서 먹어요.", "Tôi ăn ở trường.", "만 원이에요.", "Mười nghìn won.");
        d(1, "아침에 뭐 먹어요?", "Buổi sáng bạn ăn gì?", "빵과 우유를 먹어요.", "Tôi ăn bánh mì và uống sữa.", "도서관에 가요.", "Tôi đi thư viện.", "한국에서 왔어요.", "Tôi đến từ Hàn Quốc.");
        d(1, "저녁에 뭐 해요?", "Buổi tối bạn làm gì?", "집에서 쉬어요.", "Tôi nghỉ ngơi ở nhà.", "만 이천 원이에요.", "Mười hai nghìn won.", "검은색이에요.", "Màu đen.");
        d(1, "운동을 좋아해요?", "Bạn có thích thể thao không?", "네, 매일 걸어요.", "Có, tôi đi bộ mỗi ngày.", "아니요, 서울이에요.", "Không, là Seoul.", "안녕히 계세요.", "Tạm biệt.");
        d(1, "어떤 영화를 좋아해요?", "Bạn thích loại phim nào?", "저는 코미디를 좋아해요.", "Tôi thích phim hài.", "삼 층에 있어요.", "Ở tầng ba.", "아침에 먹었어요.", "Tôi đã ăn sáng.");
        d(1, "같이 영화 볼까요?", "Cùng xem phim nhé?", "좋아요! 같이 가요.", "Được! Cùng đi nhé.", "아니요, 의사예요.", "Không, là bác sĩ.", "죄송합니다, 월요일이에요.", "Xin lỗi, là thứ hai.");
        d(1, "사진 좀 찍어 주세요.", "Chụp ảnh giúp tôi với.", "네, 찍어 드릴게요.", "Vâng, để tôi chụp cho.", "아니요, 맛있어요.", "Không, ngon lắm.", "여섯 시예요.", "Sáu giờ.");
        d(1, "전화 받으세요.", "Nghe điện thoại đi.", "여보세요?", "A lô?", "안녕히 가세요.", "Tạm biệt.", "이 옷이 예뻐요.", "Bộ đồ này đẹp.");
        d(1, "여보세요, 민수 씨 계세요?", "A lô, anh Minsu có ở đó không?", "잠시만 기다리세요.", "Xin chờ một chút.", "네, 사과예요.", "Vâng, là quả táo.", "저는 학생이에요.", "Tôi là học sinh.");
        d(1, "죄송하지만 지금 바빠요.", "Xin lỗi nhưng bây giờ tôi bận.", "괜찮아요. 나중에 연락할게요.", "Không sao. Lát nữa tôi liên lạc.", "맛있게 드세요.", "Chúc ngon miệng.", "네, 삼천 원이에요.", "Vâng, ba nghìn won.");
        d(1, "여행 가고 싶어요?", "Bạn muốn đi du lịch không?", "네, 제주도에 가고 싶어요.", "Vâng, tôi muốn đi đảo Jeju.", "아니요, 숙제예요.", "Không, là bài tập.", "저는 요리사예요.", "Tôi là đầu bếp.");
        d(1, "비행기 표를 예약했어요?", "Bạn đã đặt vé máy bay chưa?", "네, 어제 예약했어요.", "Rồi, hôm qua tôi đã đặt.", "아니요, 매워요.", "Không, cay.", "열 개 있어요.", "Có mười cái.");
        d(1, "여권 있어요?", "Bạn có hộ chiếu không?", "네, 여기 있어요.", "Có, đây ạ.", "아니요, 맛있어요.", "Không, ngon lắm.", "내일 비가 와요.", "Mai trời mưa.");
        d(1, "방을 예약하고 싶어요.", "Tôi muốn đặt phòng.", "며칠 묵으세요?", "Bạn ở mấy ngày ạ?", "저는 가수예요.", "Tôi là ca sĩ.", "맛이 짜요.", "Vị mặn.");
        d(1, "생일 선물로 뭐가 좋아요?", "Quà sinh nhật thì gì là tốt?", "꽃이 좋아요.", "Hoa thì tốt.", "삼십 분이에요.", "Ba mươi phút.", "내일 갈게요.", "Mai tôi sẽ đi.");
        d(1, "선물 고마워요!", "Cảm ơn món quà!", "마음에 들어요?", "Bạn có thích không?", "안녕히 가세요.", "Tạm biệt.", "이천 원이에요.", "Hai nghìn won.");
        d(1, "오늘 정말 즐거웠어요.", "Hôm nay thật vui.", "저도요. 또 만나요!", "Tôi cũng vậy. Gặp lại nhé!", "아니요, 비싸요.", "Không, đắt.", "화장실에 가요.", "Tôi đi nhà vệ sinh.");
        d(1, "건강 조심하세요.", "Hãy giữ gìn sức khỏe nhé.", "네, 감사합니다.", "Vâng, cảm ơn bạn.", "내일은 목요일이에요.", "Mai là thứ năm.", "빨간색을 주세요.", "Cho tôi màu đỏ.");
        d(1, "여기 앉아도 돼요?", "Tôi ngồi đây được không?", "네, 앉으세요.", "Được, mời bạn ngồi.", "아니요, 서울이에요.", "Không, là Seoul.", "열 시에 일어나요.", "Mười giờ tôi dậy.");
        d(1, "창문을 열어도 돼요?", "Tôi mở cửa sổ được không?", "네, 열어도 돼요.", "Vâng, bạn mở được.", "저는 사과를 먹어요.", "Tôi ăn táo.", "만 원이에요.", "Mười nghìn won.");
        d(1, "숙제가 너무 많아요.", "Bài tập nhiều quá.", "힘내세요!", "Cố lên nhé!", "생일 축하해요.", "Chúc mừng sinh nhật.", "삼십 분 걸려요.", "Mất ba mươi phút.");
        d(1, "한국 음식을 만들 수 있어요?", "Bạn nấu được món Hàn không?", "김밥을 만들 수 있어요.", "Tôi làm được cơm cuộn.", "일곱 시에 일어나요.", "Bảy giờ tôi dậy.", "아니요, 지하철역이에요.", "Không, là ga tàu điện.");
        d(1, "오늘은 무슨 요일이에요?", "Hôm nay là thứ mấy?", "오늘은 토요일이에요.", "Hôm nay là thứ bảy.", "다섯 시예요.", "Năm giờ.", "삼천 원이에요.", "Ba nghìn won.");
        d(1, "오늘은 며칠이에요?", "Hôm nay là ngày mấy?", "시월 십칠 일이에요.", "Ngày 17 tháng 10.", "열일곱 개예요.", "Mười bảy cái.", "빨간색이에요.", "Màu đỏ.");

        // --- TOPIK 1-2: Minecraft
        d(0, "어디에서 광석을 캤어요?", "Bạn đào quặng ở đâu?", "동굴에서 캤어요.", "Tôi đào trong hang.", "내일 학교에 가요.", "Mai tôi đi học.", "맛있게 먹었어요.", "Tôi đã ăn ngon.");
        d(0, "오늘 밤에 뭐 해요?", "Tối nay bạn làm gì?", "집을 지을 거예요.", "Tôi sẽ xây nhà.", "삼천 원이에요.", "Ba nghìn won.", "저는 의사예요.", "Tôi là bác sĩ.");
        d(0, "무슨 동물을 키워요?", "Bạn nuôi con vật gì?", "고양이와 개를 키워요.", "Tôi nuôi mèo và chó.", "곡괭이로 캐요.", "Tôi đào bằng cuốc chim.", "열 시에 자요.", "Mười giờ tôi ngủ.");
        d(0, "철이 몇 개 필요해요?", "Bạn cần bao nhiêu sắt?", "세 개 필요해요.", "Tôi cần ba cái.", "동굴에 있어요.", "Ở trong hang.", "아주 맛있어요.", "Rất ngon.");
        d(0, "다이아몬드 곡괭이를 만들었어요?", "Bạn làm cuốc kim cương chưa?", "네, 방금 만들었어요.", "Rồi, tôi vừa làm xong.", "아니요, 월요일이에요.", "Không, là thứ hai.", "안녕히 가세요.", "Tạm biệt.");
        d(0, "배고파요. 먹을 것이 있어요?", "Tôi đói. Bạn có gì ăn không?", "네, 빵과 사과가 있어요.", "Có, tôi có bánh mì và táo.", "아니요, 침대예요.", "Không, là cái giường.", "저는 학생이에요.", "Tôi là học sinh.");
        d(0, "횃불을 어디에 놓을까요?", "Đặt đuốc ở đâu nhỉ?", "문 옆에 놓으세요.", "Hãy đặt cạnh cửa.", "감사합니다, 안녕히 계세요.", "Cảm ơn, tạm biệt.", "일곱 시예요.", "Bảy giờ.");
        d(0, "저기 거미가 있어요!", "Có con nhện ở kia!", "무서워요! 도망가요!", "Sợ quá! Chạy thôi!", "맛있어요!", "Ngon quá!", "사과 주세요.", "Cho tôi táo.");
        d(0, "농장에서 뭘 키워요?", "Bạn trồng gì ở nông trại?", "밀과 당근을 키워요.", "Tôi trồng lúa mì và cà rốt.", "서울에 살아요.", "Tôi sống ở Seoul.", "십 분 걸려요.", "Mất mười phút.");
        d(0, "마을 주민과 거래해요?", "Bạn có giao dịch với dân làng không?", "네, 에메랄드를 받아요.", "Có, tôi nhận ngọc lục bảo.", "아니요, 비가 와요.", "Không, trời mưa.", "저는 선생님이에요.", "Tôi là giáo viên.");
        d(0, "네더에 가 봤어요?", "Bạn từng đến Nether chưa?", "아니요, 아직 못 가 봤어요.", "Chưa, tôi vẫn chưa đến đó.", "네, 맛있었어요.", "Vâng, đã rất ngon.", "삼 층에 있어요.", "Ở tầng ba.");
        d(0, "집에 문이 몇 개 있어요?", "Nhà bạn có mấy cái cửa?", "문이 두 개 있어요.", "Có hai cái cửa.", "아주 빨라요.", "Rất nhanh.", "안녕히 가세요.", "Tạm biệt.");
        d(0, "좌표가 어디예요?", "Tọa độ ở đâu vậy?", "백이십, 육십이에요.", "Là 120, 62.", "친구를 만나요.", "Tôi gặp bạn.", "아니요, 괜찮아요.", "Không, ổn rồi.");
        d(0, "같이 서버에서 놀까요?", "Cùng chơi trên server nhé?", "좋아요! 몇 시에 해요?", "Được! Mấy giờ chơi?", "죄송합니다, 치마예요.", "Xin lỗi, là cái váy.", "저는 빨간색이에요.", "Tôi là màu đỏ.");
        d(0, "엔더맨이 나타났어요!", "Enderman xuất hiện rồi!", "쳐다보지 마세요!", "Đừng nhìn nó!", "생일 축하해요.", "Chúc mừng sinh nhật.", "배고파요.", "Tôi đói.");
        d(0, "레드스톤이 필요해요.", "Tôi cần đá đỏ.", "광산에서 구할 수 있어요.", "Bạn có thể tìm ở mỏ.", "저는 열 살이에요.", "Tôi mười tuổi.", "맛이 매워요.", "Vị cay.");
        d(0, "낚시를 좋아해요?", "Bạn có thích câu cá không?", "네, 물고기를 잡아요.", "Có, tôi bắt cá.", "아니요, 학교예요.", "Không, là trường học.", "열 개 주세요.", "Cho tôi mười cái.");
        d(0, "비가 와요. 어디로 가요?", "Trời mưa rồi. Bạn đi đâu?", "집으로 들어가요.", "Tôi vào nhà.", "삼천 원이에요.", "Ba nghìn won.", "책이 재미있어요.", "Sách thú vị.");
        d(0, "생존 모드가 어려워요?", "Chế độ sinh tồn có khó không?", "처음에는 어렵지만 재미있어요.", "Lúc đầu khó nhưng vui.", "저는 가수예요.", "Tôi là ca sĩ.", "내일은 일요일이에요.", "Mai là Chủ nhật.");
        d(0, "침대를 만들고 싶어요.", "Tôi muốn làm cái giường.", "양털 세 개와 판자가 필요해요.", "Cần ba tấm len và ván gỗ.", "저는 서울에 살아요.", "Tôi sống ở Seoul.", "비빔밥을 주세요.", "Cho tôi cơm trộn.");
        d(0, "이 집은 누가 지었어요?", "Ngôi nhà này ai xây vậy?", "제가 지었어요.", "Tôi xây đấy.", "삼 월이에요.", "Là tháng ba.", "맛있게 드세요.", "Chúc ngon miệng.");
        d(0, "저 산 위에 뭐가 있어요?", "Trên ngọn núi kia có gì?", "눈이 많이 있어요.", "Có rất nhiều tuyết.", "저는 의사예요.", "Tôi là bác sĩ.", "내일 만나요.", "Mai gặp nhé.");
        d(0, "친구가 길을 잃었어요!", "Bạn tôi bị lạc rồi!", "같이 찾으러 가요.", "Cùng đi tìm nhé.", "생일 축하해요.", "Chúc mừng sinh nhật.", "치킨 주세요.", "Cho tôi gà rán.");
    }

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
