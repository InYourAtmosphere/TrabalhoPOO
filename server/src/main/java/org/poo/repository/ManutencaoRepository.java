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
import org.poo.model.Manutencao;
import org.poo.model.Manutencao.TipoManutencao;

public class ManutencaoRepository {

    private final VeiculoRepository veiculoRepository;

    public ManutencaoRepository(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    
    public Manutencao save(Manutencao manutencao) {
        if (manutencao.getId() == null) {
            return insert(manutencao);
        } else {
            return update(manutencao);
        }
    }

    private Manutencao insert(Manutencao manutencao) {
        String sql = "INSERT INTO manutencoes (veiculo_id, data_inicio, data_fim, descricao, custo, tipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, manutencao.getVeiculo().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(manutencao.getDataInicio()));
            if (manutencao.getDataFim() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(manutencao.getDataFim()));
            } else {
                stmt.setNull(3, java.sql.Types.TIMESTAMP);
            }
            stmt.setString(4, manutencao.getDescricao());
            stmt.setDouble(5, manutencao.getCusto());
            stmt.setString(6, manutencao.getTipo().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir manutenção, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    manutencao.setId(generatedKeys.getLong(1));
                }
            }
            return manutencao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar manutenção", e);
        }
    }

    private Manutencao update(Manutencao manutencao) {
        String sql = "UPDATE manutencoes SET data_fim = ?, descricao = ?, custo = ?, tipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (manutencao.getDataFim() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(manutencao.getDataFim()));
            } else {
                stmt.setNull(1, java.sql.Types.TIMESTAMP);
            }
            stmt.setString(2, manutencao.getDescricao());
            stmt.setDouble(3, manutencao.getCusto());
            stmt.setString(4, manutencao.getTipo().name());
            stmt.setLong(5, manutencao.getId());

            stmt.executeUpdate();
            return manutencao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar manutenção", e);
        }
    }

    
    public Optional<Manutencao> findById(Long id) {
        String sql = "SELECT * FROM manutencoes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToManutencao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar manutenção por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Manutencao> findAll() {
        String sql = "SELECT * FROM manutencoes";
        List<Manutencao> manutencoes = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                manutencoes.add(mapResultSetToManutencao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as manutenções", e);
        }
        return manutencoes;
    }

    
    public void deleteById(Long id) {
        String sql = "DELETE FROM manutencoes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar manutenção", e);
        }
    }

    private Manutencao mapResultSetToManutencao(ResultSet rs) throws SQLException {
        Manutencao manutencao = new Manutencao();
        manutencao.setId(rs.getLong("id"));
        
        long veiculoId = rs.getLong("veiculo_id");
        veiculoRepository.findById(veiculoId).ifPresent(manutencao::setVeiculo);
        
        Timestamp dataInicio = rs.getTimestamp("data_inicio");
        if (dataInicio != null) {
            manutencao.setDataInicio(dataInicio.toLocalDateTime());
        }
        
        Timestamp dataFim = rs.getTimestamp("data_fim");
        if (dataFim != null) {
            manutencao.setDataFim(dataFim.toLocalDateTime());
        }
        
        manutencao.setDescricao(rs.getString("descricao"));
        manutencao.setCusto(rs.getDouble("custo"));
        manutencao.setTipo(TipoManutencao.valueOf(rs.getString("tipo")));
        
        return manutencao;
    }
}
