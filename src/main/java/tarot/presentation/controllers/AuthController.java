package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.common.result.Result;
import tarot.application.features.auth.commands.login.LoginCommand;
import tarot.application.features.auth.commands.login.LoginHandler;
import tarot.application.features.auth.commands.register.RegisterCommand;
import tarot.application.features.auth.commands.register.RegisterHandler;
import tarot.application.features.auth.common.AuthResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "0. Authentication & Authorization", description = "Endpoints for user registration, login, and JWT token issuance")
@CrossOrigin(origins = "*")
public class AuthController {

    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account", description = "Creates a new user profile, hashes password with BCrypt, and issues a JWT token")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        Result<AuthResponse> result = registerHandler.handle(command);
        if (result.isFailure()) {
            return ResponseEntity.badRequest().body(result.getErrorOrNone());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getDataOrNull());
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT", description = "Validates user credentials and returns a Bearer JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginCommand command) {
        Result<AuthResponse> result = loginHandler.handle(command);
        if (result.isFailure()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result.getErrorOrNone());
        }
        return ResponseEntity.ok(result.getDataOrNull());
    }
}