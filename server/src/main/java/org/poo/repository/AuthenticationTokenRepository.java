package org.poo.repository;

import org.poo.config.DatabaseConfig;
import org.poo.model.AuthenticationToken;
import org.poo.model.pessoa.Funcionario;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthenticationTokenRepository {

    private final FuncionarioRepository funcionarioRepository;

    public AuthenticationTokenRepository(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public void save(AuthenticationToken token) {
        String sql = "INSERT INTO authentication_tokens (token, funcionario_id, expira_em) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, token.getToken());
            stmt.setLong(2, token.getFuncionario().getId());
            stmt.setLong(3, token.getExpiraEm());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar token de autenticação", e);
        }
    }

    public Optional<AuthenticationToken> findByToken(UUID tokenValue) {
        String sql = "SELECT * FROM authentication_tokens WHERE token = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tokenValue);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AuthenticationToken token = new AuthenticationToken();
                    token.setToken(UUID.fromString(rs.getString("token")));
                    token.setExpiraEm(rs.getLong("expira_em"));
                    
                    long funcionarioId = rs.getLong("funcionario_id");
                    funcionarioRepository.findById(funcionarioId).ifPresent(token::setFuncionario);
                    
                    return Optional.of(token);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar token", e);
        }
        return Optional.empty();
    }

    public void deleteByToken(UUID tokenValue) {
        String sql = "DELETE FROM authentication_tokens WHERE token = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tokenValue);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar token", e);
        }
    }

    public void deleteExpired() {
        String sql = "DELETE FROM authentication_tokens WHERE expira_em < ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, System.currentTimeMillis());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tokens expirados", e);
        }
    }
}
