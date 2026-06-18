package org.poo.model;

import java.time.LocalDateTime;

import org.poo.model.pessoa.Cliente;
import org.poo.model.veiculo.Veiculo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contrato {
    private Long id;

    private Cliente cliente;
    private Veiculo veiculo;
    private Unidade unidadeRetirada;
    private Unidade unidadeDevolucao;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFimPrevista;

    private LocalDateTime dataFimReal;

    private Double valorDiaria;
    private Double valorTotal;
    private StatusContrato status;

    private Double kmInicial;
    private Double kmFinal;
    private String formaPagamento;

    public enum StatusContrato {
        ATIVO, FINALIZADO, CANCELADO
    }

    public Contrato() {
        this.dataInicio = LocalDateTime.now();
        this.status = StatusContrato.ATIVO;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Cliente cliente;
        private Veiculo veiculo;
        private Unidade unidadeRetirada;
        private Unidade unidadeDevolucao;
        private LocalDateTime dataInicio;
        private boolean dataInicioDefinida = false;
        private LocalDateTime dataFimPrevista;
        private Double valorDiaria;
        private StatusContrato status;
        private boolean statusDefinido = false;
        private Double kmInicial;
        private String formaPagamento;

        public Builder cliente(Cliente cliente) { this.cliente = cliente; return this; }
        public Builder veiculo(Veiculo veiculo) { this.veiculo = veiculo; return this; }
        public Builder unidadeRetirada(Unidade unidadeRetirada) { this.unidadeRetirada = unidadeRetirada; return this; }
        public Builder unidadeDevolucao(Unidade unidadeDevolucao) { this.unidadeDevolucao = unidadeDevolucao; return this; }
        public Builder dataFimPrevista(LocalDateTime dataFimPrevista) { this.dataFimPrevista = dataFimPrevista; return this; }
        public Builder valorDiaria(Double valorDiaria) { this.valorDiaria = valorDiaria; return this; }
        public Builder kmInicial(Double kmInicial) { this.kmInicial = kmInicial; return this; }
        public Builder formaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; return this; }

        public Builder dataInicio(LocalDateTime dataInicio) {
            this.dataInicio = dataInicio;
            this.dataInicioDefinida = true;
            return this;
        }

        public Builder status(StatusContrato status) {
            this.status = status;
            this.statusDefinido = true;
            return this;
        }

        public Contrato build() {
            Contrato contrato = new Contrato();
            contrato.setCliente(cliente);
            contrato.setVeiculo(veiculo);
            contrato.setUnidadeRetirada(unidadeRetirada);
            contrato.setUnidadeDevolucao(unidadeDevolucao);
            contrato.setDataFimPrevista(dataFimPrevista);
            contrato.setValorDiaria(valorDiaria);
            contrato.setKmInicial(kmInicial);
            contrato.setFormaPagamento(formaPagamento);

            if (dataInicioDefinida) {
                contrato.setDataInicio(dataInicio);
            }
            if (statusDefinido) {
                contrato.setStatus(status);
            }

            return contrato;
        }
    }
}
