package org.poo.model.veiculo;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private String chassi;
    private Double kmAtual;
    private StatusVeiculo status;

    private LocalDateTime dataCadastro;

    private LocalDateTime atualizadoEm;

    private boolean ativo = true;

    public Veiculo() {
        this.dataCadastro = LocalDateTime.now();
        this.status = StatusVeiculo.DISPONIVEL;
    }
}
