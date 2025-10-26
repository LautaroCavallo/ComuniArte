package com.uade.tpo.marketplace.controllers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket con STOMP para chat en vivo y eventos en tiempo real
 * 
 * Endpoints:
 * - /ws: Endpoint de conexión WebSocket
 * - /app: Prefijo para mensajes del cliente al servidor
 * - /topic: Prefijo para mensajes broadcast (muchos clientes)
 * - /queue: Prefijo para mensajes punto a punto
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar un broker simple en memoria para mensajes broadcast
        // /topic para mensajes públicos (chat, eventos)
        // /queue para mensajes privados (notificaciones personales)
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes que vienen del cliente
        // Ejemplo: cliente envía a "/app/chat" -> manejado por @MessageMapping("/chat")
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo para mensajes de usuario específico
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para que los clientes se conecten via WebSocket
        // URL: ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permitir todos los orígenes (ajustar en producción)
                .withSockJS(); // Fallback a SockJS para navegadores sin WebSocket
    }
}


