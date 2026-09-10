package tarot.application.features.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.profile.queries.getmyprofile.GetMyProfileHandler;
import tarot.application.features.profile.queries.getmyprofile.ProfileDto;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.Gender;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.core.UserStreakRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GetMyProfileHandlerTest {

    private AccountServiceClient accountServiceClient;
    private UserProfileRepository profileRepository;
    private UserStreakRepository streakRepository;
    private GetMyProfileHandler handler;

    @BeforeEach
    void setUp() {
        accountServiceClient = Mockito.mock(AccountServiceClient.class);
        profileRepository = Mockito.mock(UserProfileRepository.class);
        streakRepository = Mockito.mock(UserStreakRepository.class);
        handler = new GetMyProfileHandler(accountServiceClient, profileRepository, streakRepository);
    }

    @Test
    @DisplayName("handle - Returns combined ProfileDto when user and profile exist")
    void handle_Success() {
        UUID userId = UUID.randomUUID();
        AccountUserDto accountUser = new AccountUserDto(
                userId,
                "mystic_user",
                "Mystic Traveler",
                "https://avatar.com/mystic.png",
                "mystic@tarot.com",
                LocalDate.of(2000, 1, 15),
                Gender.FEMALE,
                true
        );

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .zodiacSign(ZodiacSign.CAPRICORN)
                .relationshipStatus(RelationshipStatus.SINGLE)
                .build();

        UserStreak streak = UserStreak.builder()
                .userId(userId)
                .currentStreak(5)
                .longestStreak(10)
                .lastStreakDate(LocalDate.now())
                .build();

        when(accountServiceClient.getUser(userId)).thenReturn(Optional.of(accountUser));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(streakRepository.findByUserId(userId)).thenReturn(Optional.of(streak));

        Result<ProfileDto> result = handler.handle(userId);

        assertThat(result.isSuccess()).isTrue();
        ProfileDto dto = result.getDataOrNull();
        assertThat(dto).isNotNull();
        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.email()).isEqualTo("mystic@tarot.com");
        assertThat(dto.displayName()).isEqualTo("Mystic Traveler");
        assertThat(dto.zodiacSign()).isEqualTo(ZodiacSign.CAPRICORN);
        assertThat(dto.relationshipStatus()).isEqualTo(RelationshipStatus.SINGLE);
        assertThat(dto.birthCard()).isNotNull();
    }

    @Test
    @DisplayName("handle - Returns USER_NOT_FOUND when user does not exist in Account Service")
    void handle_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(accountServiceClient.getUser(userId)).thenReturn(Optional.empty());

        Result<ProfileDto> result = handler.handle(userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrorOrNone().code()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    @DisplayName("handle - Automatically calculates zodiac sign and birth card when date of birth is updated in Account Service")
    void handle_CalculatesZodiacSignFromDateOfBirth() {
        UUID userId = UUID.randomUUID();
        // Date of birth: Oct 25 -> Scorpio
        AccountUserDto accountUser = new AccountUserDto(
                userId,
                "seeker",
                "Seeker",
                null,
                "seeker@example.com",
                LocalDate.of(1996, 10, 25),
                Gender.OTHER,
                true
        );

        // Profile without explicit zodiac sign
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .relationshipStatus(RelationshipStatus.IN_RELATIONSHIP)
                .build();

        when(accountServiceClient.getUser(userId)).thenReturn(Optional.of(accountUser));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(streakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Result<ProfileDto> result = handler.handle(userId);

        assertThat(result.isSuccess()).isTrue();
        ProfileDto dto = result.getDataOrNull();
        assertThat(dto).isNotNull();
        assertThat(dto.zodiacSign()).isEqualTo(ZodiacSign.SCORPIO);
        assertThat(dto.birthCard()).isNotNull();
    }
}
