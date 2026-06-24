package org.poo.service;

import org.poo.model.Manutencao;
import org.poo.model.dto.request.ManutencaoDTO;
import org.poo.model.veiculo.StatusVeiculo;
import org.poo.repository.ManutencaoRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final VeiculoRepository veiculoRepository;

    public ManutencaoService(ManutencaoRepository manutencaoRepository,
                             VeiculoRepository veiculoRepository) {
        this.manutencaoRepository = manutencaoRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public List<Manutencao> listarTodas() {
        return manutencaoRepository.findAll();
    }

    public Optional<Manutencao> buscarPorId(Long id) {
        return manutencaoRepository.findById(id);
    }

    public Manutencao registrar(ManutencaoDTO dto) {
        dto.validate();
        Manutencao.Builder manutencaoBuilder = Manutencao.builder()
                .descricao(dto.getDescricao())
                .custo(dto.getCusto())
                .tipo(dto.getTipo());

        veiculoRepository.findById(dto.getVeiculoId()).ifPresent(manutencaoBuilder::veiculo);

        Manutencao salva = manutencaoRepository.save(manutencaoBuilder.build());

        veiculoRepository.findById(dto.getVeiculoId())
                .ifPresent(v -> veiculoRepository.updateStatus(v.getId(), StatusVeiculo.EM_MANUTENCAO));

        return salva;
    }

    public boolean deletar(Long id) {
        if (manutencaoRepository.findById(id).isEmpty()) {
            return false;
        }
        manutencaoRepository.deleteById(id);
        return true;
    }
}
