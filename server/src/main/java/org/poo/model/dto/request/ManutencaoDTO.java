package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.poo.model.Manutencao.TipoManutencao;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManutencaoDTO {
    private Long veiculoId;
    private String descricao;
    private Double custo;
    private TipoManutencao tipo;

    public void validate() {
        if (veiculoId == null) throw new IllegalArgumentException("ID do veículo é obrigatório");
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("Descrição é obrigatória");
        if (custo == null || custo < 0) throw new IllegalArgumentException("Custo não pode ser negativo");
        if (tipo == null) throw new IllegalArgumentException("Tipo de manutenção é obrigatório");
    }
}
