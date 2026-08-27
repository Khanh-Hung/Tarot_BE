package tarot.infrastructure.persistence.seeders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tarot.domain.entities.Card;
import tarot.domain.entities.Deck;
import tarot.domain.enums.ArcanaType;
import tarot.domain.enums.DeckCode;
import tarot.infrastructure.persistence.repositories.CardRepository;
import tarot.infrastructure.persistence.repositories.DeckRepository;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        seedDecks();
        seedAuthentic78Cards();
    }

    private void seedDecks() {
        log.info("🌟 Đang khởi tạo bộ bài Tarot Rider-Waite-Smith 1909 chính thống duy nhất...");

        // Xóa các bộ bài không phải RIDER_WAITE_CLASSIC
        List<Deck> decks = deckRepository.findAll();
        for (Deck d : decks) {
            if (d.getCode() != DeckCode.RIDER_WAITE_CLASSIC) {
                deckRepository.delete(d);
            }
        }

        Deck rwsDeck = deckRepository.findByCode(DeckCode.RIDER_WAITE_CLASSIC).orElse(null);
        if (rwsDeck == null) {
            rwsDeck = Deck.builder()
                    .code(DeckCode.RIDER_WAITE_CLASSIC)
                    .nameVi("Rider-Waite Classic (1909)")
                    .nameEn("Rider-Waite-Smith Authentic 1909 Deck")
                    .description("Bộ bài Tarot 78 lá nguyên bản chuẩn mực quốc tế do Arthur Edward Waite thiết kế và nữ họa sĩ Pamela Colman Smith minh họa năm 1909.")
                    .coverImageUrl("/cards/rws/m00.jpg")
                    .build();
            deckRepository.save(rwsDeck);
        } else {
            rwsDeck.setNameVi("Rider-Waite Classic (1909)");
            rwsDeck.setNameEn("Rider-Waite-Smith Authentic 1909 Deck");
            rwsDeck.setDescription("Bộ bài Tarot 78 lá nguyên bản chuẩn mực quốc tế do Arthur Edward Waite thiết kế và nữ họa sĩ Pamela Colman Smith minh họa năm 1909.");
            rwsDeck.setCoverImageUrl("/cards/rws/m00.jpg");
            deckRepository.save(rwsDeck);
        }
    }

    private void seedAuthentic78Cards() {
        if (cardRepository.count() == 78) {
            log.info("✅ CSDL đã có đầy đủ 78 lá bài Rider-Waite chính thống!");
            return;
        }

        log.info("🎴 Đang nạp trọn bộ 78 lá bài Tarot Rider-Waite chính thống từ file cards-rws-78.json...");

        // Xóa các lá bài cũ để nạp lại chuẩn 78 lá
        cardRepository.deleteAll();

        try {
            ClassPathResource resource = new ClassPathResource("cards-rws-78.json");
            try (InputStream is = resource.getInputStream()) {
                List<CardJsonItem> rawCards = objectMapper.readValue(is, new TypeReference<List<CardJsonItem>>() {});

                List<Card> cards = new java.util.ArrayList<>();
                for (CardJsonItem c : rawCards) {
                    cards.add(Card.builder()
                            .deckCode(DeckCode.RIDER_WAITE_CLASSIC)
                            .nameEn(c.getNameEn())
                            .nameVi(c.getNameVi())
                            .arcanaType(ArcanaType.valueOf(c.getArcanaType()))
                            .element(c.getElement())
                            .keywords(c.getKeywords())
                            .imageUrl(c.getImageUrl())
                            .uprightMeaning(c.getUprightMeaning())
                            .reversedMeaning(c.getReversedMeaning())
                            .build());
                }

                cardRepository.saveAll(cards);
                log.info("✅ Đã nạp thành công {} lá bài Tarot Rider-Waite chính thống vào Database!", cards.size());
            }
        } catch (Exception e) {
            log.error("❌ Lỗi khi nạp dữ liệu 78 lá bài Tarot: {}", e.getMessage(), e);
        }
    }

    @Data
    private static class CardJsonItem {
        private String nameEn;
        private String nameVi;
        private String arcanaType;
        private String element;
        private String keywords;
        private String imageUrl;
        private String uprightMeaning;
        private String reversedMeaning;
    }
}