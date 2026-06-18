package org.poo.model.dto.request;

import org.poo.model.pessoa.Cargo;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFuncionarioDTO {
    private String nome;
    private String telefone;
    private String email;
    private String matricula;
    private Cargo cargo;
    private String username;
    private String password;
    private Long unidadeId;

    public void validate() {
        // Validações opcionais para atualização
    }
}
