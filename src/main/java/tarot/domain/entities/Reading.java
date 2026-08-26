package tarot.domain.entities;

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

@Entity
@Table(name = "readings")
@SoftDelete
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Reading extends AggregateRoot {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_question", nullable = false, length = 500)
    private String userQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic", nullable = false, length = 50)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "spread_type", nullable = false, length = 50)
    @Builder.Default
    private SpreadType spreadType = SpreadType.PAST_PRESENT_FUTURE;

    @Enumerated(EnumType.STRING)
    @Column(name = "deck_code", nullable = false, length = 50)
    @Builder.Default
    private DeckCode deckCode = DeckCode.RIDER_WAITE_CLASSIC;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<DrawnCard> drawnCards = new ArrayList<>();

    @Column(name = "initial_reading", columnDefinition = "TEXT")
    private String initialReading;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // --- DOMAIN FACTORY METHOD ---

    public static Reading create(User user, String question, Topic topic, SpreadType spreadType, DeckCode deckCode) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when creating a reading");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("User question cannot be blank");
        }
        return Reading.builder()
                .user(user)
                .userQuestion(question)
                .topic(topic != null ? topic : Topic.GENERAL_GUIDANCE)
                .spreadType(spreadType != null ? spreadType : SpreadType.PAST_PRESENT_FUTURE)
                .deckCode(deckCode != null ? deckCode : DeckCode.RIDER_WAITE_CLASSIC)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void drawCards(List<Card> availableCards) {
        int requiredCards = (this.spreadType == SpreadType.DAILY_ORACLE) ? 1 : 3;
        if (availableCards == null || availableCards.size() < requiredCards) {
            throw new IllegalStateException("At least " + requiredCards + " cards are required in the deck to perform this spread");
        }

        List<Card> deck = new ArrayList<>(availableCards);
        Random random = new Random();
        Collections.shuffle(deck, random);

        if (this.spreadType == SpreadType.DAILY_ORACLE) {
            this.addDrawnCard(deck.getFirst(), 1, "Daily Guidance", random.nextBoolean());
        } else if (this.spreadType == SpreadType.TWO_PATHS_CHOICE) {
            this.addDrawnCard(deck.get(0), 1, "Current Reality", random.nextBoolean());
            this.addDrawnCard(deck.get(1), 2, "Path A Outcome", random.nextBoolean());
            this.addDrawnCard(deck.get(2), 3, "Path B Outcome", random.nextBoolean());
        } else {
            this.addDrawnCard(deck.get(0), 1, "Past & Foundations", random.nextBoolean());
            this.addDrawnCard(deck.get(1), 2, "Present Situation", random.nextBoolean());
            this.addDrawnCard(deck.get(2), 3, "Future & Destiny Trends", random.nextBoolean());
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