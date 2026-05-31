package org.poo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
