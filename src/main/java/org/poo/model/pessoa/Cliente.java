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
}
