package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.poo.model.Endereco;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClienteDTO {
    private String nome;
    private String telefone;
    private String email;
    private String documentoIdentidade;
    private String documentoHabilitacao;
    private Endereco endereco;

    public void validate() {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (documentoIdentidade == null || documentoIdentidade.isBlank()) {
            throw new IllegalArgumentException("Documento de identidade é obrigatório");
        }
    }
}
