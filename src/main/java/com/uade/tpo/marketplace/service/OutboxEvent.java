package com.uade.tpo.marketplace.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Entidad que representa un evento pendiente en la colección "outbox_events" de MongoDB.
 */
@Document(collection = "outbox_events")
@Data
@Builder
public class OutboxEvent {

    @Id
    private String id;
    
    // Tipo de evento, ej: "USER_REGISTERED", "USER_DELETED"
    private String eventType; 
    
    // Datos necesarios para procesar el evento (ej: userId, nombre)
    private Map<String, Object> payload;
    
    private Instant createdAt;
    
    // Estado del procesamiento
    @Builder.Default
    private boolean processed = false;
    
    @Builder.Default
    private int retryCount = 0; // Para controlar reintentos
    
    private Instant processedAt;
    private String lastError;

}
