package org.poo.service;

import org.poo.model.Unidade;
import org.poo.model.dto.request.UnidadeDTO;
import org.poo.repository.UnidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    public Optional<Unidade> buscarPorId(Long id) {
        return unidadeRepository.findById(id);
    }

    public Unidade cadastrar(UnidadeDTO dto) {
        dto.validate();
        Unidade unidade = Unidade.builder()
                .nomeUnidade(dto.getNomeUnidade())
                .endereco(dto.getEndereco())
                .build();
        return unidadeRepository.save(unidade);
    }

    public boolean deletar(Long id) {
        if (unidadeRepository.findById(id).isEmpty()) {
            return false;
        }
        unidadeRepository.deleteById(id);
        return true;
    }
}
