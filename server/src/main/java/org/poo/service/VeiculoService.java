package org.poo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.model.Unidade;
import org.poo.model.dto.request.TransferenciaVeiculoDTO;
import org.poo.model.dto.request.UpdateStatusVeiculoDTO;
import org.poo.model.dto.request.UpdateVeiculoDTO;
import org.poo.model.veiculo.CarroPopular;
import org.poo.model.veiculo.Motocicleta;
import org.poo.model.veiculo.Veiculo;
import org.poo.repository.UnidadeRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ObjectMapper objectMapper;

    public VeiculoService(VeiculoRepository veiculoRepository, UnidadeRepository unidadeRepository) {
        this.veiculoRepository = veiculoRepository;
        this.unidadeRepository = unidadeRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    public List<Veiculo> listarPorUnidade(Long unidadeId) {
        return veiculoRepository.findByUnidade(unidadeId);
    }

    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    public Veiculo registrar(String tipo, Map<String, Object> payload, long unidadeId) {
        Optional<Unidade> unidadeOpt = unidadeRepository.findById(unidadeId);
        if (unidadeOpt.isEmpty()) {
            throw new IllegalArgumentException("Unidade não encontrada");
        }

        Veiculo veiculo;
        if ("carro".equalsIgnoreCase(tipo)) {
            veiculo = objectMapper.convertValue(payload, CarroPopular.class);
        } else if ("moto".equalsIgnoreCase(tipo)) {
            veiculo = objectMapper.convertValue(payload, Motocicleta.class);
        } else {
            throw new IllegalArgumentException("Tipo de veículo inválido");
        }

        veiculo.setUnidade(unidadeOpt.get());
        return veiculoRepository.save(veiculo);
    }

    public Optional<Veiculo> atualizar(Long id, UpdateVeiculoDTO updates) {
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

            return veiculoRepository.save(veiculo);
        });
    }

    public Optional<Veiculo> atualizarStatus(Long id, UpdateStatusVeiculoDTO dto) {
        dto.validate();
        if (veiculoRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        veiculoRepository.updateStatus(id, dto.getStatus());
        return veiculoRepository.findById(id);
    }

    public Optional<Veiculo> transferir(Long id, TransferenciaVeiculoDTO dto) {
        dto.validate();
        Optional<Veiculo> veiculoOpt = veiculoRepository.findById(id);
        if (veiculoOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Unidade> unidadeOpt = unidadeRepository.findById(dto.getUnidadeDestinoId());
        if (unidadeOpt.isEmpty()) {
            throw new IllegalArgumentException("Unidade de destino não encontrada");
        }

        Veiculo veiculo = veiculoOpt.get();
        veiculo.setUnidade(unidadeOpt.get());
        return Optional.of(veiculoRepository.save(veiculo));
    }

    public boolean deletar(Long id) {
        if (veiculoRepository.findById(id).isEmpty()) {
            return false;
        }
        veiculoRepository.deleteById(id);
        return true;
    }
}
