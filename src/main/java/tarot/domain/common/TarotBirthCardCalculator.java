package tarot.domain.common;

import tarot.application.features.profile.dtos.BirthCardDto;
import java.time.LocalDate;

public final class TarotBirthCardCalculator {

    private TarotBirthCardCalculator() {}

    private record CardMeta(String vi, String en, String soulVi, String keywords, String desc) {}

    private static final CardMeta[] CARDS = new CardMeta[22];

    static {
        CARDS[0] = new CardMeta("Kẻ Khờ", "The Fool", "Hoàng Đế", "Tự do, khởi đầu, thuần khiết, can đảm",
                "Linh hồn tự do không bị trói buộc bởi định kiến. Bạn mang năng lượng tiên phong, sẵn sàng bước vào những hành trình mới với sự lạc quan và tin tưởng tuyệt đối vào vũ trụ.");
        CARDS[1] = new CardMeta("Bậc Thầy Biến Hóa", "The Magician", "Bánh Xe Số Phận", "Ý chí, kiến tạo, tháo vát, quyền năng",
                "Khả năng hiện thực hóa ý tưởng thành tiền tài và thành quả. Bạn sở hữu tài ăn nói, trí tuệ sắc bén và khả năng kết nối nguồn lực để tạo nên kỳ tích.");
        CARDS[2] = new CardMeta("Nữ Tư Tế", "The High Priestess", "Công Lý", "Trực giác, bí ẩn, tri thức ngầm, tĩnh lặng",
                "Trực giác nhạy bén phi thường và thế giới nội tâm sâu sắc. Bạn có khả năng nhìn thấu bản chất con người và sự việc đằng sau những bức màn che đậy.");
        CARDS[3] = new CardMeta("Hoàng Hậu", "The Empress", "Người Treo Ngược", "Trù phú, nuôi dưỡng, sáng tạo, tình yêu",
                "Năng lượng nuôi dưỡng và thu hút sự thịnh vượng tự nhiên. Bạn có khiếu nghệ thuật, tình cảm nồng ấm và khả năng biến mọi mảnh đất cằn cỗi thành hoa thơm trái ngọt.");
        CARDS[4] = new CardMeta("Hoàng Đế", "The Emperor", "Cái Chết & Tái Sinh", "Kỷ luật, quyền lực, cấu trúc, vững chãi",
                "Nhà lãnh đạo bẩm sinh với tư duy chiến lược và ý chí sắt đá. Bạn xây dựng cuộc đời dựa trên trật tự, nguyên tắc và khả năng gánh vác trách nhiệm lớn lao.");
        CARDS[5] = new CardMeta("Đại Tư Tế", "The Hierophant", "Tiết Độ & Cân Bằng", "Truyền thống, học thức, dẫn lối, niềm tin",
                "Người thầy tinh thần và người gìn giữ chân lý. Bạn có sứ mệnh chia sẻ tri thức, kết nối cộng đồng và xây dựng hệ giá trị đạo đức bền vững.");
        CARDS[6] = new CardMeta("Người Tình", "The Lovers", "Ác Quỷ & Ảo Tưởng", "Lựa chọn, đam mê, hòa hợp, giá trị sống",
                "Khát khao kết nối sâu sắc và năng lực dung hòa các mặt đối lập. Cuộc đời bạn là hành trình của những lựa chọn mang tính định đoạt dựa trên tiếng gọi của con tim.");
        CARDS[7] = new CardMeta("Cỗ Xe Chiến Thắng", "The Chariot", "Tòa Tháp Bứt Phá", "Ý chí, vượt khó, kiểm soát, bứt phá",
                "Chiến binh kiên cường không bao giờ đầu hàng nghịch cảnh. Bạn làm chủ cảm xúc và mục tiêu, sẵn sàng đạp bằng mọi chông gai để tiến về vạch đích.");
        CARDS[8] = new CardMeta("Sức Mạnh", "Strength", "Ngôi Sao Hy Vọng", "Nội lực, kiên nhẫn, lòng trắc ẩn, chế ngự",
                "Sức mạnh của sự mềm mỏng và lòng từ bi. Bạn thuần hóa những cơn bão giông bên trong và ngoài đời không phải bằng bạo lực mà bằng sự kiên định tĩnh lặng.");
        CARDS[9] = new CardMeta("Ẩn Sĩ", "The Hermit", "Mặt Trăng Trực Giác", "Chiêm nghiệm, thông thái, độc lập, soi sáng",
                "Ngọn hải đăng của sự minh triết. Bạn cần những khoảng lặng để đào sâu vào căn nguyên sự việc, tìm ra chân lý và trở thành người soi đường cho người khác.");
        CARDS[10] = new CardMeta("Bánh Xe Số Phận", "Wheel of Fortune", "Bậc Thầy Biến Hóa", "Thời vận, chuyển dịch, cơ duyên, linh hoạt",
                "Bậc thầy thích nghi với các chu kỳ thăng trầm của số phận. Bạn có cơ duyên đón nhận những bước ngoặt bất ngờ và luôn biết cách nắm bắt cơ hội ngàn vàng.");
        CARDS[11] = new CardMeta("Công Lý", "Justice", "Nữ Tư Tế", "Chân lý, công bằng, nhân quả, sáng suốt",
                "Cái đầu lạnh và trái tim công minh. Bạn đòi hỏi sự minh bạch, logic và luôn chịu trách nhiệm 100% với mọi nhân quả do hành động của mình tạo ra.");
        CARDS[12] = new CardMeta("Người Treo Ngược", "The Hanged Man", "Hoàng Hậu", "Buông bỏ, góc nhìn mới, hy sinh, giác ngộ",
                "Khả năng nhìn đời bằng lăng kính hoàn toàn khác biệt. Bạn sẵn sàng dừng lại, buông bỏ sự kiểm soát để đón nhận sự thức tỉnh và giác ngộ sâu sắc.");
        CARDS[13] = new CardMeta("Cái Chết & Tái Sinh", "Death", "Hoàng Đế", "Chuyển hóa, kết thúc, lột xác, tái sinh",
                "Năng lực lột xác phi thường sau những khủng hoảng. Bạn không ngại đóng lại những cánh cửa cũ kỹ để tái sinh một phiên bản mạnh mẽ, hoàn hảo hơn.");
        CARDS[14] = new CardMeta("Tiết Độ & Cân Bằng", "Temperance", "Đại Tư Tế", "Hòa giải, kiên nhẫn, thích ứng, thanh lọc",
                "Nhà giả kim dung hòa mọi thái cực. Bạn mang lại sự êm dịu, chữa lành và khả năng tìm thấy sự cân bằng hoàn hảo giữa lý trí và cảm xúc.");
        CARDS[15] = new CardMeta("Ác Quỷ & Ảo Tưởng", "The Devil", "Người Tình", "Dục vọng, vật chất, rào cản, giải thoát",
                "Sức hút mãnh liệt với thế giới vật chất và tham vọng lớn. Bài học cuộc đời bạn là nhận diện những sợi xích vô hình để tự giải phóng tiềm năng vô hạn.");
        CARDS[16] = new CardMeta("Tòa Tháp Bứt Phá", "The Tower", "Cỗ Xe Chiến Thắng", "Thức tỉnh, đột phá, tái thiết, tự do",
                "Cơn sấm sét quét sạch những ảo tưởng và nền móng mục ruỗng. Bạn là ngòi nổ cho những cuộc cách mạng đổi mới, tái thiết cuộc đời từ sự thật cốt lõi.");
        CARDS[17] = new CardMeta("Ngôi Sao Hy Vọng", "The Star", "Sức Mạnh", "Hy vọng, chữa lành, cảm hứng, an yên",
                "Nguồn cảm hứng thuần khiết và ánh sáng hy vọng sau bão giông. Bạn có khả năng xoa dịu nỗi đau, mang lại niềm tin và sự lạc quan cho chính mình và mọi người.");
        CARDS[18] = new CardMeta("Mặt Trăng Trực Giác", "The Moon", "Ẩn Sĩ", "Tiềm thức, trực giác, mơ mộng, ảo ảnh",
                "Người lữ hành của cõi vô thức và giấc mơ. Bạn cực kỳ nhạy cảm với năng lượng xung quanh, bài học của bạn là biến nỗi sợ mơ hồ thành nguồn sáng tạo độc bản.");
        CARDS[19] = new CardMeta("Mặt Trời Rực Rỡ", "The Sun", "Bánh Xe Số Phận", "Hân hoan, rực rỡ, thành công, sinh lực",
                "Ánh dương ấm áp xua tan mọi bóng tối. Bạn mang nguồn năng lượng sống tích cực, lan tỏa niềm vui, sự tự tin và gặt hái thành công vang dội.");
        CARDS[20] = new CardMeta("Phán Xét & Thức Tỉnh", "Judgement", "Nữ Tư Tế", "Lời kêu gọi, thức tỉnh, chuộc lỗi, tái sinh",
                "Tiếng kèn hiệu triệu của sự thức tỉnh tâm linh. Bạn được thôi thúc bởi một sứ mệnh cao cả, dám nhìn lại quá khứ để bước sang một tầng nhận thức mới.");
        CARDS[21] = new CardMeta("Thế Giới Toàn Vẹn", "The World", "Hoàng Hậu", "Viên mãn, hoàn tất, hòa hợp, tự do",
                "Sự thành toàn và hợp nhất tuyệt đối. Bạn hoàn tất một vòng xoay bài học lớn của cuộc đời để bước vào trạng thái tự do, viên mãn và làm chủ vận mệnh.");
    }

    public static BirthCardDto calculate(LocalDate dob) {
        if (dob == null) {
            return null;
        }

        int day = dob.getDayOfMonth();
        int month = dob.getMonthValue();
        int year = dob.getYear();

        // Chuẩn Mary K. Greer: Day + Month + First2Digits(Year) + Last2Digits(Year)
        int century = year / 100;
        int decade = year % 100;
        int sum = day + month + century + decade;

        // Nếu > 22 thì rút gọn
        while (sum > 22) {
            int temp = 0;
            int n = sum;
            while (n > 0) {
                temp += n % 10;
                n /= 10;
            }
            sum = temp;
        }

        // 22 là The Fool (card 0)
        int cardIdx = (sum == 22) ? 0 : sum;
        if (cardIdx < 0 || cardIdx > 21) {
            cardIdx = 0;
        }

        CardMeta meta = CARDS[cardIdx];
        String img = String.format("/cards/rws/m%02d.jpg", cardIdx);

        return new BirthCardDto(
                cardIdx,
                meta.vi(),
                meta.en(),
                meta.soulVi(),
                img,
                meta.keywords(),
                meta.desc()
        );
    }
}
