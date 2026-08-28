package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.profile.commands.updatemyprofile.UpdateMyProfileCommand;
import tarot.application.features.profile.commands.updatemyprofile.UpdateMyProfileHandler;
import tarot.application.features.profile.queries.getmyprofile.GetMyProfileHandler;
import tarot.infrastructure.security.jwt.JwtTokenProvider;
import tarot.presentation.common.ActionResult;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "1. User Profile Management", description = "Endpoints for viewing and updating personal profile information")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private final GetMyProfileHandler getMyProfileHandler;
    private final UpdateMyProfileHandler updateMyProfileHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns combined account information and domain profile details")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        Long userId = jwtTokenProvider.extractUserId(token);
        return ActionResult.from(getMyProfileHandler.handle(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates username, zodiac sign, birth date, bio, and preferences")
    public ResponseEntity<?> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateMyProfileCommand command
    ) {
        String token = authHeader.replace("Bearer ", "").trim();
        Long userId = jwtTokenProvider.extractUserId(token);
        return ActionResult.from(updateMyProfileHandler.handle(userId, command));
    }
}

