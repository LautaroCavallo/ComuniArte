package com.uade.tpo.marketplace.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Representa una transmisión en vivo (live streaming)
 * Almacena información sobre transmisiones activas y finalizadas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transmisiones")
public class Transmision {

    @Id
    private String id; // ID de la transmisión (ej: "live-123")
    
    private String creadorId; // ID del usuario que transmite
    private String titulo; // Título de la transmisión
    private String descripcion; // Descripción
    
    private String categoria; // Categoría del contenido
    private List<String> etiquetas; // Etiquetas/tags
    
    private String estado; // ACTIVA, FINALIZADA, PAUSADA
    
    private LocalDateTime fechaInicio; // Cuándo inició
    private LocalDateTime fechaFin; // Cuándo finalizó
    
    private String urlStreaming; // URL del stream (si se usa servicio externo)
    private String urlGrabacion; // URL de la grabación guardada
    
    // Metadatos adicionales
    private Map<String, Object> metadatos; // Idioma, región, calidad, etc.
    
    // Estadísticas
    private Integer espectadoresMax; // Máximo de espectadores simultáneos
    private Integer totalDonaciones; // Total de donaciones recibidas
    private Double montoTotalDonaciones; // Monto total recaudado
    private Integer totalPreguntas; // Número de preguntas recibidas
    private Integer totalMensajes; // Mensajes de chat
    
    // Referencia al contenido creado (si se guarda como contenido)
    private String contenidoId; // ID del Contenido resultante
}

