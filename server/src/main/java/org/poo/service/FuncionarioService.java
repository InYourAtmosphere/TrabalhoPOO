package org.poo.service;

import org.poo.model.dto.request.FuncionarioDTO;
import org.poo.model.dto.request.UpdateFuncionarioDTO;
import org.poo.model.pessoa.Funcionario;
import org.poo.repository.FuncionarioRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.util.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UnidadeRepository unidadeRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository,
                              UnidadeRepository unidadeRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public List<Funcionario> listarPorUnidade(Long unidadeId) {
        return funcionarioRepository.findAll().stream()
                .filter(f -> f.getUnidade() != null && f.getUnidade().getId().equals(unidadeId))
                .toList();
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    public Funcionario cadastrar(FuncionarioDTO dto) {
        dto.validate();
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
        return funcionarioRepository.save(funcionarioBuilder.build());
    }

    public Optional<Funcionario> atualizar(Long id, UpdateFuncionarioDTO updates) {
        return funcionarioRepository.findById(id).map(funcionario -> {
            if (updates.getNome() != null) funcionario.setNome(updates.getNome());
            if (updates.getTelefone() != null) funcionario.setTelefone(updates.getTelefone());
            if (updates.getEmail() != null) funcionario.setEmail(updates.getEmail());
            if (updates.getMatricula() != null) funcionario.setMatricula(updates.getMatricula());
            if (updates.getCargo() != null) funcionario.setCargo(updates.getCargo());
            if (updates.getUsername() != null) funcionario.setUsername(updates.getUsername());
            if (updates.getPassword() != null) {
                funcionario.setPassword(PasswordUtils.hashPassword(updates.getPassword()));
            }
            if (updates.getUnidadeId() != null) {
                unidadeRepository.findById(updates.getUnidadeId()).ifPresent(funcionario::setUnidade);
            }
            return funcionarioRepository.save(funcionario);
        });
    }

    public boolean deletar(Long id) {
        if (funcionarioRepository.findById(id).isEmpty()) {
            return false;
        }
        funcionarioRepository.deleteById(id);
        return true;
    }
}
