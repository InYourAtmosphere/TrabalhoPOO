package org.poo.model;

import java.time.LocalDateTime;

public class Manutencao {
    private long id;
    private String tipo;
    private float descricao;
    private LocalDateTime dataAgendamento;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Float getDescricao() {
        return descricao;
    }

    public void setDescricao(Float dadosDepreciacao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }
}
