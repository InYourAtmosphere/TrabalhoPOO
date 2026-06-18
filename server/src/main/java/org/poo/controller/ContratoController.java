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

    public ContratoController(ContratoRepository contratoRepository, ClienteRepository clienteRepository,
                               VeiculoRepository veiculoRepository, UnidadeRepository unidadeRepository) {
        this.contratoRepository = contratoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.unidadeRepository = unidadeRepository;
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
            Contrato.Builder contratoBuilder = Contrato.builder()
                    .dataFimPrevista(dto.getDataFimPrevista())
                    .valorDiaria(dto.getValorDiaria())
                    .kmInicial(dto.getKmInicial())
                    .formaPagamento(dto.getFormaPagamento());

            clienteRepository.findById(dto.getClienteId()).ifPresent(contratoBuilder::cliente);
            veiculoRepository.findById(dto.getVeiculoId()).ifPresent(contratoBuilder::veiculo);
            unidadeRepository.findById(dto.getUnidadeRetiradaId()).ifPresent(contratoBuilder::unidadeRetirada);

            Contrato contrato = contratoBuilder.build();

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
