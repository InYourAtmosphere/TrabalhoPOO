package org.poo.controller;

import org.poo.model.pessoa.Funcionario;
import org.poo.model.veiculo.Veiculo;
import org.poo.model.dto.request.UpdateVeiculoDTO;
import org.poo.model.dto.request.UpdateStatusVeiculoDTO;
import org.poo.model.dto.request.TransferenciaVeiculoDTO;
import org.poo.service.VeiculoService;
import org.poo.util.AuthorizationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public ResponseEntity<?> listarVeiculos(
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false, defaultValue = "false") boolean todasUnidades,
            HttpServletRequest request) {
        Funcionario logado = (Funcionario) request.getAttribute("usuarioLogado");

        if (todasUnidades) {
            if (!AuthorizationUtils.isGerente(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: somente gerentes podem visualizar a frota de todas as unidades.");
            }
            return ResponseEntity.ok(veiculoService.listarTodos());
        }

        Long unidadeFiltro = unidadeId;
        if (unidadeFiltro == null && logado != null && logado.getUnidade() != null) {
            unidadeFiltro = logado.getUnidade().getId();
        }

        if (unidadeFiltro != null && !AuthorizationUtils.isGerente(request)
                && (logado == null || logado.getUnidade() == null || !logado.getUnidade().getId().equals(unidadeFiltro))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: você só pode visualizar a frota da sua própria unidade.");
        }

        List<Veiculo> veiculos = unidadeFiltro != null
                ? veiculoService.listarPorUnidade(unidadeFiltro)
                : veiculoService.listarTodos();
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> listarVeiculoPorId(@PathVariable Long id) {
        return veiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarVeiculo(@RequestParam String tipo, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem registrar veículos.");
        }

        Object unidadeIdRaw = payload.remove("unidadeId");
        if (unidadeIdRaw == null) {
            return ResponseEntity.badRequest().body("ID da unidade é obrigatório");
        }

        long unidadeId;
        try {
            unidadeId = ((Number) unidadeIdRaw).longValue();
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("ID da unidade inválido");
        }

        if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: supervisores só podem registrar veículos da própria unidade.");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(veiculoService.registrar(tipo, payload, unidadeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro ao processar dados do veículo: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarDadosVeiculo(@PathVariable Long id, @RequestBody UpdateVeiculoDTO updates, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem atualizar veículos.");
        }

        ResponseEntity<?> erroAcesso = verificarAcessoVeiculo(id, request);
        if (erroAcesso != null) return erroAcesso;

        try {
            return veiculoService.atualizar(id, updates)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusVeiculo(@PathVariable Long id, @RequestBody UpdateStatusVeiculoDTO dto, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem alterar o status do veículo.");
        }

        ResponseEntity<?> erroAcesso = verificarAcessoVeiculo(id, request);
        if (erroAcesso != null) return erroAcesso;

        try {
            return veiculoService.atualizarStatus(id, dto)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/transferir")
    public ResponseEntity<?> transferirVeiculo(@PathVariable Long id, @RequestBody TransferenciaVeiculoDTO dto, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerente(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes podem transferir veículos.");
        }

        try {
            return veiculoService.transferir(id, dto)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVeiculo(@PathVariable Long id, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerenteOuSupervisor(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes ou supervisores podem deletar veículos.");
        }

        ResponseEntity<?> erroAcesso = verificarAcessoVeiculo(id, request);
        if (erroAcesso != null) return erroAcesso;

        return veiculoService.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> verificarAcessoVeiculo(Long id, HttpServletRequest request) {
        Optional<Veiculo> veiculoOpt = veiculoService.buscarPorId(id);
        if (veiculoOpt.isEmpty()) {
            return null;
        }
        Long unidadeId = veiculoOpt.get().getUnidade() != null ? veiculoOpt.get().getUnidade().getId() : null;
        if (!AuthorizationUtils.podeGerenciarUnidade(request, unidadeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: supervisores só podem gerenciar veículos da própria unidade.");
        }
        return null;
    }
}
