package com.uade.tpo.marketplace.entity.mongodb;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contenidos")
public class Contenido {

    @Id
    private String id;

    private String titulo;
    private String tipo; // video, audio, texto, live
    private String urlArchivo;
    private String creadorId; // Referencia al Artista/Usuario creador
    
    // Mapa flexible para metadatos
    private Map<String, Object> metadatosEnriquecidos; 
    
    private String categoria;
    private List<String> etiquetas;
    private LocalDateTime fechaPublicacion;
}