# Zhwii KoreanCraft 26.2 — v0.1.0 starter

Đây là **bản source thử nghiệm**, chưa phải file JAR đã build và chưa có đủ các tính năng dự kiến. Nhấn phím **K** để lần lượt xem 12 từ tiếng Hàn kèm nghĩa **tiếng Việt** trong chat.

## Ngôn ngữ
- Nghĩa từ vựng hiện mặc định bằng tiếng Việt trong bản thử nghiệm.
- `assets/koreancraft/lang/vi_vn.json`: bản dịch tên phím tắt và danh mục điều khiển tiếng Việt.
- `en_us.json`: bản dự phòng tiếng Anh cho người dùng đặt ngôn ngữ game khác.
- Để hiện nhãn phím tắt bằng tiếng Việt, chọn Tiếng Việt trong cài đặt ngôn ngữ Minecraft.

## Build
Cần JDK 25. Đưa toàn bộ nội dung project lên GitHub, vào Actions > Build KoreanCraft 26.2 > Run workflow, sau đó tải artifact; hoặc thử `gradle build` với Gradle 9.5.1 và Java 25.

## Phụ thuộc
Minecraft 26.2, Fabric Loader 0.19.5, Fabric API 0.156.0+26.2, Loom 1.17.17. Có thể cần điều chỉnh theo artifact Maven thực tế. Project chưa được xác minh build thành công; nếu constructor KeyMapping của bản 26.2 thay đổi, cần cập nhật KoreanCraftClient.java.

## Dự kiến
- v0.1.1: GUI, quiz, từ điển JSON, XP và lưu tiến độ
- v0.2: bài học Hangul và ôn tập ngắt quãng
- v0.3: HUD từ vựng block/item và nhiệm vụ sinh tồn
- v0.4: đấu trường quiz
- v0.5: NPC hội thoại và RPG
