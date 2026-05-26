package org.poo.model.pessoa;

import org.poo.model.Unidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Funcionario extends Pessoa {
    private String matricula;
    private String cargo;
    private Unidade unidade;
}