package org.poo.model.pessoa;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Pessoa {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String nome;
    private String telefone;
    private String email;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dataCadastro;

    public Pessoa() {
        this.dataCadastro = LocalDateTime.now();
    }
}
