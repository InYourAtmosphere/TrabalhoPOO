package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.poo.model.pessoa.Cargo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    private String nome;
    private String telefone;
    private String email;
    private String matricula;
    private Cargo cargo;
    private String username;
    private String password;
    private Long unidadeId;

    public void validate() {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (matricula == null || matricula.isBlank()) throw new IllegalArgumentException("Matrícula é obrigatória");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username é obrigatório");
        if (unidadeId == null) throw new IllegalArgumentException("ID da unidade é obrigatório");
    }
}
