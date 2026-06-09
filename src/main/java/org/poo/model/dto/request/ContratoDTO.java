package org.poo.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDTO {
    private Long clienteId;
    private Long veiculoId;
    private Long unidadeRetiradaId;
    private LocalDateTime dataFimPrevista;
    private Double valorDiaria;
    private Double kmInicial;
    private String formaPagamento;

    public void validate() {
        if (clienteId == null) throw new IllegalArgumentException("ID do cliente é obrigatório");
        if (veiculoId == null) throw new IllegalArgumentException("ID do veículo é obrigatório");
        if (unidadeRetiradaId == null) throw new IllegalArgumentException("ID da unidade de retirada é obrigatório");
        if (dataFimPrevista == null || dataFimPrevista.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de fim prevista inválida");
        }
        if (valorDiaria == null || valorDiaria <= 0) {
            throw new IllegalArgumentException("Valor da diária deve ser maior que zero");
        }
    }
}
