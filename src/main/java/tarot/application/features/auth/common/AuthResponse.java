package tarot.application.features.auth.common;

import tarot.domain.entities.User;

public record AuthResponse(
    String token,
    Long userId,
    String email,
    String username,
    String zodiacSign,
    String role
) {
    public static AuthResponse fromEntity(User user, String token) {
        if (user == null) return null;
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getZodiacSign().name(),
            user.getRole().name()
        );
    }
}