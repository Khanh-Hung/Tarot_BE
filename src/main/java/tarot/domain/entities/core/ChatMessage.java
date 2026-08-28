package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.MessageSender;

@Entity
@Table(name = "\"ReadingChatMessages\"")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class ChatMessage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "\"ReadingId\"", nullable = false)
    private Reading reading;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"Sender\"", nullable = false, length = 20)
    private MessageSender sender;

    @Column(name = "\"Content\"", nullable = false, columnDefinition = "TEXT")
    private String content;
}