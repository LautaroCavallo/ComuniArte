package com.uade.tpo.marketplace.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad para almacenar logs del sistema
 * Permite auditoría, debugging y monitoreo de todas las operaciones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "logs")
public class LogEntry {

    @Id
    private String id;
    
    @Indexed
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Indexed
    private String level; // INFO, WARN, ERROR, DEBUG
    
    @Indexed
    private String service; // LiveService, AnalyticsService, LikeService, etc.
    
    @Indexed
    private String source; // MONGODB, NEO4J, REDIS, WEBSOCKET, APPLICATION
    
    private String method; // Método que generó el log
    
    private String action; // LIKE_CREATED, TRANSMISION_INICIADA, REDIS_ERROR, etc.
    
    private String message; // Mensaje descriptivo
    
    private String userId; // Usuario relacionado (opcional)
    
    private String resourceId; // ID del recurso afectado (contenido, transmisión, etc.)
    
    private Map<String, Object> metadata; // Datos adicionales (latencia, parámetros, etc.)
    
    private String stackTrace; // Para errores
    
    private Long duration; // Duración en ms (para operaciones)
    
    @Indexed
    @Builder.Default
    private String environment = "development"; // development, production
    
    private String ipAddress; // IP del cliente (para requests HTTP)
    
    private String userAgent; // User agent (para requests HTTP)
}

