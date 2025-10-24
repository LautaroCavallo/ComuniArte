package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Contenido")
public class Contenido {
    
    @Id @GeneratedValue
    private Long neo4jId;

    @Property("contenido_id") // ID de MongoDB
    private String contenidoId;
    
    // Propiedades adicionales...
    private String titulo;

    // Getters y Setters...
}