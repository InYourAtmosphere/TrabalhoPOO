package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaVeiculoDTO {
    private Long unidadeDestinoId;

    public void validate() {
        if (unidadeDestinoId == null) {
            throw new IllegalArgumentException("ID da unidade de destino é obrigatório");
        }
    }
}
