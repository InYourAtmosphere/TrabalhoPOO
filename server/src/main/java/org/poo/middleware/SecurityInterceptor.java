package org.poo.middleware;

import org.poo.repository.AuthenticationTokenRepository;
import org.poo.model.AuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;
import java.util.Optional;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private final AuthenticationTokenRepository tokenRepository;

    public SecurityInterceptor(AuthenticationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/auth/login") || request.getRequestURI().equals("/error")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String tokenValue = authHeader.substring(7);
            try {
                Optional<AuthenticationToken> tokenOpt = tokenRepository.findByToken(UUID.fromString(tokenValue));
                
                if (tokenOpt.isPresent()) {
                    AuthenticationToken token = tokenOpt.get();
                    if (token.getExpiraEm() > System.currentTimeMillis()) {
                        request.setAttribute("usuarioLogado", token.getFuncionario());
                        return true;
                    } else {
                        tokenRepository.deleteByToken(token.getToken());
                    }
                }
            } catch (IllegalArgumentException e) {
                // Token inválido
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Acesso negado: Token invalido ou expirado.");
        return false;
    }
}
