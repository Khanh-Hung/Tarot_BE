package tarot.application.features.auth.commands.login;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.features.auth.common.AuthResponse;
import tarot.domain.entities.User;
import tarot.infrastructure.persistence.repositories.UserRepository;
import tarot.infrastructure.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Result<AuthResponse> handle(LoginCommand command) {
        String normalizedEmail = command.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null) {
            return Result.failure(new Error("INVALID_CREDENTIALS", "Invalid email or password."));
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            return Result.failure(new Error("INVALID_CREDENTIALS", "Invalid email or password."));
        }

        if (!user.isActive()) {
            return Result.failure(new Error("USER_DEACTIVATED", "Your account has been deactivated. Please contact support."));
        }

        String token = jwtTokenProvider.generateToken(user);
        return Result.success(AuthResponse.fromEntity(user, token));
    }
}