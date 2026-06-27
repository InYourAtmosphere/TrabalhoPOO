package org.poo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.poo.model.dto.request.LoginDTO;
import org.poo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        return authService.login(login)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body("Usuário ou senha inválidos"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String tokenHeader) {
        authService.logout(tokenHeader);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
