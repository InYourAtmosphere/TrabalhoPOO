package org.poo.controller;

import org.poo.model.Unidade;
import org.poo.model.dto.request.UnidadeDTO;
import org.poo.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    public UnidadeController() {
        this.unidadeRepository = new UnidadeRepository();
    }

    @GetMapping
    public List<Unidade> listarUnidades() {
        return unidadeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarUnidadePorId(@PathVariable Long id) {
        return unidadeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarUnidade(@RequestBody UnidadeDTO dto) {
        try {
            dto.validate();
            Unidade unidade = new Unidade();
            unidade.setNomeUnidade(dto.getNomeUnidade());
            unidade.setEndereco(dto.getEndereco());
            return ResponseEntity.status(HttpStatus.CREATED).body(unidadeRepository.save(unidade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUnidade(@PathVariable Long id) {
        if (unidadeRepository.findById(id).isPresent()) {
            unidadeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
