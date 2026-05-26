package org.poo.repository;

import org.poo.config.DatabaseConfig;
import org.poo.model.Veiculo;
import org.poo.model.StatusVeiculo;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class VeiculoRepository {

    public List<Veiculo> findAll() throws SQLException {
        String sql = "SELECT * FROM veiculos";
        List<Veiculo> veiculos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));
                v.setAno(Year.of(rs.getInt("ano")));
                v.setPlaca(rs.getString("placa"));
                v.setChassi(rs.getString("chassi"));
                v.setKmAtual(rs.getFloat("km_atual"));
                v.atualizarStatus(StatusVeiculo.valueOf(rs.getString("status")));
                veiculos.add(v);
            }
        }
        return veiculos;
    }

    public void save(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculos (marca, modelo, ano, placa, chassi, km_atual, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, v.getMarca());
            pstmt.setString(2, v.getModelo());
            pstmt.setInt(3, v.getAno().getValue());
            pstmt.setString(4, v.getPlaca());
            pstmt.setString(5, v.getChassi());
            pstmt.setFloat(6, v.getKmAtual());
            pstmt.setString(7, v.getStatus().name());
            
            pstmt.executeUpdate();
        }
    }
}
