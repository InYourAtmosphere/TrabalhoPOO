package org.poo.service;

import org.poo.model.Contrato;
import org.poo.model.Contrato.StatusContrato;
import org.poo.model.dto.request.ContratoDTO;
import org.poo.model.dto.request.EncerrarContratoDTO;
import org.poo.model.veiculo.StatusVeiculo;
import org.poo.repository.ClienteRepository;
import org.poo.repository.ContratoRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UnidadeRepository unidadeRepository;

    public ContratoService(ContratoRepository contratoRepository,
                           ClienteRepository clienteRepository,
                           VeiculoRepository veiculoRepository,
                           UnidadeRepository unidadeRepository) {
        this.contratoRepository = contratoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<Contrato> listarTodos() {
        return contratoRepository.findAll();
    }

    public Optional<Contrato> buscarPorId(Long id) {
        return contratoRepository.findById(id);
    }

    public Contrato registrar(ContratoDTO dto) {
        dto.validate();
        Contrato.Builder contratoBuilder = Contrato.builder()
                .dataFimPrevista(dto.getDataFimPrevista())
                .valorDiaria(dto.getValorDiaria())
                .kmInicial(dto.getKmInicial())
                .formaPagamento(dto.getFormaPagamento());

        clienteRepository.findById(dto.getClienteId()).ifPresent(contratoBuilder::cliente);
        veiculoRepository.findById(dto.getVeiculoId()).ifPresent(contratoBuilder::veiculo);
        unidadeRepository.findById(dto.getUnidadeRetiradaId()).ifPresent(contratoBuilder::unidadeRetirada);

        Contrato salvo = contratoRepository.save(contratoBuilder.build());

        veiculoRepository.findById(dto.getVeiculoId())
                .ifPresent(v -> veiculoRepository.updateStatus(v.getId(), StatusVeiculo.LOCADO));

        return salvo;
    }

    public Optional<Contrato> encerrar(Long id, EncerrarContratoDTO dto) {
        dto.validate();
        return contratoRepository.findById(id).map(contrato -> {
            if (contrato.getStatus() != StatusContrato.ATIVO) {
                throw new IllegalArgumentException("Contrato já encerrado.");
            }

            LocalDateTime agora = LocalDateTime.now();
            long dias = ChronoUnit.DAYS.between(contrato.getDataInicio(), agora);
            contrato.setDataFimReal(agora);
            contrato.setStatus(StatusContrato.FINALIZADO);
            contrato.setValorTotal(contrato.getValorDiaria() * Math.max(dias, 1));

            if (dto.getKmFinal() != null) contrato.setKmFinal(dto.getKmFinal());

            if (dto.getUnidadeDevolucaoId() != null) {
                unidadeRepository.findById(dto.getUnidadeDevolucaoId())
                        .ifPresent(contrato::setUnidadeDevolucao);
            }

            Contrato encerrado = contratoRepository.save(contrato);
            veiculoRepository.updateStatus(contrato.getVeiculo().getId(), StatusVeiculo.DISPONIVEL);
            return encerrado;
        });
    }

    public boolean deletar(Long id) {
        if (contratoRepository.findById(id).isEmpty()) {
            return false;
        }
        contratoRepository.deleteById(id);
        return true;
    }
}
