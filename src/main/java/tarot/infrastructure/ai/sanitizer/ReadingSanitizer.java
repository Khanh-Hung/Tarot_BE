package tarot.infrastructure.ai.sanitizer;

import org.springframework.stereotype.Component;
import tarot.domain.entities.identity.User;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReadingSanitizer {

    private static final Map<String, String> TOPIC_TRANSLATIONS = Map.of(
            "LOVE_AND_RELATIONSHIP", "Tình Yêu & Các Mối Quan Hệ",
            "LOVE_RELATIONSHIP", "Tình Yêu & Mối Quan Hệ",
            "CAREER_AND_FINANCE", "Công Việc & Sự Nghiệp",
            "CAREER_MONEY", "Công Việc & Tài Chính",
            "SELF_GROWTH_AND_HEALING", "Phát Triển Bản Thân & Chữa Lành",
            "SPIRITUAL_HEALING", "Tâm Linh & Chữa Lành",
            "DAILY_GUIDANCE", "Thông Điệp Ngày Mới",
            "DAILY_ORACLE", "Thông Điệp Ngày Mới",
            "GENERAL_GUIDANCE", "Tổng Quan Cuộc Sống",
            "GENERAL_QUESTION", "Câu Hỏi Chung"
    );

    private static final Map<String, String> ZODIAC_TRANSLATIONS = Map.ofEntries(
            Map.entry("ARIES", "Bạch Dương"),
            Map.entry("TAURUS", "Kim Ngưu"),
            Map.entry("GEMINI", "Song Tử"),
            Map.entry("CANCER", "Cự Giải"),
            Map.entry("LEO", "Sư Tử"),
            Map.entry("VIRGO", "Xử Nữ"),
            Map.entry("LIBRA", "Thiên Bình"),
            Map.entry("SCORPIO", "Bọ Cạp"),
            Map.entry("SAGITTARIUS", "Nhân Mã"),
            Map.entry("CAPRICORN", "Ma Kết"),
            Map.entry("AQUARIUS", "Bảo Bình"),
            Map.entry("PISCES", "Song Ngư")
    );

    private static final Map<String, String> POSITION_TRANSLATIONS = Map.of(
            "Daily Guidance", "Thông Điệp Ngày Mới",
            "Current Reality", "Thực Tại Hiện Nhiên",
            "Past & Foundations", "Quá Khứ & Nền Tảng",
            "Present Situation", "Hiện Tại & Thử Thách",
            "Future & Destiny Trends", "Tương Lai & Xu Hướng",
            "Path A Outcome", "Ngả Rẽ A",
            "Path B Outcome", "Ngả Rẽ B"
    );

    private static final Pattern TITLE_PATTERN = Pattern.compile("#\\s*🔮?\\s*THÔNG ĐIỆP VŨ TRỤ DÀNH CHO\\s+[^\\n]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern METADATA_PATTERN = Pattern.compile(
            ">?\\s*\\*?\\*?Chủ đề\\*?\\*?:?\\s*([^|\\n]+?)\\s*\\|\\s*\\*?\\*?Cung Hoàng Đạo\\*?\\*?:?\\s*([^|\\n>]+?)\\s*(?:\\||>|\\n|\\s)+\\*?\\*?Câu hỏi\\*?\\*?:?\\s*([^\\n]+)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern ENGLISH_PARENS_PATTERN = Pattern.compile("\\s*\\([A-Za-z\\s']+\\)");
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*([^*]+)\\*\\*");

    public String sanitize(String rawContent, User user) {
        if (rawContent == null || rawContent.isBlank()) {
            return rawContent;
        }

        String text = rawContent;

        // 1. Luôn chuẩn hóa tiêu đề lớn
        text = TITLE_PATTERN.matcher(text).replaceAll("# 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO BẠN");

        // 2. Chuẩn hóa metadata xuống dòng 3 hàng & dịch enum
        Matcher metaMatcher = METADATA_PATTERN.matcher(text);
        if (metaMatcher.find()) {
            String topicRaw = metaMatcher.group(1).trim().toUpperCase();
            String zodiacRaw = metaMatcher.group(2).trim().toUpperCase();
            String question = metaMatcher.group(3).trim();

            String topicVi = TOPIC_TRANSLATIONS.getOrDefault(topicRaw, metaMatcher.group(1).trim());
            String zodiacVi = ZODIAC_TRANSLATIONS.getOrDefault(zodiacRaw, metaMatcher.group(2).trim());

            String formattedMeta = String.format("> **Chủ đề**: %s  \n>\n> **Cung Hoàng Đạo**: %s  \n>\n> **Câu hỏi**: %s",
                    topicVi, zodiacVi, question);
            text = metaMatcher.replaceFirst(formattedMeta);
        }

        // 3. Loại bỏ tên tiếng Anh trong ngoặc đơn (như "(The High Priestess)")
        text = ENGLISH_PARENS_PATTERN.matcher(text).replaceAll("");

        // 3.1. Chuẩn hóa biểu tượng mục 2 thành cuộn thư 📜 để tránh trùng lặp 🎴 với lá bài con
        text = text.replaceAll("(?m)^##\\s*🎴\\s*2\\.", "## 📜 2.");

        // 4. Cá nhân hóa danh xưng bằng DisplayName
        String displayName = "Bạn";
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isBlank()) {
                displayName = user.getDisplayName().trim();
            } else if (user.getUserName() != null && !user.getUserName().isBlank()) {
                displayName = user.getUserName().trim();
            }
        }
        text = text.replaceAll("(?i)Chào\\s+[Bb]ạn\\s+thân\\s+mến", "Chào " + displayName + " thân mến");
        text = text.replaceAll("(?i)hỡi\\s+[a-zA-Z0-9_-]+,\\s*", displayName + ", ");
        text = text.replaceAll("(?i)hỡi\\s+bạn,\\s*", displayName + ", ");

        // 5. Dịch các vị trí tiếng Anh trong ngoặc vuông
        for (Map.Entry<String, String> entry : POSITION_TRANSLATIONS.entrySet()) {
            text = text.replaceAll("(?i)\\[\\s*" + Pattern.quote(entry.getKey()) + "\\s*\\]", "**" + entry.getValue() + "**");
        }
        text = text.replaceAll("(?i)\\[\\s*(Thông Điệp Ngày Mới|Thực Tại Hiện Nhiên|Quá Khứ & Nền Tảng|Hiện Tại & Thử Thách|Tương Lai & Xu Hướng|Ngả Rẽ [AB])\\s*\\]", "**$1**");

        // 6. Viết hoa đồng bộ các từ trong cụm in đậm phong cách Title Case
        text = fixTitleCaseCapitalization(text);

        // 7. Chuyển đổi các từ viết hoa toàn bộ (ALL CAPS) gào thét sang chữ in đậm thanh lịch
        text = deAnonymizeAllCaps(text);

        return text;
    }

    private String deAnonymizeAllCaps(String text) {
        Matcher boldMatcher = BOLD_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (boldMatcher.find()) {
            String inner = boldMatcher.group(1);
            // Nếu cụm từ in đậm bị viết hoa toàn bộ (ALL CAPS), chuyển về chữ thường để tránh cảm giác gào thét
            if (inner.length() >= 3 && inner.equals(inner.toUpperCase()) && !inner.equals(inner.toLowerCase())) {
                boldMatcher.appendReplacement(sb, Matcher.quoteReplacement("**" + inner.toLowerCase() + "**"));
            } else {
                boldMatcher.appendReplacement(sb, Matcher.quoteReplacement("**" + inner + "**"));
            }
        }
        boldMatcher.appendTail(sb);
        return sb.toString();
    }

    private String fixTitleCaseCapitalization(String text) {
        Matcher matcher = BOLD_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String inner = matcher.group(1);
            if (isTitleCasedHeading(inner)) {
                inner = inner.replaceAll("(?<=^|\\s)và(?=\\s|[.,:;!?]|$)", "Và")
                        .replaceAll("(?<=^|\\s)của(?=\\s|[.,:;!?]|$)", "Của")
                        .replaceAll("(?<=^|\\s)cho(?=\\s|[.,:;!?]|$)", "Cho")
                        .replaceAll("(?<=^|\\s)trong(?=\\s|[.,:;!?]|$)", "Trong")
                        .replaceAll("(?<=^|\\s)với(?=\\s|[.,:;!?]|$)", "Với")
                        .replaceAll("(?<=^|\\s)ở(?=\\s|[.,:;!?]|$)", "Ở")
                        .replaceAll("(?<=^|\\s)từ(?=\\s|[.,:;!?]|$)", "Từ")
                        .replaceAll("(?<=^|\\s)đến(?=\\s|[.,:;!?]|$)", "Đến");
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement("**" + inner + "**"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private boolean isTitleCasedHeading(String inner) {
        if (inner.length() > 60) return false;
        String[] words = inner.trim().split("\\s+");
        if (words.length < 2 || words.length > 8) return false;
        int upperFirstCount = 0;
        for (String w : words) {
            if (!w.isEmpty() && Character.isUpperCase(w.codePointAt(0))) {
                upperFirstCount++;
            }
        }
        return upperFirstCount >= 2;
    }
}
