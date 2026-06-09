package org.poo.controller;

import org.poo.model.Contrato;
import org.poo.model.dto.request.ContratoDTO;
import org.poo.repository.ContratoRepository;
import org.poo.repository.ClienteRepository;
import org.poo.repository.VeiculoRepository;
import org.poo.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoRepository contratoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UnidadeRepository unidadeRepository;

    public ContratoController() {
        this.clienteRepository = new ClienteRepository();
        this.unidadeRepository = new UnidadeRepository();
        this.veiculoRepository = new VeiculoRepository(unidadeRepository);
        this.contratoRepository = new ContratoRepository(clienteRepository, veiculoRepository, unidadeRepository);
    }

    @GetMapping
    public List<Contrato> listarContratos() {
        return contratoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrato> buscarContratoPorId(@PathVariable Long id) {
        return contratoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarContrato(@RequestBody ContratoDTO dto) {
        try {
            dto.validate();
            Contrato contrato = new Contrato();
            clienteRepository.findById(dto.getClienteId()).ifPresent(contrato::setCliente);
            veiculoRepository.findById(dto.getVeiculoId()).ifPresent(contrato::setVeiculo);
            unidadeRepository.findById(dto.getUnidadeRetiradaId()).ifPresent(contrato::setUnidadeRetirada);
            contrato.setDataFimPrevista(dto.getDataFimPrevista());
            contrato.setValorDiaria(dto.getValorDiaria());
            contrato.setKmInicial(dto.getKmInicial());
            contrato.setFormaPagamento(dto.getFormaPagamento());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(contratoRepository.save(contrato));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarContrato(@PathVariable Long id) {
        if (contratoRepository.findById(id).isPresent()) {
            contratoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
