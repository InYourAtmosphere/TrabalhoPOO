package org.poo.controller;

import org.poo.model.pessoa.Funcionario;
import org.poo.model.dto.request.FuncionarioDTO;
import org.poo.repository.FuncionarioRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.util.PasswordUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final UnidadeRepository unidadeRepository;

    public FuncionarioController() {
        this.unidadeRepository = new UnidadeRepository();
        this.funcionarioRepository = new FuncionarioRepository(unidadeRepository);
    }

    @GetMapping
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        return funcionarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Funcionario> cadastrarFuncionario(@RequestBody FuncionarioDTO dto) {
        dto.validate();
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setEmail(dto.getEmail());
        funcionario.setMatricula(dto.getMatricula());
        funcionario.setCargo(dto.getCargo());
        funcionario.setUsername(dto.getUsername());

        if (dto.getPassword() != null) {
            funcionario.setPassword(PasswordUtils.hashPassword(dto.getPassword()));
        }

        unidadeRepository.findById(dto.getUnidadeId()).ifPresent(funcionario::setUnidade);

        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioRepository.save(funcionario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        if (funcionarioRepository.findById(id).isPresent()) {
            funcionarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
