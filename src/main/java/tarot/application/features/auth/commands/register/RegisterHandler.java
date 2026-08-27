package tarot.application.features.auth.commands.register;

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
public class RegisterHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Result<AuthResponse> handle(RegisterCommand command) {
        String normalizedEmail = command.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            return Result.failure(new Error("EMAIL_ALREADY_EXISTS", "An account with this email already exists: " + normalizedEmail));
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.create(normalizedEmail, passwordHash);

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved);

        return Result.success(AuthResponse.fromEntity(saved, token));
    }
}