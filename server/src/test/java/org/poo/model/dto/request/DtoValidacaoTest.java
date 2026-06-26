package org.poo.model.dto.request;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.poo.model.veiculo.StatusVeiculo;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DtoValidacaoTest {

    @Nested
    class ContratoDtoValidate {

        private ContratoDTO valido() {
            return new ContratoDTO(1L, 2L, 3L,
                    LocalDateTime.now().plusDays(5), 100.0, 0.0, "PIX");
        }

        @Test
        void aceitaContratoValido() {
            assertThatCode(() -> valido().validate()).doesNotThrowAnyException();
        }

        @Test
        void rejeitaClienteNulo() {
            ContratoDTO dto = valido();
            dto.setClienteId(null);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cliente");
        }

        @Test
        void rejeitaVeiculoNulo() {
            ContratoDTO dto = valido();
            dto.setVeiculoId(null);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("veículo");
        }

        @Test
        void rejeitaDataFimNoPassado() {
            ContratoDTO dto = valido();
            dto.setDataFimPrevista(LocalDateTime.now().minusDays(1));

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Data de fim");
        }

        @Test
        void rejeitaValorDiariaZeroOuNegativo() {
            ContratoDTO dto = valido();
            dto.setValorDiaria(0.0);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("diária");
        }

        @Test
        void rejeitaKmInicialNegativo() {
            ContratoDTO dto = valido();
            dto.setKmInicial(-1.0);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quilometragem");
        }
    }

    @Nested
    class EncerrarContratoDtoValidate {

        @Test
        void aceitaKmFinalNulo() {
            assertThatCode(() -> new EncerrarContratoDTO(null, null).validate())
                    .doesNotThrowAnyException();
        }

        @Test
        void rejeitaKmFinalNegativo() {
            assertThatThrownBy(() -> new EncerrarContratoDTO(-5.0, null).validate())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quilometragem final");
        }
    }

    @Nested
    class UpdateVeiculoDtoValidate {

        @Test
        void aceitaCamposNulos() {
            assertThatCode(() -> new UpdateVeiculoDTO().validate()).doesNotThrowAnyException();
        }

        @Test
        void rejeitaMarcaEmBranco() {
            UpdateVeiculoDTO dto = new UpdateVeiculoDTO();
            dto.setMarca("  ");

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Marca");
        }

        @Test
        void rejeitaAnoAnteriorA1900() {
            UpdateVeiculoDTO dto = new UpdateVeiculoDTO();
            dto.setAno(1899);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ano");
        }

        @Test
        void rejeitaKmNegativo() {
            UpdateVeiculoDTO dto = new UpdateVeiculoDTO();
            dto.setKmAtual(-1.0);

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quilometragem");
        }
    }

    @Nested
    class UpdateStatusVeiculoDtoValidate {

        @Test
        void aceitaStatusDisponivel() {
            assertThatCode(() -> new UpdateStatusVeiculoDTO(StatusVeiculo.DISPONIVEL).validate())
                    .doesNotThrowAnyException();
        }

        @Test
        void rejeitaStatusNulo() {
            assertThatThrownBy(() -> new UpdateStatusVeiculoDTO(null).validate())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Status é obrigatório");
        }

        @Test
        void rejeitaStatusLocadoManual() {
            assertThatThrownBy(() -> new UpdateStatusVeiculoDTO(StatusVeiculo.LOCADO).validate())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("LOCADO");
        }
    }

    @Nested
    class TransferenciaVeiculoDtoValidate {

        @Test
        void aceitaUnidadeDestinoPreenchida() {
            assertThatCode(() -> new TransferenciaVeiculoDTO(10L).validate())
                    .doesNotThrowAnyException();
        }

        @Test
        void rejeitaUnidadeDestinoNula() {
            assertThatThrownBy(() -> new TransferenciaVeiculoDTO(null).validate())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unidade de destino");
        }
    }

    @Nested
    class CreateClienteDtoValidate {

        @Test
        void aceitaClienteValido() {
            CreateClienteDTO dto = new CreateClienteDTO();
            dto.setNome("João");
            dto.setDocumentoIdentidade("123.456.789-00");

            assertThatCode(dto::validate).doesNotThrowAnyException();
        }

        @Test
        void rejeitaNomeEmBranco() {
            CreateClienteDTO dto = new CreateClienteDTO();
            dto.setNome("   ");
            dto.setDocumentoIdentidade("123");

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nome");
        }

        @Test
        void rejeitaDocumentoIdentidadeAusente() {
            CreateClienteDTO dto = new CreateClienteDTO();
            dto.setNome("Maria");

            assertThatThrownBy(dto::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Documento de identidade");
        }
    }
}
