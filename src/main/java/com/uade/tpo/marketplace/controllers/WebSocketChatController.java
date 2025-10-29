package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.LiveService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;

/**
 * Controlador WebSocket para chat en vivo y eventos de streaming
 * 
 * Flujo de comunicación:
 * 1. Cliente se conecta a /ws
 * 2. Cliente se suscribe a /topic/live/{liveId}/chat
 * 3. Cliente envía mensajes a /app/live/{liveId}/chat
 * 4. Servidor broadcast el mensaje a todos los suscritos
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LiveService liveService;

    /**
     * Maneja mensajes de chat enviados por clientes
     * Los clientes envían a: /app/live/{liveId}/chat
     * El servidor broadcast a: /topic/live/{liveId}/chat
     */
    @MessageMapping("/live/{liveId}/chat")
    @SendTo("/topic/live/{liveId}/chat")
    public ChatMessage handleChatMessage(
            @DestinationVariable String liveId,
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.info("Mensaje de chat recibido para live {}: {} de {}", 
                liveId, message.getContent(), message.getSender());
        
        // Agregar timestamp
        message.setTimestamp(Instant.now().toString());
        
        // Guardar en Redis para persistencia temporal
        try {
            liveService.sendChatMessage(liveId, message.getSender(), message.getContent());
        } catch (Exception e) {
            log.error("Error al guardar mensaje en Redis: {}", e.getMessage());
        }
        
        return message;
    }

    /**
     * Maneja eventos de usuario uniéndose a la transmisión
     */
    @MessageMapping("/live/{liveId}/join")
    @SendTo("/topic/live/{liveId}/events")
    public LiveEvent handleUserJoin(
            @DestinationVariable String liveId,
            @Payload UserJoinMessage message) {
        
        log.info("Usuario {} se unió a live {}", message.getUserId(), liveId);
        
        // Registrar en Redis
        liveService.joinLive(liveId, message.getUserId());
        
        return LiveEvent.builder()
                .type("USER_JOINED")
                .liveId(liveId)
                .userId(message.getUserId())
                .userName(message.getUserName())
                .timestamp(Instant.now().toString())
                .build();
    }

    /**
     * Maneja eventos de usuario saliendo de la transmisión
     */
    @MessageMapping("/live/{liveId}/leave")
    @SendTo("/topic/live/{liveId}/events")
    public LiveEvent handleUserLeave(
            @DestinationVariable String liveId,
            @Payload UserLeaveMessage message) {
        
        log.info("Usuario {} salió de live {}", message.getUserId(), liveId);
        
        // Eliminar de Redis
        liveService.leaveLive(liveId, message.getUserId());
        
        return LiveEvent.builder()
                .type("USER_LEFT")
                .liveId(liveId)
                .userId(message.getUserId())
                .timestamp(Instant.now().toString())
                .build();
    }

    /**
     * Maneja preguntas al presentador
     */
    @MessageMapping("/live/{liveId}/question")
    @SendTo("/topic/live/{liveId}/questions")
    public QuestionMessage handleQuestion(
            @DestinationVariable String liveId,
            @Payload QuestionMessage message) {
        
        log.info("Pregunta recibida para live {}: {} de {}", 
                liveId, message.getQuestion(), message.getUserId());
        
        message.setTimestamp(Instant.now().toString());
        
        // Guardar en Redis
        liveService.sendQuestion(liveId, message.getUserId(), message.getQuestion());
        
        return message;
    }

    /**
     * Envía notificación de donación a todos los espectadores
     */
    public void sendDonationNotification(String liveId, String donorName, Double amount) {
        DonationEvent event = DonationEvent.builder()
                .type("DONATION")
                .liveId(liveId)
                .donorName(donorName)
                .amount(amount)
                .timestamp(Instant.now().toString())
                .build();
        
        messagingTemplate.convertAndSend("/topic/live/" + liveId + "/events", event);
    }

    // ========== DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String sender;
        private String content;
        private String timestamp;
        private String type; // "CHAT", "JOIN", "LEAVE"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJoinMessage {
        private String userId;
        private String userName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLeaveMessage {
        private String userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionMessage {
        private String userId;
        private String userName;
        private String question;
        private String timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    public static class LiveEvent {
        private String type; // "USER_JOINED", "USER_LEFT", "DONATION"
        private String liveId;
        private String userId;
        private String userName;
        private String timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    public static class DonationEvent {
        private String type;
        private String liveId;
        private String donorName;
        private Double amount;
        private String timestamp;
    }
}





