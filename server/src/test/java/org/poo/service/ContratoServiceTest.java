package org.poo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poo.model.Contrato;
import org.poo.model.Contrato.StatusContrato;
import org.poo.model.dto.request.EncerrarContratoDTO;
import org.poo.model.veiculo.CarroPopular;
import org.poo.model.veiculo.StatusVeiculo;
import org.poo.model.veiculo.Veiculo;
import org.poo.repository.ClienteRepository;
import org.poo.repository.ContratoRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock private ContratoRepository contratoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private UnidadeRepository unidadeRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private ContratoService service;

    private Contrato contratoAtivo(LocalDateTime dataInicio, double valorDiaria) {
        Veiculo veiculo = new CarroPopular();
        veiculo.setId(42L);
        return Contrato.builder()
                .veiculo(veiculo)
                .valorDiaria(valorDiaria)
                .dataInicio(dataInicio)
                .build();
    }

    @Test
    void encerrar_calculaValorTotalPorDiasDecorridos() {
        Contrato contrato = contratoAtivo(LocalDateTime.now().minusDays(3), 100.0);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Contrato> resultado = service.encerrar(1L, new EncerrarContratoDTO(null, null));

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getValorTotal()).isEqualTo(300.0);
        assertThat(resultado.get().getStatus()).isEqualTo(StatusContrato.FINALIZADO);
        assertThat(resultado.get().getDataFimReal()).isNotNull();
    }

    @Test
    void encerrar_cobraNoMinimoUmaDiariaNoMesmoDia() {
        Contrato contrato = contratoAtivo(LocalDateTime.now(), 80.0);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Contrato> resultado = service.encerrar(1L, new EncerrarContratoDTO(null, null));

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getValorTotal()).isEqualTo(80.0);
    }

    @Test
    void encerrar_liberaVeiculoComoDisponivel() {
        Contrato contrato = contratoAtivo(LocalDateTime.now().minusDays(1), 50.0);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.encerrar(1L, new EncerrarContratoDTO(null, null));

        verify(veiculoRepository).updateStatus(42L, StatusVeiculo.DISPONIVEL);
    }

    @Test
    void encerrar_gravaKmFinalQuandoInformado() {
        Contrato contrato = contratoAtivo(LocalDateTime.now().minusDays(2), 50.0);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Contrato> resultado = service.encerrar(1L, new EncerrarContratoDTO(15000.0, null));

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getKmFinal()).isEqualTo(15000.0);
    }

    @Test
    void encerrar_rejeitaContratoJaEncerrado() {
        Contrato contrato = contratoAtivo(LocalDateTime.now().minusDays(1), 50.0);
        contrato.setStatus(StatusContrato.FINALIZADO);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));

        assertThatThrownBy(() -> service.encerrar(1L, new EncerrarContratoDTO(null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já encerrado");

        verify(contratoRepository, never()).save(any());
    }

    @Test
    void encerrar_retornaVazioParaContratoInexistente() {
        when(contratoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.encerrar(99L, new EncerrarContratoDTO(null, null))).isEmpty();
        verify(contratoRepository, never()).save(any());
    }

    @Test
    void deletar_removeContratoExistente() {
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(new Contrato()));

        assertThat(service.deletar(1L)).isTrue();
        verify(contratoRepository).deleteById(1L);
    }

    @Test
    void deletar_naoRemoveContratoInexistente() {
        when(contratoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(service.deletar(1L)).isFalse();
        verify(contratoRepository, never()).deleteById(any());
    }
}
