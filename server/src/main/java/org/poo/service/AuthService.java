package org.poo.service;

import org.poo.model.AuthenticationToken;
import org.poo.model.dto.request.LoginDTO;
import org.poo.model.dto.response.LoginResponseDTO;
import org.poo.repository.AuthenticationTokenRepository;
import org.poo.repository.FuncionarioRepository;
import org.poo.util.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final long TOKEN_TTL_MS = 2 * 60 * 60 * 1000;

    private final FuncionarioRepository funcionarioRepository;
    private final AuthenticationTokenRepository tokenRepository;

    public AuthService(FuncionarioRepository funcionarioRepository,
                       AuthenticationTokenRepository tokenRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.tokenRepository = tokenRepository;
    }

    public Optional<LoginResponseDTO> login(LoginDTO login) {
        return funcionarioRepository.findByUsername(login.getUsername())
                .filter(f -> f.getPassword() != null
                        && PasswordUtils.verifyPassword(login.getPassword(), f.getPassword()))
                .map(f -> {
                    AuthenticationToken token = new AuthenticationToken();
                    token.setToken(UUID.randomUUID());
                    token.setFuncionario(f);
                    token.setExpiraEm(System.currentTimeMillis() + TOKEN_TTL_MS);
                    tokenRepository.save(token);
                    return new LoginResponseDTO(
                            token.getToken().toString(),
                            token.getExpiraEm(),
                            f.getCargo().name(),
                            f.getNome()
                    );
                });
    }

    public void logout(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            try {
                tokenRepository.deleteByToken(UUID.fromString(tokenHeader.substring(7)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
