package com.uade.tpo.marketplace.service;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LiveService {

    public Object liveChat(String liveId) {
        // TODO: implementar WebSocket / Pub-Sub
        return null;
    }

    public void donate(String liveId, Double amount) {
        // TODO: registrar donación
    }

    public void sendQuestion(String liveId, String question) {
        // TODO: enviar pregunta
    }

    public Object streamEvents(String liveId) {
        // TODO: implementar streaming de eventos en tiempo real
        return Collections.emptyList();
    }
}
