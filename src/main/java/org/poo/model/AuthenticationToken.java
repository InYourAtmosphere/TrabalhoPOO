package org.poo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.model.pessoa.Funcionario;

import java.util.UUID;

@Getter
@Setter
public class AuthenticationToken {

    private UUID token;
    @JsonIgnore
    private Funcionario funcionario;
    private long expiraEm;

}
