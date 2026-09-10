package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.AggregateRoot;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.MessageSender;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "\"Readings\"")
@org.hibernate.annotations.SQLRestriction("\"Deleted\" = false")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Reading extends AggregateRoot {

    @Column(name = "\"UserId\"", nullable = false)
    private UUID userId;

    @Column(name = "\"UserQuestion\"", nullable = false, length = 500)
    private String userQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"Topic\"", nullable = false, length = 50)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"SpreadType\"", nullable = false, length = 50)
    @Builder.Default
    private SpreadType spreadType = SpreadType.PAST_PRESENT_FUTURE;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DeckCode\"", nullable = false, length = 50)
    @Builder.Default
    private DeckCode deckCode = DeckCode.RIDER_WAITE_CLASSIC;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DrawnCard> drawnCards = new ArrayList<>();

    @Column(name = "\"InitialReading\"", columnDefinition = "TEXT")
    private String initialReading;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // --- DOMAIN FACTORY METHOD ---

    public static Reading create(UUID userId, String question, Topic topic, SpreadType spreadType, DeckCode deckCode) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null when creating a reading");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("User question cannot be blank");
        }
        return Reading.builder()
                .userId(userId)
                .userQuestion(question)
                .topic(topic != null ? topic : Topic.GENERAL_GUIDANCE)
                .spreadType(spreadType != null ? spreadType : SpreadType.PAST_PRESENT_FUTURE)
                .deckCode(deckCode != null ? deckCode : DeckCode.RIDER_WAITE_CLASSIC)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateTopic(Topic newTopic) {
        if (newTopic != null) {
            this.topic = newTopic;
        }
    }

    public static String[] getPositionNames(SpreadType spreadType) {
        if (spreadType == null) return new String[]{"Past & Foundations", "Present Situation", "Future & Destiny Trends"};
        return switch (spreadType) {
            case DAILY_ORACLE -> new String[]{"Daily Guidance"};
            case TWO_PATHS_CHOICE -> new String[]{"Current Reality", "Path A Outcome", "Path B Outcome"};
            case LOVE_RELATIONSHIP -> new String[]{"Your Energy", "Partner's Energy", "Relationship Connection"};
            case MIND_BODY_SPIRIT -> new String[]{"Mind & Beliefs", "Body & Actions", "Spirit & Intuition"};
            case SITUATION_OBSTACLE_ADVICE -> new String[]{"Current Situation", "Hidden Obstacle", "Actionable Advice"};
            case HORSESHOE -> new String[]{"Past Influence", "Present Reality", "Hidden Dynamics", "Best Action", "Final Outcome"};
            case CELTIC_CROSS -> new String[]{
                    "Present Situation",
                    "Immediate Challenge",
                    "Distant Past / Foundation",
                    "Recent Past",
                    "Highest Potential",
                    "Near Future",
                    "Self Attitude",
                    "Environment & Influences",
                    "Hopes & Fears",
                    "Ultimate Outcome"
            };
            case PAST_PRESENT_FUTURE -> new String[]{"Past & Foundations", "Present Situation", "Future & Destiny Trends"};
        };
    }

    public void drawCards(List<Card> availableCards) {
        int requiredCards = (this.spreadType != null) ? this.spreadType.getCardCount() : 3;
        if (availableCards == null || availableCards.size() < requiredCards) {
            throw new IllegalStateException("At least " + requiredCards + " cards are required in the deck to perform this spread");
        }

        List<Card> deck = new ArrayList<>(availableCards);
        Random random = new Random();
        Collections.shuffle(deck, random);

        String[] positionNames = getPositionNames(this.spreadType);
        for (int i = 0; i < requiredCards; i++) {
            String posName = (i < positionNames.length) ? positionNames[i] : "Position " + (i + 1);
            this.addDrawnCard(deck.get(i), i + 1, posName, random.nextBoolean());
        }
    }

    public void drawSelectedCards(List<Card> selectedCards, List<Boolean> isReversedList) {
        if (selectedCards == null || selectedCards.isEmpty()) {
            throw new IllegalArgumentException("Selected cards cannot be empty");
        }

        String[] positionNames = getPositionNames(this.spreadType);

        Random random = new Random();
        for (int i = 0; i < selectedCards.size(); i++) {
            Card card = selectedCards.get(i);
            String posName = (i < positionNames.length) ? positionNames[i] : "Position " + (i + 1);
            boolean reversed = (isReversedList != null && i < isReversedList.size() && isReversedList.get(i) != null)
                    ? isReversedList.get(i)
                    : random.nextBoolean();
            this.addDrawnCard(card, i + 1, posName, reversed);
        }
    }

    public void attachInitialReading(String interpretationMarkdown) {
        if (interpretationMarkdown == null || interpretationMarkdown.isBlank()) {
            throw new IllegalArgumentException("Interpretation markdown cannot be empty");
        }
        this.initialReading = interpretationMarkdown;
    }

    public void addDrawnCard(Card card, int posIndex, String posName, boolean isReversed) {
        DrawnCard dc = DrawnCard.builder()
                .reading(this)
                .card(card)
                .positionIndex(posIndex)
                .positionName(posName)
                .isReversed(isReversed)
                .build();
        this.drawnCards.add(dc);
    }

    public void addUserMessage(String message) {
        ChatMessage msg = ChatMessage.builder()
                .reading(this)
                .sender(MessageSender.USER)
                .content(message)
                .build();
        this.chatMessages.add(msg);
    }

    public void addAiReply(String reply) {
        ChatMessage msg = ChatMessage.builder()
                .reading(this)
                .sender(MessageSender.AI_READER)
                .content(reply)
                .build();
        this.chatMessages.add(msg);
    }
}