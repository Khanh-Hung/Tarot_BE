package tarot.infrastructure.persistence.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tarot.domain.entities.Card;
import tarot.domain.entities.Deck;
import tarot.domain.enums.ArcanaType;
import tarot.domain.enums.DeckCode;
import tarot.infrastructure.persistence.repositories.CardRepository;
import tarot.infrastructure.persistence.repositories.DeckRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedDecks();
        seedMajorArcanaCards();
    }

    private void seedDecks() {
        if (deckRepository.count() > 0) return;

        log.info("🌟 Đang nạp dữ liệu danh mục 4 Bộ bài Tarot mẫu...");

        Deck d1 = Deck.builder()
                .code(DeckCode.RIDER_WAITE_CLASSIC)
                .nameVi("Rider-Waite Classic")
                .nameEn("Rider-Waite Tarot Standard Edition")
                .description("Bộ bài Tarot chuẩn mực và phổ biến nhất thế giới được sáng tạo bởi Arthur Edward Waite và minh họa bởi Pamela Colman Smith.")
                .build();

        Deck d2 = Deck.builder()
                .code(DeckCode.ANIME_MANGA)
                .nameVi("Anime & Manga Tarot")
                .nameEn("Japanese Anime Aesthetic Tarot")
                .description("Bộ bài mang phong cách hoạt họa Anime Nhật Bản hiện đại, rực rỡ sắc màu và tươi mới.")
                .build();

        Deck d3 = Deck.builder()
                .code(DeckCode.CYBERPUNK_NEON)
                .nameVi("Cyberpunk Neon 2077")
                .nameEn("Cyberpunk Futuristic Neon Tarot")
                .description("Bộ bài mang phong cách tương lai Cyberpunk với ánh sáng Neon huyền ảo và công nghệ vị lai.")
                .build();

        Deck d4 = Deck.builder()
                .code(DeckCode.MYSTICAL_CATS)
                .nameVi("Mèo Huyền Bí")
                .nameEn("Mystical Cats Tarot")
                .description("Bộ bài Tarot lấy cảm hứng từ thế giới loài mèo thông thái, bí ẩn và giàu tính chữa lành.")
                .build();

        deckRepository.saveAll(List.of(d1, d2, d3, d4));
    }

    private void seedMajorArcanaCards() {
        if (cardRepository.count() > 0) return;

        log.info("🎴 Đang nạp 22 lá bài Major Arcana (Ẩn chính) Rider-Waite...");

        List<Card> cards = List.of(
            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Chàng Khờ").nameEn("The Fool")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Khí").keywords("Khởi đầu mới, tự do, ngây thơ, phiêu lưu, liều lĩnh")
                .uprightMeaning("Một hành trình mới đầy hứng khởi đang bắt đầu. Hãy tin vào tiếng gọi trái tim và can đảm bước đi.")
                .reversedMeaning("Sự liều lĩnh thiếu suy nghĩ, bốc đồng hoặc ngần ngại không dám nắm bắt cơ hội mới.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Nhà Ảo Thuật").nameEn("The Magician")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Khí").keywords("Tập trung, ý chí, nguồn lực, sáng tạo, hành động")
                .uprightMeaning("Bạn có đầy đủ mọi nguồn lực, kỹ năng và cơ hội để biến ý tưởng thành hiện thực.")
                .reversedMeaning("Thiếu phương hướng, tiềm năng chưa được khai phá hoặc có sự thao túng, gian lận.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Nữ Tư Tế").nameEn("The High Priestess")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Nước").keywords("Trực giác, bí ẩn, tiềm thức, tĩnh lặng, thông thái")
                .uprightMeaning("Hãy lắng nghe trực giác và tiếng nói nội tâm sâu kín của bạn. Chân lý nằm ở sự tĩnh lặng.")
                .reversedMeaning("Bỏ qua tiếng nói nội tâm, bí mật bị che giấu hoặc tâm trạng bất an xáo trộn.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Hoàng Hậu").nameEn("The Empress")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Đất").keywords("Màu mỡ, nuôi dưỡng, tình mẫu tử, thịnh vượng, nghệ thuật")
                .uprightMeaning("Sự phát triển dồi dào về tài lộc, tình cảm và sáng tạo. Giai đoạn sinh sôi nảy nở tốt đẹp.")
                .reversedMeaning("Sự lệ thuộc, thiếu quan chăm sóc bản thân hoặc bế tắc trong sáng tạo.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Hoàng Đế").nameEn("The Emperor")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Kỷ luật, quyền lực, cấu trúc, vững chãi, lãnh đạo")
                .uprightMeaning("Cần thiết lập trật tự, kỷ luật và kế hoạch rõ ràng để dẫn dắt mọi việc đi đến thành công.")
                .reversedMeaning("Sự độc đoán, cứng nhắc hoặc mất kiểm soát trước tình huống phức tạp.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Giáo Hoàng").nameEn("The Hierophant")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Đất").keywords("Truyền thống, học vấn, tâm linh, cố vấn, niềm tin")
                .uprightMeaning("Tìm kiếm lời khuyên từ người có kinh nghiệm, tuân theo chuẩn mực đạo đức và giá trị tinh thần.")
                .reversedMeaning("Sự gò bó giáo điều, nổi loạn chống lại quy chuẩn hoặc niềm tin sai lệch.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Đôi Tình Nhân").nameEn("The Lovers")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Khí").keywords("Tình yêu, hòa hợp, lựa chọn, đồng điệu, cam kết")
                .uprightMeaning("Sự hòa hợp sâu sắc trong tình yêu và các mối quan hệ. Đứng trước một lựa chọn quan trọng từ con tim.")
                .reversedMeaning("Mâu thuẫn giá trị, rạn nứt niềm tin hoặc sự thiếu hòa hợp giữa hai bên.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Cỗ Xe Chiến Thắng").nameEn("The Chariot")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Nước").keywords("Quyết tâm, ý chí, vượt qua thử thách, kiểm soát, chiến thắng")
                .uprightMeaning("Tập trung cao độ và quyết tâm vượt qua mọi chướng ngại vật để giành thắng lợi.")
                .reversedMeaning("Mất phương hướng, nóng vội, thiếu kiên nhẫn hoặc bị áp lực đè nặng.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Sức Mạnh").nameEn("Strength")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Lòng can đảm, kiên nhẫn, lòng trắc ẩn, chế ngự, nội lực")
                .uprightMeaning("Sức mạnh mềm của sự kiên trì, dịu dàng và lòng từ bi sẽ thuần hóa mọi nghịch cảnh.")
                .reversedMeaning("Tự ti, nghi ngờ bản thân hoặc bùng phát cảm xúc mất kiểm soát.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Ẩn Sĩ").nameEn("The Hermit")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Đất").keywords("Tự vấn, chiêm nghiệm, soi sáng, cô độc, tìm kiếm chân lý")
                .uprightMeaning("Hãy dành thời gian tĩnh lặng một mình để chiêm nghiệm và tìm kiếm câu trả lời bên trong bạn.")
                .reversedMeaning("Cô lập thái quá, cảm giác cô đơn bế tắc hoặc từ chối sự trợ giúp từ bên ngoài.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Vòng Xoay Vận Mệnh").nameEn("Wheel of Fortune")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Vận may, chu kỳ, định mệnh, bước ngoặt, thay đổi")
                .uprightMeaning("Bánh xe may mắn đang quay theo hướng tích cực. Cơ hội và bước ngoặt bất ngờ đang đến.")
                .reversedMeaning("Giai đoạn khó khăn tạm thời, cảm giác thiếu may mắn hoặc chống lại sự thay đổi tự nhiên.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Công Lý").nameEn("Justice")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Khí").keywords("Công bằng, sự thật, nhân quả, quyết định minh bạch")
                .uprightMeaning("Mọi sự thật sẽ được sáng tỏ. Quyết định công tâm sẽ mang lại kết quả xứng đáng.")
                .reversedMeaning("Sự bất công, phán xét thiên vị hoặc trốn tránh trách nhiệm.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Kẻ Bị Treo").nameEn("The Hanged Man")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Nước").keywords("Góc nhìn mới, buông bỏ, dừng lại, hy sinh, kiên nhẫn")
                .uprightMeaning("Hãy học cách buông bỏ sự kiểm soát và nhìn nhận vấn đề từ một góc độ hoàn toàn mới.")
                .reversedMeaning("Trì hoãn vô ích, hy sinh không cần thiết hoặc kháng cự buông bỏ.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Cái Chết (Tái Sinh)").nameEn("Death")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Nước").keywords("Kết thúc, biến đổi, tái sinh, buông bỏ quá khứ")
                .uprightMeaning("Một giai đoạn cũ khép lại để mở ra một khởi đầu mới rực rỡ hơn. Sự chuyển hóa mạnh mẽ.")
                .reversedMeaning("Sợ hãi thay đổi, bám víu vào quá khứ đã qua hoặc khó khăn trong việc buông bỏ.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Điều Độ").nameEn("Temperance")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Cân bằng, hòa hợp, kiên nhẫn, chữa lành, dung hòa")
                .uprightMeaning("Tìm lại sự cân bằng giữa cảm xúc và lý trí. Dòng chảy chữa lành đang làm dịu tâm hồn bạn.")
                .reversedMeaning("Mất cân bằng, căng thẳng quá mức hoặc bất đồng quan điểm.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Ác Quỷ").nameEn("The Devil")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Đất").keywords("Ràng buộc, cám dỗ, vật chất, ảo tưởng, chấp niệm")
                .uprightMeaning("Nhận diện những xiềng xích vô hình do nỗi sợ và thói quen tiêu cực tự giam hãm bạn.")
                .reversedMeaning("Tự giải phóng bản thân khỏi sự ràng buộc, thức tỉnh và lấy lại tự do.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Tòa Tháp").nameEn("The Tower")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Sụp đổ, thức tỉnh đột ngột, hỗn loạn, tái thiết")
                .uprightMeaning("Sự sụp đổ của những ảo tưởng không bền vững giúp bạn xây dựng lại nền móng chân thật.")
                .reversedMeaning("Tránh được tai họa trong gang tấc hoặc chần chừ không dám đối diện sự thật.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Ngôi Sao Hy Vọng").nameEn("The Star")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Khí").keywords("Hy vọng, niềm tin, cảm hứng, chữa lành, bình an")
                .uprightMeaning("Ánh sáng dẫn đường của hy vọng và niềm tin. Một thời kỳ bình yên và tái sinh tâm hồn.")
                .reversedMeaning("Mất niềm tin, tuyệt vọng tạm thời hoặc thiếu cảm hứng sáng tạo.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Mặt Trăng").nameEn("The Moon")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Nước").keywords("Ảo tưởng, nỗi sợ tiềm thức, bí ẩn, bất an")
                .uprightMeaning("Cảnh báo về những điều chưa rõ ràng và sự nhầm lẫn cảm xúc. Hãy cẩn trọng quan sát.")
                .reversedMeaning("Sự thật dần lộ diện, nỗi sợ hãi tan biến và tâm trí sáng tỏ trở lại.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Mặt Trời").nameEn("The Sun")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Niềm vui, thành công, rạng rỡ, sức sống, tích cực")
                .uprightMeaning("Nguồn năng lượng tích cực rực rỡ, thành công trọn vẹn và niềm vui ngập tràn đang đến.")
                .reversedMeaning("Niềm vui bị trì hoãn tạm thời hoặc quá lạc quan thiếu thực tế.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Sự Phán Xét").nameEn("Judgement")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Lửa").keywords("Thức tỉnh, tiếng gọi định mệnh, tha thứ, quyết định lớn")
                .uprightMeaning("Tiếng chuông thức tỉnh tâm thức. Đã đến lúc đưa ra quyết định hệ trọng và bước sang trang mới.")
                .reversedMeaning("Tự trách móc, ngần ngại trước bước ngoặt hoặc chưa sẵn sàng tha thứ.").build(),

            Card.builder().deckCode(DeckCode.RIDER_WAITE_CLASSIC).nameVi("Thế Giới").nameEn("The World")
                .arcanaType(ArcanaType.MAJOR_ARCANA).element("Đất").keywords("Viên mãn, hoàn thành, trọn vẹn, hòa nhập, thành tựu lớn")
                .uprightMeaning("Một chu kỳ tuyệt vời đã hoàn tất trọn vẹn. Thành công, sự thỏa mãn và bình an tuyệt đối.")
                .reversedMeaning("Chưa hoàn thành trọn vẹn, còn thiếu một mảnh ghép nhỏ để về đích.").build()
        );

        cardRepository.saveAll(cards);
        log.info("✅ Đã nạp thành công 22 lá bài Major Arcana vào CSDL Supabase!");
    }
}