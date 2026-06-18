package org.poo.model.veiculo;

import org.poo.model.Unidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Motocicleta extends Veiculo {
    private int cilindrada;
    private boolean temBau;

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
        private int cilindrada;
        private boolean temBau;

        public Builder marca(String marca) { this.marca = marca; return this; }
        public Builder modelo(String modelo) { this.modelo = modelo; return this; }
        public Builder ano(Integer ano) { this.ano = ano; return this; }
        public Builder placa(String placa) { this.placa = placa; return this; }
        public Builder chassi(String chassi) { this.chassi = chassi; return this; }
        public Builder kmAtual(Double kmAtual) { this.kmAtual = kmAtual; return this; }
        public Builder unidade(Unidade unidade) { this.unidade = unidade; return this; }
        public Builder cilindrada(int cilindrada) { this.cilindrada = cilindrada; return this; }
        public Builder temBau(boolean temBau) { this.temBau = temBau; return this; }

        public Motocicleta build() {
            Motocicleta motocicleta = new Motocicleta();
            motocicleta.setMarca(marca);
            motocicleta.setModelo(modelo);
            motocicleta.setAno(ano);
            motocicleta.setPlaca(placa);
            motocicleta.setChassi(chassi);
            motocicleta.setKmAtual(kmAtual);
            motocicleta.setUnidade(unidade);
            motocicleta.setCilindrada(cilindrada);
            motocicleta.setTemBau(temBau);
            return motocicleta;
        }
    }
}
