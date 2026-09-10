package tarot.application.features.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.chat.commands.sendchatmessage.ChatMessageDto;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageCommand;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageHandler;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.Reading;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.Gender;
import tarot.domain.enums.SpreadType;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.persistence.repositories.core.ChatMessageRepository;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SendChatMessageHandlerTest {

    private ReadingRepository readingRepository;
    private ChatMessageRepository chatMessageRepository;
    private AccountServiceClient accountServiceClient;
    private AiConsultationService aiService;
    private SendChatMessageHandler handler;

    @BeforeEach
    void setUp() {
        readingRepository = Mockito.mock(ReadingRepository.class);
        chatMessageRepository = Mockito.mock(ChatMessageRepository.class);
        accountServiceClient = Mockito.mock(AccountServiceClient.class);
        aiService = Mockito.mock(AiConsultationService.class);

        handler = new SendChatMessageHandler(
                readingRepository,
                chatMessageRepository,
                accountServiceClient,
                aiService
        );
    }

    @Test
    @DisplayName("handle - Returns EMAIL_NOT_VERIFIED when user email is not verified in Account Service")
    void handle_EmailNotVerified() {
        UUID readingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Reading reading = Reading.create(userId, "My question", null, SpreadType.DAILY_ORACLE, DeckCode.RIDER_WAITE_CLASSIC);
        AccountUserDto unverifiedUser = new AccountUserDto(
                userId,
                "unverified_user",
                "Unverified",
                "",
                "unverified@oracle.com",
                LocalDate.of(2000, 1, 1),
                Gender.UNKNOWN,
                false // NOT verified
        );

        when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));
        when(accountServiceClient.getUser(userId)).thenReturn(Optional.of(unverifiedUser));

        SendChatMessageCommand command = new SendChatMessageCommand("Xin chào AI reader!");
        Result<ChatMessageDto> result = handler.handle(readingId, command);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrorOrNone().code()).isEqualTo("EMAIL_NOT_VERIFIED");
    }

    @Test
    @DisplayName("handle - Successfully sends chat message when user email is verified in Account Service")
    void handle_SuccessWhenEmailVerified() {
        UUID readingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Reading reading = Reading.create(userId, "My question", null, SpreadType.DAILY_ORACLE, DeckCode.RIDER_WAITE_CLASSIC);
        AccountUserDto verifiedUser = new AccountUserDto(
                userId,
                "verified_user",
                "Verified",
                "",
                "verified@oracle.com",
                LocalDate.of(2000, 1, 1),
                Gender.UNKNOWN,
                true // VERIFIED
        );

        when(readingRepository.findById(readingId)).thenReturn(Optional.of(reading));
        when(accountServiceClient.getUser(userId)).thenReturn(Optional.of(verifiedUser));
        when(chatMessageRepository.findByReadingIdOrderByCreatedAtAsc(readingId)).thenReturn(List.of());
        when(aiService.generateChatReply(any(), any(), any(), any())).thenReturn("Chào bạn, tôi là AI Reader.");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SendChatMessageCommand command = new SendChatMessageCommand("Xin chào AI reader!");
        Result<ChatMessageDto> result = handler.handle(readingId, command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDataOrNull().content()).isEqualTo("Chào bạn, tôi là AI Reader.");
    }
}
