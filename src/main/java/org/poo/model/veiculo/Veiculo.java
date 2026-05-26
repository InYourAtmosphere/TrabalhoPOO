package org.poo.model.veiculo;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Veiculo {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private String chassi;
    private Double kmAtual;
    private StatusVeiculo status;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dataCadastro;

    @Setter(AccessLevel.NONE)
    private LocalDateTime atualizadoEm;

    public Veiculo() {
        this.dataCadastro = LocalDateTime.now();
        this.status = StatusVeiculo.DISPONIVEL;
    }
}
