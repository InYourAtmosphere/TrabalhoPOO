package org.poo.controller;

import org.poo.model.pessoa.Cliente;
import org.poo.model.dto.request.UpdateClienteDTO;
import org.poo.model.dto.request.CreateClienteDTO;
import org.poo.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody CreateClienteDTO dto) {
        try {
            dto.validate();
            Cliente cliente = new Cliente();
            cliente.setNome(dto.getNome());
            cliente.setTelefone(dto.getTelefone());
            cliente.setEmail(dto.getEmail());
            cliente.setDocumentoIdentidade(dto.getDocumentoIdentidade());
            cliente.setDocumentoHabilitacao(dto.getDocumentoHabilitacao());
            cliente.setEndereco(dto.getEndereco());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteRepository.save(cliente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody UpdateClienteDTO updates) {
        try {
            updates.validate();
            return clienteRepository.findById(id).map(cliente -> {
                if (updates.getNome() != null) cliente.setNome(updates.getNome());
                if (updates.getDocumentoIdentidade() != null) cliente.setDocumentoIdentidade(updates.getDocumentoIdentidade());
                if (updates.getDocumentoHabilitacao() != null) cliente.setDocumentoHabilitacao(updates.getDocumentoHabilitacao());
                if (updates.getTelefone() != null) cliente.setTelefone(updates.getTelefone());
                
                return ResponseEntity.ok(clienteRepository.save(cliente));
            }).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        if (clienteRepository.findById(id).isPresent()) {
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
