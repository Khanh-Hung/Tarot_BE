package tarot.application.features.auth.common;

import tarot.domain.entities.identity.User;

import java.util.UUID;

public record AuthResponse(
    String token,
    UUID userId,
    String email,
    String username,
    String role
) {
    public static AuthResponse fromEntity(User user, String token) {
        if (user == null) return null;
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getRole().name()
        );
    }
}