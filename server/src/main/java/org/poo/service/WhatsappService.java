package org.poo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhatsappService implements NotificationService, NotificacaoObserver {

    @Override
    public void enviar(String telefone, String mensagem) {
        log.info("Enviando notificação para %s", telefone);
    }

    @Override
    public void onEvento(NotificacaoEvent evento) {
        enviar(evento.telefone(), evento.mensagem());
    }
}
