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
public class UnidadeDTO {
    private String nomeUnidade;
    private Endereco endereco;

    public void validate() {
        if (nomeUnidade == null || nomeUnidade.isBlank()) {
            throw new IllegalArgumentException("Nome da unidade é obrigatório");
        }
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
    }
}
