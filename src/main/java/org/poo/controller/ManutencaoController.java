package org.poo.controller;

import org.poo.model.Manutencao;
import org.poo.model.dto.request.ManutencaoDTO;
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

    public ManutencaoController() {
        this.veiculoRepository = new VeiculoRepository();
        this.manutencaoRepository = new ManutencaoRepository(veiculoRepository);
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
            Manutencao manutencao = new Manutencao();
            veiculoRepository.findById(dto.getVeiculoId()).ifPresent(manutencao::setVeiculo);
            manutencao.setDescricao(dto.getDescricao());
            manutencao.setCusto(dto.getCusto());
            manutencao.setTipo(dto.getTipo());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(manutencaoRepository.save(manutencao));
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
