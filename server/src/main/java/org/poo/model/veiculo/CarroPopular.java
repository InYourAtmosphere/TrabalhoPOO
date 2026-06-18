package org.poo.model.veiculo;

import org.poo.model.Unidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarroPopular extends Veiculo {
    private int quantidadePortas;
    private boolean temArCondicionado;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String marca;
        private String modelo;
        private Integer ano;
        private String placa;
        private String chassi;
        private Double kmAtual;
        private Unidade unidade;
        private int quantidadePortas;
        private boolean temArCondicionado;

        public Builder marca(String marca) { this.marca = marca; return this; }
        public Builder modelo(String modelo) { this.modelo = modelo; return this; }
        public Builder ano(Integer ano) { this.ano = ano; return this; }
        public Builder placa(String placa) { this.placa = placa; return this; }
        public Builder chassi(String chassi) { this.chassi = chassi; return this; }
        public Builder kmAtual(Double kmAtual) { this.kmAtual = kmAtual; return this; }
        public Builder unidade(Unidade unidade) { this.unidade = unidade; return this; }
        public Builder quantidadePortas(int quantidadePortas) { this.quantidadePortas = quantidadePortas; return this; }
        public Builder temArCondicionado(boolean temArCondicionado) { this.temArCondicionado = temArCondicionado; return this; }

        public CarroPopular build() {
            CarroPopular carroPopular = new CarroPopular();
            carroPopular.setMarca(marca);
            carroPopular.setModelo(modelo);
            carroPopular.setAno(ano);
            carroPopular.setPlaca(placa);
            carroPopular.setChassi(chassi);
            carroPopular.setKmAtual(kmAtual);
            carroPopular.setUnidade(unidade);
            carroPopular.setQuantidadePortas(quantidadePortas);
            carroPopular.setTemArCondicionado(temArCondicionado);
            return carroPopular;
        }
    }
}
