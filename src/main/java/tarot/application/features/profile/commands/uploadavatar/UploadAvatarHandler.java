package tarot.application.features.profile.commands.uploadavatar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.interfaces.AccountServiceClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadAvatarHandler {

    private final AccountServiceClient accountServiceClient;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Transactional
    public Result<AvatarUploadDto> handle(UUID userId, MultipartFile file, String baseUrl) {
        if (userId == null) {
            return Result.failure(new Error("UNAUTHORIZED", "Please log in to update avatar"));
        }

        Optional<AccountUserDto> userOpt = accountServiceClient.getUser(userId);
        if (userOpt.isEmpty()) {
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

            return Result.success(new AvatarUploadDto(fullUrl));
        } catch (Exception e) {
            return Result.failure(new Error("UPLOAD_FAILED", "Failed to upload avatar: " + e.getMessage()));
        }
    }
}
