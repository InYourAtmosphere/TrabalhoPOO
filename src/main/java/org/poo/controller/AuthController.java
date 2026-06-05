package org.poo.controller;

import org.poo.model.AuthenticationToken;
import org.poo.model.pessoa.Funcionario;
import org.poo.model.dto.request.LoginDTO;
import org.poo.repository.AuthenticationTokenRepository;
import org.poo.repository.FuncionarioRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.util.PasswordUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final FuncionarioRepository funcionarioRepository;
    private final AuthenticationTokenRepository tokenRepository;

    public AuthController() {
        UnidadeRepository unidadeRepository = new UnidadeRepository();
        this.funcionarioRepository = new FuncionarioRepository(unidadeRepository);
        this.tokenRepository = new AuthenticationTokenRepository(funcionarioRepository);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        return funcionarioRepository.findByUsername(login.getUsername())
                .filter(f -> f.getPassword() != null && PasswordUtils.verifyPassword(login.getPassword(), f.getPassword()))
                .<ResponseEntity<?>>map(f -> {
                    AuthenticationToken token = new AuthenticationToken();
                    token.setToken(UUID.randomUUID());
                    token.setFuncionario(f);
                    // Expira em 2 horas
                    token.setExpiraEm(System.currentTimeMillis() + (2 * 60 * 60 * 1000));
                    
                    tokenRepository.save(token);
                    return ResponseEntity.ok(token);
                })
                .orElse(ResponseEntity.status(401).body("Usuário ou senha inválidos"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String tokenValue = tokenHeader.substring(7);
            try {
                tokenRepository.deleteByToken(UUID.fromString(tokenValue));
            } catch (IllegalArgumentException e) {
                // Token inválido, ignorar
            }
        }
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
