package org.poo.controller;

import org.poo.model.Manutencao;
import org.poo.model.dto.request.ManutencaoDTO;
import org.poo.service.ManutencaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manutencoes")
public class ManutencaoController {

    private final ManutencaoService manutencaoService;

    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @GetMapping
    public List<Manutencao> listarManutencoes() {
        return manutencaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manutencao> buscarManutencaoPorId(@PathVariable Long id) {
        return manutencaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarManutencao(@RequestBody ManutencaoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(manutencaoService.registrar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarManutencao(@PathVariable Long id) {
        return manutencaoService.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
