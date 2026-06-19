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
import org.poo.model.veiculo.CarroPopular;
import org.poo.model.veiculo.Motocicleta;
import org.poo.model.veiculo.StatusVeiculo;
import org.poo.model.veiculo.Veiculo;

public class VeiculoRepository {

    private final UnidadeRepository unidadeRepository;

    public VeiculoRepository(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    
    public Veiculo save(Veiculo veiculo) {
        if (veiculo.getId() == null) {
            return insert(veiculo);
        } else {
            return update(veiculo);
        }
    }

    private Veiculo insert(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (marca, modelo, ano, placa, chassi, km_atual, status, data_cadastro, tipo_veiculo, qtd_portas, tem_ar_condicionado, cilindrada, tem_bau, ativo, unidade_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, veiculo.getMarca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setInt(3, veiculo.getAno());
            stmt.setString(4, veiculo.getPlaca());
            stmt.setString(5, veiculo.getChassi());
            stmt.setDouble(6, veiculo.getKmAtual());
            stmt.setString(7, veiculo.getStatus().name());
            stmt.setTimestamp(8, Timestamp.valueOf(veiculo.getDataCadastro()));
            
            if (veiculo instanceof CarroPopular) {
                CarroPopular carro = (CarroPopular) veiculo;
                stmt.setString(9, "CARRO_POPULAR");
                stmt.setInt(10, carro.getQuantidadePortas());
                stmt.setBoolean(11, carro.isTemArCondicionado());
                stmt.setNull(12, java.sql.Types.INTEGER);
                stmt.setNull(13, java.sql.Types.BOOLEAN);
            } else if (veiculo instanceof Motocicleta) {
                Motocicleta moto = (Motocicleta) veiculo;
                stmt.setString(9, "MOTOCICLETA");
                stmt.setNull(10, java.sql.Types.INTEGER);
                stmt.setNull(11, java.sql.Types.BOOLEAN);
                stmt.setInt(12, moto.getCilindrada());
                stmt.setBoolean(13, moto.isTemBau());
            }
            stmt.setBoolean(14, veiculo.isAtivo());
            
            if (veiculo.getUnidade() != null) {
                stmt.setLong(15, veiculo.getUnidade().getId());
            } else {
                stmt.setNull(15, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir veiculo, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    veiculo.setId(generatedKeys.getLong(1));
                }
            }
            return veiculo;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar veiculo", e);
        }
    }

    private Veiculo update(Veiculo veiculo) {
        String sql = "UPDATE veiculos SET marca = ?, modelo = ?, ano = ?, placa = ?, chassi = ?, km_atual = ?, status = ?, qtd_portas = ?, tem_ar_condicionado = ?, cilindrada = ?, tem_bau = ?, ativo = ?, unidade_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, veiculo.getMarca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setInt(3, veiculo.getAno());
            stmt.setString(4, veiculo.getPlaca());
            stmt.setString(5, veiculo.getChassi());
            stmt.setDouble(6, veiculo.getKmAtual());
            stmt.setString(7, veiculo.getStatus().name());
            
            if (veiculo instanceof CarroPopular) {
                CarroPopular carro = (CarroPopular) veiculo;
                stmt.setInt(8, carro.getQuantidadePortas());
                stmt.setBoolean(9, carro.isTemArCondicionado());
                stmt.setNull(10, java.sql.Types.INTEGER);
                stmt.setNull(11, java.sql.Types.BOOLEAN);
            } else if (veiculo instanceof Motocicleta) {
                Motocicleta moto = (Motocicleta) veiculo;
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.BOOLEAN);
                stmt.setInt(10, moto.getCilindrada());
                stmt.setBoolean(11, moto.isTemBau());
            }
            stmt.setBoolean(12, veiculo.isAtivo());
            
            if (veiculo.getUnidade() != null) {
                stmt.setLong(13, veiculo.getUnidade().getId());
            } else {
                stmt.setNull(13, java.sql.Types.BIGINT);
            }
            stmt.setLong(14, veiculo.getId());

            stmt.executeUpdate();
            return veiculo;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar veiculo", e);
        }
    }

    
    public Optional<Veiculo> findById(Long id) {
        String sql = "SELECT * FROM veiculos WHERE id = ? AND ativo = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVeiculo(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veiculo por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Veiculo> findAll() {
        String sql = "SELECT * FROM veiculos WHERE ativo = TRUE";
        List<Veiculo> veiculos = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                veiculos.add(mapResultSetToVeiculo(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os veiculos", e);
        }
        return veiculos;
    }

    
    public void updateStatus(Long id, StatusVeiculo status) {
        String sql = "UPDATE veiculos SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do veículo", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "UPDATE veiculos SET ativo = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar veiculo (logicamente)", e);
        }
    }

    private Veiculo mapResultSetToVeiculo(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_veiculo");
        Veiculo veiculo;
        
        if ("CARRO_POPULAR".equals(tipo)) {
            CarroPopular carro = new CarroPopular();
            carro.setQuantidadePortas(rs.getInt("qtd_portas"));
            carro.setTemArCondicionado(rs.getBoolean("tem_ar_condicionado"));
            veiculo = carro;
        } else {
            Motocicleta moto = new Motocicleta();
            moto.setCilindrada(rs.getInt("cilindrada"));
            moto.setTemBau(rs.getBoolean("tem_bau"));
            veiculo = moto;
        }
        
        veiculo.setId(rs.getLong("id"));
        veiculo.setMarca(rs.getString("marca"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setAno(rs.getInt("ano"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setChassi(rs.getString("chassi"));
        veiculo.setKmAtual(rs.getDouble("km_atual"));
        veiculo.setStatus(StatusVeiculo.valueOf(rs.getString("status")));
        
        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            veiculo.setDataCadastro(dataCadastro.toLocalDateTime());
        }
        veiculo.setAtivo(rs.getBoolean("ativo"));

        long unidadeId = rs.getLong("unidade_id");
        if (!rs.wasNull()) {
            unidadeRepository.findById(unidadeId).ifPresent(veiculo::setUnidade);
        }
        
        return veiculo;
    }
}
