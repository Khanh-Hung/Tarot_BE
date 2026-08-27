package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.auth.commands.login.LoginCommand;
import tarot.application.features.auth.commands.login.LoginHandler;
import tarot.application.features.auth.commands.register.RegisterCommand;
import tarot.application.features.auth.commands.register.RegisterHandler;
import tarot.presentation.common.ActionResult;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "0. Authentication & Authorization", description = "Endpoints for user registration, login, and JWT token issuance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account", description = "Creates a new user profile, hashes password with BCrypt, and issues a JWT token")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return ActionResult.from(registerHandler.handle(command), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT", description = "Validates user credentials and returns a Bearer JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginCommand command) {
        return ActionResult.from(loginHandler.handle(command));
    }
}