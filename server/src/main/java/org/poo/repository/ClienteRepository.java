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
import org.poo.model.Endereco;
import org.poo.model.pessoa.Cliente;

public class ClienteRepository {

    
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            return insert(cliente);
        } else {
            return update(cliente);
        }
    }

    private Cliente insert(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, telefone, email, data_cadastro, documento_identidade, documento_habilitacao, logradouro, numero, complemento, bairro, cidade, estado, cep, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setTimestamp(4, Timestamp.valueOf(cliente.getDataCadastro()));
            stmt.setString(5, cliente.getDocumentoIdentidade());
            stmt.setString(6, cliente.getDocumentoHabilitacao());
            
            if (cliente.getEndereco() != null) {
                stmt.setString(7, cliente.getEndereco().getLogradouro());
                stmt.setString(8, cliente.getEndereco().getNumero());
                stmt.setString(9, cliente.getEndereco().getComplemento());
                stmt.setString(10, cliente.getEndereco().getBairro());
                stmt.setString(11, cliente.getEndereco().getCidade());
                stmt.setString(12, cliente.getEndereco().getEstado());
                stmt.setString(13, cliente.getEndereco().getCep());
            } else {
                for (int i = 7; i <= 13; i++) stmt.setNull(i, java.sql.Types.VARCHAR);
            }
            stmt.setBoolean(14, cliente.isAtivo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir cliente, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getLong(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }

    private Cliente update(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, email = ?, documento_identidade = ?, documento_habilitacao = ?, logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ?, ativo = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getDocumentoIdentidade());
            stmt.setString(5, cliente.getDocumentoHabilitacao());
            
            if (cliente.getEndereco() != null) {
                stmt.setString(6, cliente.getEndereco().getLogradouro());
                stmt.setString(7, cliente.getEndereco().getNumero());
                stmt.setString(8, cliente.getEndereco().getComplemento());
                stmt.setString(9, cliente.getEndereco().getBairro());
                stmt.setString(10, cliente.getEndereco().getCidade());
                stmt.setString(11, cliente.getEndereco().getEstado());
                stmt.setString(12, cliente.getEndereco().getCep());
            } else {
                for (int i = 6; i <= 12; i++) stmt.setNull(i, java.sql.Types.VARCHAR);
            }
            stmt.setBoolean(13, cliente.isAtivo());
            stmt.setLong(14, cliente.getId());

            stmt.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente", e);
        }
    }

    
    public Optional<Cliente> findById(Long id) {
        String sql = "SELECT * FROM clientes WHERE id = ? AND ativo = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por ID", e);
        }
        return Optional.empty();
    }

    
    public List<Cliente> findAll() {
        String sql = "SELECT * FROM clientes WHERE ativo = TRUE";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os clientes", e);
        }
        return clientes;
    }

    
    public void deleteById(Long id) {
        String sql = "UPDATE clientes SET ativo = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente (logicamente)", e);
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));
        cliente.setDocumentoIdentidade(rs.getString("documento_identidade"));
        cliente.setDocumentoHabilitacao(rs.getString("documento_habilitacao"));
        
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            cliente.setDataCadastro(timestamp.toLocalDateTime());
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setComplemento(rs.getString("complemento"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setCep(rs.getString("cep"));
        cliente.setEndereco(endereco);
        cliente.setAtivo(rs.getBoolean("ativo"));
        
        return cliente;
    }
}
