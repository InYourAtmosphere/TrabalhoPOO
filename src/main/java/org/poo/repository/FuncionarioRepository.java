package org.poo.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.poo.config.DatabaseConfig;
import org.poo.model.Unidade;
import org.poo.model.pessoa.Funcionario;

public class FuncionarioRepository {

    private final UnidadeRepository unidadeRepository;

    public FuncionarioRepository(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    
    public Funcionario save(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            return insert(funcionario);
        } else {
            return update(funcionario);
        }
    }

    private Funcionario insert(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (nome, telefone, email, username, password, data_cadastro, matricula, cargo, unidade_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getTelefone());
            stmt.setString(3, funcionario.getEmail());
            stmt.setString(4, funcionario.getUsername());
            stmt.setString(5, funcionario.getPassword());
            stmt.setTimestamp(6, Timestamp.valueOf(funcionario.getDataCadastro()));
            stmt.setString(7, funcionario.getMatricula());
            stmt.setString(8, funcionario.getCargo());
            
            if (funcionario.getUnidade() != null) {
                stmt.setLong(9, funcionario.getUnidade().getId());
            } else {
                stmt.setNull(9, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir funcionario, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    funcionario.setId(generatedKeys.getLong(1));
                }
            }
            return funcionario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar funcionario", e);
        }
    }

    private Funcionario update(Funcionario funcionario) {
        String sql = "UPDATE funcionarios SET nome = ?, telefone = ?, email = ?, username = ?, password = ?, matricula = ?, cargo = ?, unidade_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getTelefone());
            stmt.setString(3, funcionario.getEmail());
            stmt.setString(4, funcionario.getUsername());
            stmt.setString(5, funcionario.getPassword());
            stmt.setString(6, funcionario.getMatricula());
            stmt.setString(7, funcionario.getCargo());
            
            if (funcionario.getUnidade() != null) {
                stmt.setLong(8, funcionario.getUnidade().getId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }
            stmt.setLong(9, funcionario.getId());

            stmt.executeUpdate();
            return funcionario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar funcionario", e);
        }
    }

    
    public Optional<Funcionario> findById(Long id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFuncionario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionario por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Funcionario> findAll() {
        String sql = "SELECT * FROM funcionarios";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                funcionarios.add(mapResultSetToFuncionario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os funcionarios", e);
        }
        return funcionarios;
    }

    
    public void deleteById(Long id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar funcionario", e);
        }
    }

    public Optional<Funcionario> findByUsername(String username) {
        String sql = "SELECT * FROM funcionarios WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFuncionario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionario por username", e);
        }
        return Optional.empty();
    }

    private Funcionario mapResultSetToFuncionario(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setTelefone(rs.getString("telefone"));
        funcionario.setEmail(rs.getString("email"));
        funcionario.setUsername(rs.getString("username"));
        funcionario.setPassword(rs.getString("password"));
        funcionario.setMatricula(rs.getString("matricula"));
        funcionario.setCargo(rs.getString("cargo"));
        
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            funcionario.setDataCadastro(timestamp.toLocalDateTime());
        }

        long unidadeId = rs.getLong("unidade_id");
        if (!rs.wasNull()) {
            unidadeRepository.findById(unidadeId).ifPresent(funcionario::setUnidade);
        }
        
        return funcionario;
    }
}
