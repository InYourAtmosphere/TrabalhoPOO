package org.poo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhatsappService implements NotificationService {

    @Override
    public void enviar(String telefone, String mensagem) {
        log.info("[WhatsApp] Para: {} | Mensagem: {}", telefone, mensagem);
    }
}
