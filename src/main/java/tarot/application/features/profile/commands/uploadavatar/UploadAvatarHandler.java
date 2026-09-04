package tarot.application.features.profile.commands.uploadavatar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.identity.User;
import tarot.infrastructure.persistence.repositories.identity.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadAvatarHandler {

    private final UserRepository userRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Transactional
    public Result<AvatarUploadDto> handle(UUID userId, MultipartFile file, String baseUrl) {
        if (userId == null) {
            return Result.failure(new Error("UNAUTHORIZED", "Please log in to update avatar"));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        if (file == null || file.isEmpty()) {
            return Result.failure(new Error("FILE_EMPTY", "Image file cannot be empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.failure(new Error("INVALID_FILE_TYPE", "Please select a valid image file (PNG, JPG, WEBP, GIF)"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.failure(new Error("FILE_TOO_LARGE", "Image size exceeds maximum limit of 10MB"));
        }

        try {
            Path uploadDir = Paths.get("uploads", "avatars");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".png";
            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + extension;
            Path targetPath = uploadDir.resolve(filename);
            file.transferTo(targetPath.toFile().getAbsoluteFile());

            String fullUrl = (baseUrl != null ? baseUrl : "") + "/uploads/avatars/" + filename;

            // Directly update and persist avatar to database for User
            user.updateAvatar(fullUrl);
            userRepository.save(user);

            return Result.success(new AvatarUploadDto(fullUrl));
        } catch (Exception e) {
            return Result.failure(new Error("UPLOAD_FAILED", "Failed to upload avatar: " + e.getMessage()));
        }
    }
}
