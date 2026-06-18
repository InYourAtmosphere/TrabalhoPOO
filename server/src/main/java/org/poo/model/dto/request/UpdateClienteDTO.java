package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClienteDTO {
    private String nome;
    private String documentoIdentidade;
    private String documentoHabilitacao;
    private String telefone;

    public void validate() {
        if (nome != null && nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (telefone != null && telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        // Adicionar outras validações conforme necessário
    }
}
