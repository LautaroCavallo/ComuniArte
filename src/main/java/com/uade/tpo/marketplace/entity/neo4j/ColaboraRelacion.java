package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class ColaboraRelacion {

    @RelationshipId
    private Long id;

    private String rol;

    @TargetNode
    private Contenido targetContenido;

    // Constructor, Getters y Setters...
}