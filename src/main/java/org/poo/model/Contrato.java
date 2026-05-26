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
    @Setter(AccessLevel.NONE)
    private Long id;

    private Cliente cliente;
    private Veiculo veiculo;
    private Unidade unidadeRetirada;
    private Unidade unidadeDevolucao;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFimPrevista;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dataFimReal;

    private Double valorDiaria;
    private Double valorTotal;
    private StatusContrato status;

    public enum StatusContrato {
        ATIVO, FINALIZADO, CANCELADO
    }

    public Contrato() {
        this.dataInicio = LocalDateTime.now();
        this.status = StatusContrato.ATIVO;
    }
}
