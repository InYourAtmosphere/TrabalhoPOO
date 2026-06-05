package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVeiculoDTO {
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private Double kmAtual;
    private Integer quantidadePortas;
    private Boolean temArCondicionado;
    private Integer cilindrada;
    private Boolean temBau;

    public void validate() {
        if (marca != null && marca.isBlank()) throw new IllegalArgumentException("Marca não pode ser vazia");
        if (modelo != null && modelo.isBlank()) throw new IllegalArgumentException("Modelo não pode ser vazio");
        if (ano != null && ano < 1900) throw new IllegalArgumentException("Ano inválido");
        if (kmAtual != null && kmAtual < 0) throw new IllegalArgumentException("Quilometragem não pode ser negativa");
    }
}
