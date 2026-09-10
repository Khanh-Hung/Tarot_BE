package tarot.application.features.reading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.reading.commands.createreading.CreateReadingCommand;
import tarot.application.features.reading.commands.createreading.CreateReadingHandler;
import tarot.application.features.reading.commands.createreading.CreateReadingResponse;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.Card;
import tarot.domain.entities.core.Reading;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserQuota;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.ArcanaType;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.Gender;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.ai.core.AiReadingResult;
import tarot.infrastructure.persistence.repositories.core.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateReadingHandlerTest {

    private AccountServiceClient accountServiceClient;
    private UserProfileRepository profileRepository;
    private UserQuotaRepository quotaRepository;
    private UserStreakRepository streakRepository;
    private CardRepository cardRepository;
    private ReadingRepository readingRepository;
    private AiConsultationService aiService;
    private CreateReadingHandler handler;

    @BeforeEach
    void setUp() {
        accountServiceClient = Mockito.mock(AccountServiceClient.class);
        profileRepository = Mockito.mock(UserProfileRepository.class);
        quotaRepository = Mockito.mock(UserQuotaRepository.class);
        streakRepository = Mockito.mock(UserStreakRepository.class);
        cardRepository = Mockito.mock(CardRepository.class);
        readingRepository = Mockito.mock(ReadingRepository.class);
        aiService = Mockito.mock(AiConsultationService.class);

        handler = new CreateReadingHandler(
                accountServiceClient,
                profileRepository,
                quotaRepository,
                streakRepository,
                cardRepository,
                readingRepository,
                aiService
        );
    }

    @Test
    @DisplayName("handle - Successfully creates reading with user data fetched via AccountServiceClient")
    void handle_Success() {
        UUID userId = UUID.randomUUID();
        AccountUserDto accountUser = new AccountUserDto(
                userId,
                "tarot_seeker",
                "Seeker",
                "https://avatar.com/1.png",
                "seeker@oracle.com",
                LocalDate.of(1998, 5, 20),
                Gender.MALE,
                true
        );

        Card card1 = Card.builder().nameEn("The Fool").nameVi("Chàng Khờ").deckCode(DeckCode.RIDER_WAITE_CLASSIC).arcanaType(ArcanaType.MAJOR_ARCANA).build();
        Card card2 = Card.builder().nameEn("The Magician").nameVi("Ảo Thuật Gia").deckCode(DeckCode.RIDER_WAITE_CLASSIC).arcanaType(ArcanaType.MAJOR_ARCANA).build();
        Card card3 = Card.builder().nameEn("The High Priestess").nameVi("Nữ Tư Tế").deckCode(DeckCode.RIDER_WAITE_CLASSIC).arcanaType(ArcanaType.MAJOR_ARCANA).build();

        CreateReadingCommand command = new CreateReadingCommand(
                userId,
                "Tương lai công việc của tôi thế nào?",
                ZodiacSign.TAURUS,
                null,
                RelationshipStatus.SINGLE,
                SpreadType.PAST_PRESENT_FUTURE,
                DeckCode.RIDER_WAITE_CLASSIC,
                List.of(),
                List.of()
        );

        UserQuota quota = UserQuota.createDefault(userId);
        quota.addBonusReadings(1);
        when(accountServiceClient.getUser(userId)).thenReturn(Optional.of(accountUser));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(quotaRepository.findByUserId(userId)).thenReturn(Optional.of(quota));
        when(streakRepository.findByUserId(userId)).thenReturn(Optional.of(UserStreak.createDefault(userId)));
        when(cardRepository.findByDeckCode(DeckCode.RIDER_WAITE_CLASSIC)).thenReturn(List.of(card1, card2, card3));
        when(aiService.generateInitialReading(any(), any(), any(), any(), any(), any()))
                .thenReturn(new AiReadingResult(Topic.CAREER_AND_FINANCE, "# Kết quả xem bài"));
        when(readingRepository.save(any(Reading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Result<CreateReadingResponse> result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        CreateReadingResponse response = result.getDataOrNull();
        assertThat(response).isNotNull();
        assertThat(response.topic()).isEqualTo("CAREER_AND_FINANCE");
        assertThat(response.spreadType()).isEqualTo("PAST_PRESENT_FUTURE");
    }

    @Test
    @DisplayName("handle - Returns USER_NOT_FOUND when user does not exist in Account Service")
    void handle_UserNotFound() {
        UUID userId = UUID.randomUUID();
        CreateReadingCommand command = new CreateReadingCommand(
                userId,
                "Câu hỏi test?",
                ZodiacSign.ARIES,
                null,
                RelationshipStatus.SINGLE,
                SpreadType.DAILY_ORACLE,
                DeckCode.RIDER_WAITE_CLASSIC,
                List.of(),
                List.of()
        );

        when(accountServiceClient.getUser(userId)).thenReturn(Optional.empty());

        Result<CreateReadingResponse> result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrorOrNone().code()).isEqualTo("USER_NOT_FOUND");
    }
}
