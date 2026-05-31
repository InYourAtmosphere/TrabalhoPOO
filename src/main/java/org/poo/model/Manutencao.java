package org.poo.model;

import java.time.LocalDateTime;

import org.poo.model.veiculo.Veiculo;

import lombok.Getter;
import lombok.Setter;

import lombok.AccessLevel;

@Getter
@Setter
public class Manutencao {
    private Long id;

    private Veiculo veiculo;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;
    private String descricao;
    private Double custo;
    private TipoManutencao tipo;

    public enum TipoManutencao {
        PREVENTIVA, CORRETIVA, PREDITIVA
    }

    public Manutencao() {
        this.dataInicio = LocalDateTime.now();
    }
}
