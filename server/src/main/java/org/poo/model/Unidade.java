package org.poo.model;

import java.util.ArrayList;
import java.util.List;

import org.poo.model.veiculo.Veiculo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Unidade {
    private Long id;

    private String nomeUnidade;
    private Endereco endereco;
    private List<Veiculo> veiculos;

    public Unidade() {
        this.veiculos = new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nomeUnidade;
        private Endereco endereco;

        public Builder nomeUnidade(String nomeUnidade) { this.nomeUnidade = nomeUnidade; return this; }
        public Builder endereco(Endereco endereco) { this.endereco = endereco; return this; }

        public Unidade build() {
            Unidade unidade = new Unidade();
            unidade.setNomeUnidade(nomeUnidade);
            unidade.setEndereco(endereco);
            return unidade;
        }
    }
}
