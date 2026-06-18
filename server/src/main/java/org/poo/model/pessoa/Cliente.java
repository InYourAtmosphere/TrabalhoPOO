package org.poo.model.pessoa;

import org.poo.model.Endereco;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cliente extends Pessoa {
    private String documentoIdentidade;
    private String documentoHabilitacao;
    private Endereco endereco;
    private boolean ativo = true;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nome;
        private String telefone;
        private String email;
        private String documentoIdentidade;
        private String documentoHabilitacao;
        private Endereco endereco;
        private boolean ativo = true;

        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder telefone(String telefone) { this.telefone = telefone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder documentoIdentidade(String documentoIdentidade) { this.documentoIdentidade = documentoIdentidade; return this; }
        public Builder documentoHabilitacao(String documentoHabilitacao) { this.documentoHabilitacao = documentoHabilitacao; return this; }
        public Builder endereco(Endereco endereco) { this.endereco = endereco; return this; }
        public Builder ativo(boolean ativo) { this.ativo = ativo; return this; }

        public Cliente build() {
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setTelefone(telefone);
            cliente.setEmail(email);
            cliente.setDocumentoIdentidade(documentoIdentidade);
            cliente.setDocumentoHabilitacao(documentoHabilitacao);
            cliente.setEndereco(endereco);
            cliente.setAtivo(ativo);
            return cliente;
        }
    }
}
