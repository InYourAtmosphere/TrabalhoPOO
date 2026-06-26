package org.poo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poo.model.Unidade;
import org.poo.model.dto.request.TransferenciaVeiculoDTO;
import org.poo.model.dto.request.UpdateVeiculoDTO;
import org.poo.model.veiculo.CarroPopular;
import org.poo.model.veiculo.Veiculo;
import org.poo.repository.UnidadeRepository;
import org.poo.repository.VeiculoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock private VeiculoRepository veiculoRepository;
    @Mock private UnidadeRepository unidadeRepository;

    @InjectMocks private VeiculoService service;

    private Unidade unidade(long id) {
        Unidade unidade = new Unidade();
        unidade.setId(id);
        return unidade;
    }

    @Test
    void registrar_falhaQuandoUnidadeNaoExiste() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar("carro", new HashMap<>(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unidade não encontrada");

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void registrar_falhaParaTipoInvalido() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade(1L)));

        assertThatThrownBy(() -> service.registrar("aviao", new HashMap<>(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de veículo inválido");
    }

    @Test
    void registrar_criaCarroEVinculaUnidade() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade(1L)));
        when(veiculoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> payload = new HashMap<>();
        payload.put("marca", "Fiat");
        payload.put("modelo", "Mobi");
        payload.put("quantidadePortas", 4);

        Veiculo salvo = service.registrar("carro", payload, 1L);

        assertThat(salvo).isInstanceOf(CarroPopular.class);
        assertThat(salvo.getMarca()).isEqualTo("Fiat");
        assertThat(salvo.getUnidade().getId()).isEqualTo(1L);
        assertThat(((CarroPopular) salvo).getQuantidadePortas()).isEqualTo(4);
    }

    @Test
    void atualizar_retornaVazioParaVeiculoInexistente() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.atualizar(99L, new UpdateVeiculoDTO())).isEmpty();
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void atualizar_aplicaSomenteCamposInformados() {
        CarroPopular carro = new CarroPopular();
        carro.setMarca("Fiat");
        carro.setModelo("Uno");
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(carro));
        when(veiculoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateVeiculoDTO dto = new UpdateVeiculoDTO();
        dto.setModelo("Argo");

        Optional<Veiculo> resultado = service.atualizar(1L, dto);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getMarca()).isEqualTo("Fiat");
        assertThat(resultado.get().getModelo()).isEqualTo("Argo");
    }

    @Test
    void transferir_retornaVazioParaVeiculoInexistente() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.transferir(99L, new TransferenciaVeiculoDTO(2L))).isEmpty();
    }

    @Test
    void transferir_falhaQuandoUnidadeDestinoNaoExiste() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(new CarroPopular()));
        when(unidadeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferir(1L, new TransferenciaVeiculoDTO(2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unidade de destino não encontrada");
    }

    @Test
    void transferir_moveVeiculoParaUnidadeDestino() {
        CarroPopular carro = new CarroPopular();
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(carro));
        when(unidadeRepository.findById(2L)).thenReturn(Optional.of(unidade(2L)));
        when(veiculoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Veiculo> resultado = service.transferir(1L, new TransferenciaVeiculoDTO(2L));

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUnidade().getId()).isEqualTo(2L);
    }

    @Test
    void deletar_removeVeiculoExistente() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(new CarroPopular()));

        assertThat(service.deletar(1L)).isTrue();
        verify(veiculoRepository).deleteById(1L);
    }

    @Test
    void deletar_naoRemoveVeiculoInexistente() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(service.deletar(1L)).isFalse();
        verify(veiculoRepository, never()).deleteById(any());
    }
}
