package org.poo.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacaoPublisher {

    private final List<NotificacaoObserver> observers;

    public NotificacaoPublisher(List<NotificacaoObserver> observers) {
        this.observers = observers;
    }

    public void publicar(NotificacaoEvent evento) {
        observers.forEach(o -> o.onEvento(evento));
    }
}
