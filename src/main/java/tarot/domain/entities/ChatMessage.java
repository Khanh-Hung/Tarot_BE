package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.MessageSender;

@Entity
@Table(name = "reading_chat_messages")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_id", nullable = false)
    private Reading reading;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false, length = 20)
    private MessageSender sender;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}