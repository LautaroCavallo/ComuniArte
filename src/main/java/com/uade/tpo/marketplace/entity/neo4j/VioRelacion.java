package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import java.time.LocalDateTime;

@RelationshipProperties
public class VioRelacion {

    @RelationshipId
    private Long id;

    private Long duracion_ms;
    private LocalDateTime fecha_ultima_vista;

    @TargetNode
    private ContenidoNeo4j targetContenido;

    // Constructor, Getters y Setters...
}