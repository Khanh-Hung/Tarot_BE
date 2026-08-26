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
import java.util.List;

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

    @Setter
    @Column(name = "initial_reading", columnDefinition = "TEXT")
    private String initialReading;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // --- DOMAIN METHODS ---

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