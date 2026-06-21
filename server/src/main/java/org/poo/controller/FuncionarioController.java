package org.poo.controller;

import org.poo.model.pessoa.Funcionario;
import org.poo.model.dto.request.FuncionarioDTO;
import org.poo.model.dto.request.UpdateFuncionarioDTO;
import org.poo.repository.FuncionarioRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.util.AuthorizationUtils;
import org.poo.util.PasswordUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final UnidadeRepository unidadeRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository, UnidadeRepository unidadeRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.unidadeRepository = unidadeRepository;
    }

    @GetMapping
    public List<Funcionario> listarFuncionarios(HttpServletRequest request) {
        if (AuthorizationUtils.isSupervisor(request)) {
            Long unidadeId = AuthorizationUtils.unidadeIdDoLogado(request);
            return funcionarioRepository.findAll().stream()
                    .filter(f -> f.getUnidade() != null && f.getUnidade().getId().equals(unidadeId))
                    .toList();
        }
        return funcionarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        return funcionarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioDTO dto, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem cadastrar funcionários.");
        }

        try {
            dto.validate();
            if (!AuthorizationUtils.podeGerenciarUnidade(request, dto.getUnidadeId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: supervisores só podem cadastrar funcionários da própria unidade.");
            }
            Funcionario.Builder funcionarioBuilder = Funcionario.builder()
                    .nome(dto.getNome())
                    .telefone(dto.getTelefone())
                    .email(dto.getEmail())
                    .matricula(dto.getMatricula())
                    .cargo(dto.getCargo())
                    .username(dto.getUsername());

            if (dto.getPassword() != null) {
                funcionarioBuilder.password(PasswordUtils.hashPassword(dto.getPassword()));
            }

            unidadeRepository.findById(dto.getUnidadeId()).ifPresent(funcionarioBuilder::unidade);

            Funcionario funcionario = funcionarioBuilder.build();

            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioRepository.save(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable Long id, @RequestBody UpdateFuncionarioDTO updates, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem atualizar funcionários.");
        }

        Optional<Funcionario> existente = funcionarioRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long unidadeAtual = existente.get().getUnidade() != null ? existente.get().getUnidade().getId() : null;
        Long unidadeDestino = updates.getUnidadeId() != null ? updates.getUnidadeId() : unidadeAtual;
        if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeAtual) || !AuthorizationUtils.podeGerenciarUnidade(request, unidadeDestino)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: supervisores só podem gerenciar funcionários da própria unidade.");
        }

        return funcionarioRepository.findById(id).map(funcionario -> {
            if (updates.getNome() != null) funcionario.setNome(updates.getNome());
            if (updates.getTelefone() != null) funcionario.setTelefone(updates.getTelefone());
            if (updates.getEmail() != null) funcionario.setEmail(updates.getEmail());
            if (updates.getMatricula() != null) funcionario.setMatricula(updates.getMatricula());
            if (updates.getCargo() != null) funcionario.setCargo(updates.getCargo());
            if (updates.getUsername() != null) funcionario.setUsername(updates.getUsername());
            if (updates.getPassword() != null) funcionario.setPassword(PasswordUtils.hashPassword(updates.getPassword()));
            if (updates.getUnidadeId() != null) {
                unidadeRepository.findById(updates.getUnidadeId()).ifPresent(funcionario::setUnidade);
            }

            return ResponseEntity.ok(funcionarioRepository.save(funcionario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFuncionario(@PathVariable Long id, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem deletar funcionários.");
        }

        Optional<Funcionario> existente = funcionarioRepository.findById(id);
        if (existente.isPresent()) {
            Long unidadeAtual = existente.get().getUnidade() != null ? existente.get().getUnidade().getId() : null;
            if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeAtual)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: supervisores só podem deletar funcionários da própria unidade.");
            }
            funcionarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
