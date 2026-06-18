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
}
