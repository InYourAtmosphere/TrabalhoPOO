package org.poo.service;

public record NotificacaoEvent(String telefone, String mensagem, TipoEvento tipo) {

    public enum TipoEvento {
        CONTRATO_ABERTO,
        CONTRATO_ENCERRADO,
        MANUTENCAO_AGENDADA
    }
}
