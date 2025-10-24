package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Colectivo")
public class Colectivo {
    
    @Id @GeneratedValue
    private Long neo4jId;

    private String nombre;
    
    // Getters y Setters...
}