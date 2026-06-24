package org.poo.service;

import org.poo.model.dto.request.CreateClienteDTO;
import org.poo.model.dto.request.UpdateClienteDTO;
import org.poo.model.pessoa.Cliente;
import org.poo.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente cadastrar(CreateClienteDTO dto) {
        dto.validate();
        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .documentoIdentidade(dto.getDocumentoIdentidade())
                .documentoHabilitacao(dto.getDocumentoHabilitacao())
                .endereco(dto.getEndereco())
                .build();
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> atualizar(Long id, UpdateClienteDTO updates) {
        updates.validate();
        return clienteRepository.findById(id).map(cliente -> {
            if (updates.getNome() != null) cliente.setNome(updates.getNome());
            if (updates.getDocumentoIdentidade() != null) cliente.setDocumentoIdentidade(updates.getDocumentoIdentidade());
            if (updates.getDocumentoHabilitacao() != null) cliente.setDocumentoHabilitacao(updates.getDocumentoHabilitacao());
            if (updates.getTelefone() != null) cliente.setTelefone(updates.getTelefone());
            return clienteRepository.save(cliente);
        });
    }

    public boolean deletar(Long id) {
        if (clienteRepository.findById(id).isEmpty()) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }
}
