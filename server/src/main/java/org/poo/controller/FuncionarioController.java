package org.poo.controller;

import org.poo.model.pessoa.Funcionario;
import org.poo.model.dto.request.FuncionarioDTO;
import org.poo.model.dto.request.UpdateFuncionarioDTO;
import org.poo.service.FuncionarioService;
import org.poo.util.AuthorizationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<Funcionario> listarFuncionarios(HttpServletRequest request) {
        if (AuthorizationUtils.isSupervisor(request)) {
            return funcionarioService.listarPorUnidade(AuthorizationUtils.unidadeIdDoLogado(request));
        }
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        return funcionarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioDTO dto, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem cadastrar funcionários.");
        }

        try {
            if (!AuthorizationUtils.podeGerenciarUnidade(request, dto.getUnidadeId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: supervisores só podem cadastrar funcionários da própria unidade.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioService.cadastrar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable Long id, @RequestBody UpdateFuncionarioDTO updates, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem atualizar funcionários.");
        }

        Optional<Funcionario> existente = funcionarioService.buscarPorId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long unidadeAtual = existente.get().getUnidade() != null ? existente.get().getUnidade().getId() : null;
        Long unidadeDestino = updates.getUnidadeId() != null ? updates.getUnidadeId() : unidadeAtual;
        if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeAtual) || !AuthorizationUtils.podeGerenciarUnidade(request, unidadeDestino)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: supervisores só podem gerenciar funcionários da própria unidade.");
        }

        return funcionarioService.atualizar(id, updates)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFuncionario(@PathVariable Long id, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem deletar funcionários.");
        }

        Optional<Funcionario> existente = funcionarioService.buscarPorId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long unidadeAtual = existente.get().getUnidade() != null ? existente.get().getUnidade().getId() : null;
        if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeAtual)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: supervisores só podem deletar funcionários da própria unidade.");
        }
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
