package org.poo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String estado,
            String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
        private String cep;

        public Builder logradouro(String logradouro) { this.logradouro = logradouro; return this; }
        public Builder numero(String numero) { this.numero = numero; return this; }
        public Builder complemento(String complemento) { this.complemento = complemento; return this; }
        public Builder bairro(String bairro) { this.bairro = bairro; return this; }
        public Builder cidade(String cidade) { this.cidade = cidade; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder cep(String cep) { this.cep = cep; return this; }

        public Endereco build() {
            return new Endereco(logradouro, numero, complemento, bairro, cidade, estado, cep);
        }
    }
}
