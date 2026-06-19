package org.poo.controller;

import org.poo.model.Manutencao;
import org.poo.model.dto.request.ManutencaoDTO;
import org.poo.model.veiculo.StatusVeiculo;
import org.poo.repository.ManutencaoRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manutencoes")
public class ManutencaoController {

    private final ManutencaoRepository manutencaoRepository;
    private final VeiculoRepository veiculoRepository;

    public ManutencaoController(ManutencaoRepository manutencaoRepository, VeiculoRepository veiculoRepository) {
        this.manutencaoRepository = manutencaoRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @GetMapping
    public List<Manutencao> listarManutencoes() {
        return manutencaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manutencao> buscarManutencaoPorId(@PathVariable Long id) {
        return manutencaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarManutencao(@RequestBody ManutencaoDTO dto) {
        try {
            dto.validate();
            Manutencao.Builder manutencaoBuilder = Manutencao.builder()
                    .descricao(dto.getDescricao())
                    .custo(dto.getCusto())
                    .tipo(dto.getTipo());

            veiculoRepository.findById(dto.getVeiculoId()).ifPresent(manutencaoBuilder::veiculo);

            Manutencao manutencao = manutencaoBuilder.build();
            Manutencao salva = manutencaoRepository.save(manutencao);

            veiculoRepository.findById(dto.getVeiculoId())
                    .ifPresent(v -> veiculoRepository.updateStatus(v.getId(), StatusVeiculo.EM_MANUTENCAO));

            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarManutencao(@PathVariable Long id) {
        if (manutencaoRepository.findById(id).isPresent()) {
            manutencaoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
