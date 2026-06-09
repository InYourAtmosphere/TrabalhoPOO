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
import org.poo.model.Contrato;
import org.poo.model.Contrato.StatusContrato;

public class ContratoRepository {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UnidadeRepository unidadeRepository;

    public ContratoRepository(ClienteRepository clienteRepository, VeiculoRepository veiculoRepository, UnidadeRepository unidadeRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    
    public Contrato save(Contrato contrato) {
        if (contrato.getId() == null) {
            return insert(contrato);
        } else {
            return update(contrato);
        }
    }

    private Contrato insert(Contrato contrato) {
        String sql = "INSERT INTO contratos (cliente_id, veiculo_id, unidade_retirada_id, unidade_devolucao_id, data_inicio, data_fim_prevista, valor_diaria, valor_total, status, km_inicial, km_final, forma_pagamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, contrato.getCliente().getId());
            stmt.setLong(2, contrato.getVeiculo().getId());
            stmt.setLong(3, contrato.getUnidadeRetirada().getId());
            if (contrato.getUnidadeDevolucao() != null) {
                stmt.setLong(4, contrato.getUnidadeDevolucao().getId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(contrato.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(contrato.getDataFimPrevista()));
            stmt.setDouble(7, contrato.getValorDiaria());
            stmt.setObject(8, contrato.getValorTotal());
            stmt.setString(9, contrato.getStatus().name());
            stmt.setObject(10, contrato.getKmInicial());
            stmt.setObject(11, contrato.getKmFinal());
            stmt.setString(12, contrato.getFormaPagamento());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir contrato, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contrato.setId(generatedKeys.getLong(1));
                }
            }
            return contrato;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar contrato", e);
        }
    }

    private Contrato update(Contrato contrato) {
        String sql = "UPDATE contratos SET unidade_devolucao_id = ?, data_fim_real = ?, valor_total = ?, status = ?, km_inicial = ?, km_final = ?, forma_pagamento = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (contrato.getUnidadeDevolucao() != null) {
                stmt.setLong(1, contrato.getUnidadeDevolucao().getId());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            
            if (contrato.getDataFimReal() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(contrato.getDataFimReal()));
            } else {
                stmt.setNull(2, java.sql.Types.TIMESTAMP);
            }
            
            stmt.setObject(3, contrato.getValorTotal());
            stmt.setString(4, contrato.getStatus().name());
            stmt.setObject(5, contrato.getKmInicial());
            stmt.setObject(6, contrato.getKmFinal());
            stmt.setString(7, contrato.getFormaPagamento());
            stmt.setLong(8, contrato.getId());

            stmt.executeUpdate();
            return contrato;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar contrato", e);
        }
    }

    
    public Optional<Contrato> findById(Long id) {
        String sql = "SELECT * FROM contratos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToContrato(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contrato por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Contrato> findAll() {
        String sql = "SELECT * FROM contratos";
        List<Contrato> contratos = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contratos.add(mapResultSetToContrato(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os contratos", e);
        }
        return contratos;
    }

    
    public void deleteById(Long id) {
        String sql = "DELETE FROM contratos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar contrato", e);
        }
    }

    private Contrato mapResultSetToContrato(ResultSet rs) throws SQLException {
        Contrato contrato = new Contrato();
        contrato.setId(rs.getLong("id"));
        
        long clienteId = rs.getLong("cliente_id");
        clienteRepository.findById(clienteId).ifPresent(contrato::setCliente);
        
        long veiculoId = rs.getLong("veiculo_id");
        veiculoRepository.findById(veiculoId).ifPresent(contrato::setVeiculo);
        
        long unidadeRetiradaId = rs.getLong("unidade_retirada_id");
        unidadeRepository.findById(unidadeRetiradaId).ifPresent(contrato::setUnidadeRetirada);
        
        long unidadeDevolucaoId = rs.getLong("unidade_devolucao_id");
        if (!rs.wasNull()) {
            unidadeRepository.findById(unidadeDevolucaoId).ifPresent(contrato::setUnidadeDevolucao);
        }
        
        Timestamp dataInicio = rs.getTimestamp("data_inicio");
        if (dataInicio != null) {
            contrato.setDataInicio(dataInicio.toLocalDateTime());
        }
        
        Timestamp dataFimPrevista = rs.getTimestamp("data_fim_prevista");
        if (dataFimPrevista != null) {
            contrato.setDataFimPrevista(dataFimPrevista.toLocalDateTime());
        }
        
        Timestamp dataFimReal = rs.getTimestamp("data_fim_real");
        if (dataFimReal != null) {
            contrato.setDataFimReal(dataFimReal.toLocalDateTime());
        }
        
        contrato.setValorDiaria(rs.getDouble("valor_diaria"));
        contrato.setValorTotal(rs.getDouble("valor_total"));
        contrato.setStatus(StatusContrato.valueOf(rs.getString("status")));
        
        contrato.setKmInicial(rs.getDouble("km_inicial"));
        contrato.setKmFinal(rs.getDouble("km_final"));
        contrato.setFormaPagamento(rs.getString("forma_pagamento"));
        
        return contrato;
    }
}
