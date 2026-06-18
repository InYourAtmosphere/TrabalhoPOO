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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Veiculo veiculo;
        private String descricao;
        private Double custo;
        private TipoManutencao tipo;
        private LocalDateTime dataFim;

        public Builder veiculo(Veiculo veiculo) { this.veiculo = veiculo; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder custo(Double custo) { this.custo = custo; return this; }
        public Builder tipo(TipoManutencao tipo) { this.tipo = tipo; return this; }
        public Builder dataFim(LocalDateTime dataFim) { this.dataFim = dataFim; return this; }

        public Manutencao build() {
            Manutencao manutencao = new Manutencao();
            manutencao.setVeiculo(veiculo);
            manutencao.setDescricao(descricao);
            manutencao.setCusto(custo);
            manutencao.setTipo(tipo);
            manutencao.setDataFim(dataFim);
            return manutencao;
        }
    }
}
