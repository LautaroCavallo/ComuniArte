package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import java.time.LocalDateTime;

@RelationshipProperties
public class EsMiembroRelacion {

    @RelationshipId
    private Long id;

    private LocalDateTime fecha_ingreso;

    @TargetNode
    private Colectivo targetColectivo;

    // Constructor, Getters y Setters...
}