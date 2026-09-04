package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.profile.commands.claimadreward.ClaimAdRewardHandler;
import tarot.application.features.profile.commands.updatemyprofile.UpdateMyProfileCommand;
import tarot.application.features.profile.commands.updatemyprofile.UpdateMyProfileHandler;
import tarot.application.features.profile.commands.uploadavatar.UploadAvatarHandler;
import tarot.application.features.profile.queries.getmyprofile.GetMyProfileHandler;
import tarot.application.features.profile.queries.getquota.GetUserQuotaHandler;
import tarot.infrastructure.security.jwt.JwtTokenProvider;
import tarot.presentation.common.ActionResult;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "1. User Profile Management", description = "Endpoints for viewing and updating personal profile information")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private final GetMyProfileHandler getMyProfileHandler;
    private final UpdateMyProfileHandler updateMyProfileHandler;
    private final GetUserQuotaHandler getUserQuotaHandler;
    private final ClaimAdRewardHandler claimAdRewardHandler;
    private final UploadAvatarHandler uploadAvatarHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns combined account information and domain profile details")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        UUID userId = jwtTokenProvider.extractUserId(token);
        return ActionResult.from(getMyProfileHandler.handle(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates username, zodiac sign, birth date, bio, and preferences")
    public ResponseEntity<?> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateMyProfileCommand command
    ) {
        String token = authHeader.replace("Bearer ", "").trim();
        UUID userId = jwtTokenProvider.extractUserId(token);
        return ActionResult.from(updateMyProfileHandler.handle(userId, command));
    }

    @GetMapping("/quota")
    @Operation(summary = "Get user reading quota", description = "Returns remaining free readings, ad bonus readings, and ad availability")
    public ResponseEntity<?> getQuota(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) UUID userId
    ) {
        UUID targetUserId = resolveUserId(authHeader, userId);
        if (targetUserId == null) {
            return ActionResult.from(Result.failure(new Error("UNAUTHORIZED", "User ID or Authorization token is required")));
        }
        return ActionResult.from(getUserQuotaHandler.handle(targetUserId));
    }

    @GetMapping("/{userId}/quota")
    @Operation(summary = "Get user reading quota by path parameter", description = "Returns remaining quota for specified user ID")
    public ResponseEntity<?> getQuotaByPath(@PathVariable UUID userId) {
        return ActionResult.from(getUserQuotaHandler.handle(userId));
    }

    @PostMapping("/claim-ad-reward")
    @Operation(summary = "Claim ad reward (+1 reading)", description = "Adds +1 bonus reading after user successfully watches a rewarded video ad")
    public ResponseEntity<?> claimAdReward(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) UUID userId
    ) {
        UUID targetUserId = resolveUserId(authHeader, userId);
        if (targetUserId == null) {
            return ActionResult.from(Result.failure(new Error("UNAUTHORIZED", "User ID or Authorization token is required")));
        }
        return ActionResult.from(claimAdRewardHandler.handle(targetUserId));
    }

    @PostMapping("/{userId}/claim-ad-reward")
    @Operation(summary = "Claim ad reward by path parameter", description = "Adds +1 bonus reading for specified user ID after rewarded ad")
    public ResponseEntity<?> claimAdRewardByPath(@PathVariable UUID userId) {
        return ActionResult.from(claimAdRewardHandler.handle(userId));
    }

    @PostMapping(value = "/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload avatar image", description = "Uploads an avatar image from device and saves it directly to user profile")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "userId", required = false) UUID paramUserId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        UUID userId = resolveUserId(authHeader, paramUserId);
        if (userId == null) {
            return ActionResult.from(Result.failure(new Error("UNAUTHORIZED", "Please log in to update avatar")));
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return ActionResult.from(uploadAvatarHandler.handle(userId, file, baseUrl));
    }

    private UUID resolveUserId(String authHeader, UUID directUserId) {
        if (directUserId != null) {
            return directUserId;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.replace("Bearer ", "").trim();
                return jwtTokenProvider.extractUserId(token);
            } catch (Exception ignored) {}
        }
        return null;
    }
}

