package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.poo.model.veiculo.StatusVeiculo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusVeiculoDTO {
    private StatusVeiculo status;

    public void validate() {
        if (status == null) throw new IllegalArgumentException("Status é obrigatório");
        if (status == StatusVeiculo.LOCADO) {
            throw new IllegalArgumentException("Status LOCADO só pode ser definido através da criação de um contrato");
        }
    }
}
