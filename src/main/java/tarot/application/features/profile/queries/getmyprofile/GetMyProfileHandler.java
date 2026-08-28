package tarot.application.features.profile.queries.getmyprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.identity.User;
import tarot.domain.entities.core.UserProfile;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.identity.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyProfileHandler {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    public Result<ProfileDto> handle(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        UserProfile profile = profileRepository.findByUserId(userId).orElse(null);
        return Result.success(ProfileDto.fromEntity(user, profile));
    }
}
