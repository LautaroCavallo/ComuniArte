package com.uade.tpo.marketplace.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una lista personalizada de contenidos creada por un usuario
 * Ejemplo: "Mis favoritos", "Ver más tarde", "Para compartir", etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "listas_personalizadas")
public class ListaPersonalizada {

    @Id
    private String id;
    
    private String usuarioId; // ID del usuario propietario de la lista
    private String nombre; // Nombre de la lista
    private String descripcion; // Descripción opcional
    
    @Builder.Default
    private List<String> contenidosIds = new ArrayList<>(); // IDs de contenidos en la lista
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    private boolean esPublica; // Si otros usuarios pueden ver esta lista
    
    @Builder.Default
    private int ordenamiento = 0; // Para ordenar listas del usuario
}

