package org.poo.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.poo.config.DatabaseConfig;
import org.poo.model.Endereco;
import org.poo.model.Unidade;

public class UnidadeRepository {

    
    public Unidade save(Unidade unidade) {
        if (unidade.getId() == null) {
            return insert(unidade);
        } else {
            return update(unidade);
        }
    }

    private Unidade insert(Unidade unidade) {
        String sql = "INSERT INTO unidades (nome_unidade, logradouro, numero, complemento, bairro, cidade, estado, cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, unidade.getNomeUnidade());
            if (unidade.getEndereco() != null) {
                stmt.setString(2, unidade.getEndereco().getLogradouro());
                stmt.setString(3, unidade.getEndereco().getNumero());
                stmt.setString(4, unidade.getEndereco().getComplemento());
                stmt.setString(5, unidade.getEndereco().getBairro());
                stmt.setString(6, unidade.getEndereco().getCidade());
                stmt.setString(7, unidade.getEndereco().getEstado());
                stmt.setString(8, unidade.getEndereco().getCep());
            } else {
                for (int i = 2; i <= 8; i++) stmt.setNull(i, java.sql.Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir unidade, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    unidade.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao inserir unidade, nenhum ID gerado.");
                }
            }
            return unidade;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar unidade", e);
        }
    }

    private Unidade update(Unidade unidade) {
        String sql = "UPDATE unidades SET nome_unidade = ?, logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, unidade.getNomeUnidade());
            if (unidade.getEndereco() != null) {
                stmt.setString(2, unidade.getEndereco().getLogradouro());
                stmt.setString(3, unidade.getEndereco().getNumero());
                stmt.setString(4, unidade.getEndereco().getComplemento());
                stmt.setString(5, unidade.getEndereco().getBairro());
                stmt.setString(6, unidade.getEndereco().getCidade());
                stmt.setString(7, unidade.getEndereco().getEstado());
                stmt.setString(8, unidade.getEndereco().getCep());
            } else {
                for (int i = 2; i <= 8; i++) stmt.setNull(i, java.sql.Types.VARCHAR);
            }
            stmt.setLong(9, unidade.getId());

            stmt.executeUpdate();
            return unidade;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar unidade", e);
        }
    }

    
    public Optional<Unidade> findById(Long id) {
        String sql = "SELECT * FROM unidades WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUnidade(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar unidade por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Unidade> findAll() {
        String sql = "SELECT * FROM unidades";
        List<Unidade> unidades = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                unidades.add(mapResultSetToUnidade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as unidades", e);
        }
        return unidades;
    }

    
    public void deleteById(Long id) {
        String sql = "DELETE FROM unidades WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar unidade", e);
        }
    }

    private Unidade mapResultSetToUnidade(ResultSet rs) throws SQLException {
        Unidade unidade = new Unidade();
        unidade.setId(rs.getLong("id"));
        unidade.setNomeUnidade(rs.getString("nome_unidade"));
        
        Endereco endereco = new Endereco();
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setComplemento(rs.getString("complemento"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setCep(rs.getString("cep"));
        
        unidade.setEndereco(endereco);
        return unidade;
    }
}
