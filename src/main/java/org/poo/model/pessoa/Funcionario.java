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
}