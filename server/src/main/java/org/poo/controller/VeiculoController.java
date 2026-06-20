package org.poo.controller;

import org.poo.model.Unidade;
import org.poo.model.veiculo.Veiculo;
import org.poo.model.veiculo.CarroPopular;
import org.poo.model.veiculo.Motocicleta;
import org.poo.model.dto.request.UpdateVeiculoDTO;
import org.poo.model.dto.request.UpdateStatusVeiculoDTO;
import org.poo.model.dto.request.TransferenciaVeiculoDTO;
import org.poo.repository.VeiculoRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.util.AuthorizationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoRepository veiculoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ObjectMapper objectMapper;

    public VeiculoController(VeiculoRepository veiculoRepository, UnidadeRepository unidadeRepository) {
        this.veiculoRepository = veiculoRepository;
        this.unidadeRepository = unidadeRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @GetMapping
    public List<Veiculo> listarVeiculos() {
        return veiculoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> listarVeiculoPorId(@PathVariable Long id) {
        return veiculoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarVeiculo(@RequestParam String tipo, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerente(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes podem registrar veículos.");
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

        Optional<Unidade> unidadeOpt = unidadeRepository.findById(unidadeId);
        if (unidadeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Unidade não encontrada");
        }

        try {
            Veiculo veiculo;
            if ("carro".equalsIgnoreCase(tipo)) {
                veiculo = objectMapper.convertValue(payload, CarroPopular.class);
            } else if ("moto".equalsIgnoreCase(tipo)) {
                veiculo = objectMapper.convertValue(payload, Motocicleta.class);
            } else {
                return ResponseEntity.badRequest().body("Tipo de veículo inválido");
            }
            veiculo.setUnidade(unidadeOpt.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(veiculoRepository.save(veiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro ao processar dados do veículo: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarDadosVeiculo(@PathVariable Long id, @RequestBody UpdateVeiculoDTO updates, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerente(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes podem atualizar veículos.");
        }

        try {
            updates.validate();
            return veiculoRepository.findById(id).map(veiculo -> {
                if (updates.getMarca() != null) veiculo.setMarca(updates.getMarca());
                if (updates.getModelo() != null) veiculo.setModelo(updates.getModelo());
                if (updates.getAno() != null) veiculo.setAno(updates.getAno());
                if (updates.getPlaca() != null) veiculo.setPlaca(updates.getPlaca());
                if (updates.getKmAtual() != null) veiculo.setKmAtual(updates.getKmAtual());
                
                if (veiculo instanceof CarroPopular carro) {
                    if (updates.getQuantidadePortas() != null) carro.setQuantidadePortas(updates.getQuantidadePortas());
                    if (updates.getTemArCondicionado() != null) carro.setTemArCondicionado(updates.getTemArCondicionado());
                } else if (veiculo instanceof Motocicleta moto) {
                    if (updates.getCilindrada() != null) moto.setCilindrada(updates.getCilindrada());
                    if (updates.getTemBau() != null) moto.setTemBau(updates.getTemBau());
                }
                
                return (ResponseEntity<?>) ResponseEntity.ok(veiculoRepository.save(veiculo));
            }).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusVeiculo(@PathVariable Long id, @RequestBody UpdateStatusVeiculoDTO dto, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerente(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes podem alterar o status do veículo.");
        }

        try {
            dto.validate();
            if (veiculoRepository.findById(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            veiculoRepository.updateStatus(id, dto.getStatus());
            return ResponseEntity.ok(veiculoRepository.findById(id).get());
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
            dto.validate();
            Optional<Veiculo> veiculoOpt = veiculoRepository.findById(id);
            if (veiculoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Unidade> unidadeOpt = unidadeRepository.findById(dto.getUnidadeDestinoId());
            if (unidadeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Unidade de destino não encontrada");
            }

            Veiculo veiculo = veiculoOpt.get();
            veiculo.setUnidade(unidadeOpt.get());
            
            return ResponseEntity.ok(veiculoRepository.save(veiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVeiculo(@PathVariable Long id, HttpServletRequest request) {
        if (!AuthorizationUtils.isGerente(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente gerentes podem deletar veículos.");
        }

        if (veiculoRepository.findById(id).isPresent()) {
            veiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
