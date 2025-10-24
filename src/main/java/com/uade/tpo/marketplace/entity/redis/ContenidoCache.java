package com.uade.tpo.marketplace.entity.redis;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
public class ContenidoCache implements Serializable {

    // Se recomienda usar el ID de MongoDB como clave de la entidad
    private String id; 
    private String titulo;
    private String tipo;
    private String urlArchivo;
    private String creadorId;
    
    // Las propiedades deben ser serializables
    private Map<String, Object> metadatosEnriquecidos; 
    private List<String> etiquetas;
    private LocalDateTime fechaPublicacion;
}