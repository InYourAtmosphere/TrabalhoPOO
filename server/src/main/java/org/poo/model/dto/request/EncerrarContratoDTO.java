package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncerrarContratoDTO {
    private Double kmFinal;
    private Long unidadeDevolucaoId;

    public void validate() {
        if (kmFinal != null && kmFinal < 0) {
            throw new IllegalArgumentException("Quilometragem final não pode ser negativa");
        }
    }
}
