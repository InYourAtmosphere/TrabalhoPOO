package org.poo.model;

import java.util.ArrayList;
import java.util.List;

import org.poo.model.veiculo.Veiculo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Unidade {
    @Setter(AccessLevel.NONE)
    private Long id;

    private String nomeUnidade;
    private Endereco endereco;
    private List<Veiculo> veiculos;

    public Unidade() {
        this.veiculos = new ArrayList<>();
    }
}
