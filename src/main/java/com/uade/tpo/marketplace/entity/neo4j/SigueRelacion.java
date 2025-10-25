package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import java.time.LocalDateTime;

@RelationshipProperties
public class SigueRelacion {

    @RelationshipId
    private Long id;

    private LocalDateTime fecha_inicio;

    @TargetNode
    private Usuario targetUsuario;

    // Constructor, Getters y Setters...
}