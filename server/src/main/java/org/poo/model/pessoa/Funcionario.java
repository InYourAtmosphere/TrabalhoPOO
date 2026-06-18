package org.poo.model.pessoa;

import org.poo.model.Unidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Funcionario extends Pessoa {
    private String matricula;
    private Cargo cargo;
    private String username;
    private String password;
    private Unidade unidade;
    private boolean ativo = true;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nome;
        private String telefone;
        private String email;
        private String matricula;
        private Cargo cargo;
        private String username;
        private String password;
        private Unidade unidade;
        private boolean ativo = true;

        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder telefone(String telefone) { this.telefone = telefone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder matricula(String matricula) { this.matricula = matricula; return this; }
        public Builder cargo(Cargo cargo) { this.cargo = cargo; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder unidade(Unidade unidade) { this.unidade = unidade; return this; }
        public Builder ativo(boolean ativo) { this.ativo = ativo; return this; }

        public Funcionario build() {
            Funcionario funcionario = new Funcionario();
            funcionario.setNome(nome);
            funcionario.setTelefone(telefone);
            funcionario.setEmail(email);
            funcionario.setMatricula(matricula);
            funcionario.setCargo(cargo);
            funcionario.setUsername(username);
            funcionario.setPassword(password);
            funcionario.setUnidade(unidade);
            funcionario.setAtivo(ativo);
            return funcionario;
        }
    }
}