package tarot.application.features.profile.commands.updatemyprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.features.profile.queries.getmyprofile.ProfileDto;
import tarot.domain.entities.User;
import tarot.domain.entities.UserProfile;
import tarot.infrastructure.persistence.repositories.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UpdateMyProfileHandler {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    @Transactional
    public Result<ProfileDto> handle(Long userId, UpdateMyProfileCommand command) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        // 1. Cập nhật thông tin dùng chung vào bảng users
        user.updatePersonalInfo(
                command.username(),
                command.birthDate(),
                command.birthTime(),
                command.bio(),
                command.avatarUrl()
        );
        User savedUser = userRepository.save(user);

        // 2. Cập nhật cài đặt riêng vào bảng user_profiles
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.createDefault(userId, null));

        profile.updatePreferences(
                command.zodiacSign(),
                command.favoriteDeckId()
        );
        UserProfile savedProfile = profileRepository.save(profile);

        return Result.success(ProfileDto.fromEntity(savedUser, savedProfile));
    }
}
