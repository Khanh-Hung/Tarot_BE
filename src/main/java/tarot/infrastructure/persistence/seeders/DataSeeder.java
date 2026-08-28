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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        dropOldCheckConstraints();
        seedDecks();
        seedAuthenticCards();
    }

    private void dropOldCheckConstraints() {
        try {
            entityManager.createNativeQuery("ALTER TABLE IF EXISTS decks DROP CONSTRAINT IF EXISTS decks_code_check").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE IF EXISTS cards DROP CONSTRAINT IF EXISTS cards_deck_code_check").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE IF EXISTS readings DROP CONSTRAINT IF EXISTS readings_deck_code_check").executeUpdate();
            log.info("✅ Đã cập nhật xong cấu trúc Check Constraint cho 3 bộ bài mới!");
        } catch (Exception e) {
            log.warn("Lưu ý khi drop constraint (có thể chưa tồn tại): {}", e.getMessage());
        }
    }

    private void seedDecks() {
        log.info("🌟 Đang khởi tạo 3 Bộ Bài Tarot Chuẩn Quốc Tế (The Big Three)...");

        saveOrUpdateDeck(
                DeckCode.RIDER_WAITE_CLASSIC,
                "Rider-Waite Classic",
                "Rider-Waite-Smith Authentic 1909 Deck",
                "Bộ bài Tarot 78 lá nguyên bản chuẩn mực quốc tế do Arthur Edward Waite thiết kế và nữ họa sĩ Pamela Colman Smith minh họa năm 1909. Biểu tượng phong phú, trực quan và phổ biến nhất thế giới.",
                "/cards/rws/m00.jpg"
        );

        saveOrUpdateDeck(
                DeckCode.THOTH_ALEISTER,
                "Thoth Tarot",
                "Aleister Crowley Thoth Tarot Deck",
                "Bộ bài Tarot huyền bí bậc nhất lịch sử do Aleister Crowley sáng tác và Lady Frieda Harris minh họa năm 1944. Liên kết sâu sắc với Chiêm tinh học, Giả kim thuật và Thần học Hermetic phương Tây.",
                "/cards/thoth/m00.jpg"
        );

        saveOrUpdateDeck(
                DeckCode.MARSEILLE_HERMETIC,
                "Tarot de Marseille",
                "Classic Tarot de Marseille Deck",
                "Bộ bài Tarot lâu đời nhất châu Âu thời Phục Hưng nước Pháp. Mang phong cách tranh khắc gỗ mộc mạc, đậm chất huyền học cổ điển thời Trung Cổ.",
                "/cards/marseille/m00.jpg"
        );
    }

    private void saveOrUpdateDeck(DeckCode code, String nameVi, String nameEn, String desc, String coverUrl) {
        Deck deck = deckRepository.findByCode(code).orElse(null);
        if (deck == null) {
            deck = Deck.builder()
                    .code(code)
                    .nameVi(nameVi)
                    .nameEn(nameEn)
                    .description(desc)
                    .coverImageUrl(coverUrl)
                    .build();
        } else {
            deck.setNameVi(nameVi);
            deck.setNameEn(nameEn);
            deck.setDescription(desc);
            deck.setCoverImageUrl(coverUrl);
        }
        deckRepository.save(deck);
    }

    private void seedAuthenticCards() {
        log.info("🎴 Đang đồng bộ và cập nhật dữ liệu 78 lá bài song ngữ cho cả 3 bộ bài quốc tế...");

        try {
            ClassPathResource resource = new ClassPathResource("cards-rws-78.json");
            try (InputStream is = resource.getInputStream()) {
                List<CardJsonItem> rawCards = objectMapper.readValue(is, new TypeReference<List<CardJsonItem>>() {});

                // 1. Đồng bộ bộ bài Rider-Waite Classic
                syncDeckCards(DeckCode.RIDER_WAITE_CLASSIC, rawCards, false, false);

                // 2. Đồng bộ bộ bài Thoth Tarot
                syncDeckCards(DeckCode.THOTH_ALEISTER, rawCards, true, false);

                // 3. Đồng bộ bộ bài Tarot de Marseille
                syncDeckCards(DeckCode.MARSEILLE_HERMETIC, rawCards, false, true);

                log.info("✅ Hoàn tất đồng bộ 234 lá bài với 3 bộ hình ảnh chuyên biệt (RWS, Thoth, Marseille)!");
            }
        } catch (Exception e) {
            log.error("❌ Lỗi khi nạp dữ liệu 3 bộ bài Tarot: {}", e.getMessage(), e);
        }
    }

    private void syncDeckCards(DeckCode deckCode, List<CardJsonItem> rawCards, boolean isThoth, boolean isMarseille) {
        List<Card> existing = cardRepository.findByDeckCode(deckCode);
        Map<String, Card> map = existing.stream().collect(Collectors.toMap(Card::getNameEn, c -> c, (a, b) -> a));

        List<Card> toSave = new ArrayList<>();
        List<String> validNames = new ArrayList<>();

        for (CardJsonItem c : rawCards) {
            String nameEn = isThoth ? getThothCardNameEn(c.getNameEn()) : isMarseille ? getMarseilleCardName(c.getNameEn()) : c.getNameEn();
            String nameVi = isThoth ? getThothCardNameVi(c.getNameVi(), nameEn) : isMarseille ? getMarseilleCardNameVi(c.getNameVi()) : c.getNameVi();
            String kwVi = isThoth ? c.getKeywords() + ", Thoth Hermetic, Chiêm Tinh Học" : isMarseille ? c.getKeywords() + ", Marseille Cổ Điển" : c.getKeywords();
            String kwEn = isThoth ? c.getKeywordsEn() + ", Thoth Hermetic, Astrology" : c.getKeywordsEn();

            String imgUrl = c.getImageUrl();
            if (isThoth) {
                imgUrl = imgUrl.replace("/cards/rws/", "/cards/thoth/");
            } else if (isMarseille) {
                imgUrl = imgUrl.replace("/cards/rws/", "/cards/marseille/");
            }

            validNames.add(nameEn);

            Card card = map.get(nameEn);
            if (card == null) {
                card = Card.builder()
                        .deckCode(deckCode)
                        .nameEn(nameEn)
                        .nameVi(nameVi)
                        .arcanaType(ArcanaType.valueOf(c.getArcanaType()))
                        .element(c.getElement())
                        .keywords(kwVi)
                        .keywordsEn(kwEn)
                        .imageUrl(imgUrl)
                        .uprightMeaning(c.getUprightMeaning())
                        .reversedMeaning(c.getReversedMeaning())
                        .uprightMeaningEn(c.getUprightMeaningEn())
                        .reversedMeaningEn(c.getReversedMeaningEn())
                        .build();
            } else {
                card.setNameVi(nameVi);
                card.setArcanaType(ArcanaType.valueOf(c.getArcanaType()));
                card.setElement(c.getElement());
                card.setKeywords(kwVi);
                card.setKeywordsEn(kwEn);
                card.setImageUrl(imgUrl);
                card.setUprightMeaning(c.getUprightMeaning());
                card.setReversedMeaning(c.getReversedMeaning());
                card.setUprightMeaningEn(c.getUprightMeaningEn());
                card.setReversedMeaningEn(c.getReversedMeaningEn());
            }
            toSave.add(card);
        }

        // Xóa những lá bài thừa không thuộc danh sách chuẩn
        for (Card oldCard : existing) {
            if (!validNames.contains(oldCard.getNameEn())) {
                cardRepository.delete(oldCard);
            }
        }

        cardRepository.saveAll(toSave);
    }

    private String getThothCardNameEn(String rwsName) {
        return switch (rwsName) {
            case "The Magician" -> "The Magus";
            case "The High Priestess" -> "The Priestess";
            case "Justice" -> "Adjustment (VIII)";
            case "Strength" -> "Lust (XI)";
            case "Temperance" -> "Art (XIV)";
            case "Judgement" -> "The Aeon (XX)";
            case "The World" -> "The Universe (XXI)";
            case "Page of Wands" -> "Princess of Wands";
            case "Knight of Wands" -> "Prince of Wands";
            case "King of Wands" -> "Knight of Wands";
            case "Page of Cups" -> "Princess of Cups";
            case "Knight of Cups" -> "Prince of Cups";
            case "King of Cups" -> "Knight of Cups";
            case "Page of Swords" -> "Princess of Swords";
            case "Knight of Swords" -> "Prince of Swords";
            case "King of Swords" -> "Knight of Swords";
            case "Page of Pentacles" -> "Princess of Disks";
            case "Knight of Pentacles" -> "Prince of Disks";
            case "Queen of Pentacles" -> "Queen of Disks";
            case "King of Pentacles" -> "Knight of Disks";
            default -> rwsName.replace("Pentacles", "Disks");
        };
    }

    private String getThothCardNameVi(String rwsVi, String thothEn) {
        return switch (thothEn) {
            case "The Magus" -> "Pháp Sư Thoth (The Magus)";
            case "The Priestess" -> "Nữ Tư Tế (The Priestess)";
            case "Adjustment (VIII)" -> "Sự Điều Chỉnh (Adjustment)";
            case "Lust (XI)" -> "Khát Vọng & Đam Mê (Lust)";
            case "Art (XIV)" -> "Nghệ Thuật Giả Kim (Art)";
            case "The Aeon (XX)" -> "Kỷ Nguyên Mới (The Aeon)";
            case "The Universe (XXI)" -> "Vũ Trụ (The Universe)";
            default -> rwsVi.replace("Tiền", "Đĩa");
        };
    }

    private String getMarseilleCardName(String rwsName) {
        return switch (rwsName) {
            case "The Fool" -> "Le Mat";
            case "The Magician" -> "I. Le Bateleur";
            case "The High Priestess" -> "II. La Papesse";
            case "The Empress" -> "III. L'Impératrice";
            case "The Emperor" -> "IV. L'Empereur";
            case "The Hierophant" -> "V. Le Pape";
            case "The Lovers" -> "VI. L'Amoureux";
            case "The Chariot" -> "VII. Le Chariot";
            case "Justice" -> "VIII. La Justice";
            case "The Hermit" -> "IX. L'Hermite";
            case "Wheel of Fortune" -> "X. La Roue de Fortune";
            case "Strength" -> "XI. La Force";
            case "The Hanged Man" -> "XII. Le Pendu";
            case "Death" -> "XIII. L'Arcane sans nom";
            case "Temperance" -> "XIV. Tempérance";
            case "The Devil" -> "XV. Le Diable";
            case "The Tower" -> "XVI. La Maison Dieu";
            case "The Star" -> "XVII. L'Étoile";
            case "The Moon" -> "XVIII. La Lune";
            case "The Sun" -> "XIX. Le Soleil";
            case "Judgement" -> "XX. Le Jugement";
            case "The World" -> "XXI. Le Monde";
            default -> rwsName;
        };
    }

    private String getMarseilleCardNameVi(String rwsVi) {
        return rwsVi;
    }

    @Data
    private static class CardJsonItem {
        private String nameEn;
        private String nameVi;
        private String arcanaType;
        private String element;
        private String keywords;
        private String keywordsEn;
        private String imageUrl;
        private String uprightMeaning;
        private String reversedMeaning;
        private String uprightMeaningEn;
        private String reversedMeaningEn;
    }
}