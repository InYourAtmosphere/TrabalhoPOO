package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    private String nome;
    private String telefone;
    private String email;
    private String matricula;
    private String cargo;
    private Long unidadeId;

    public void validate() {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (matricula == null || matricula.isBlank()) throw new IllegalArgumentException("Matrícula é obrigatória");
        if (unidadeId == null) throw new IllegalArgumentException("ID da unidade é obrigatório");
    }
}
