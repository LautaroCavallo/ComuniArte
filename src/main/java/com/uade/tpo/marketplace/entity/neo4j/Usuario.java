package com.uade.tpo.marketplace.entity.neo4j;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.schema.*;
import java.util.HashSet;
import java.util.Set;

@Node("Usuario")
public class Usuario {

    @Id @GeneratedValue // ID interno de Neo4j
    private Long neo4jId;

    // ID de MongoDB para sincronización y consultas externas
    @Property("user_id") 
    private String userId; 
    
    // Propiedades adicionales del nodo...
    private String nombre;

    // Relaciones: Mapeamos los campos a las clases de relación
    @Relationship(type = "SIGUE", direction = Direction.OUTGOING)
    private Set<SigueRelacion> seguidos = new HashSet<>();

    @Relationship(type = "CREO", direction = Direction.OUTGOING)
    private Set<Contenido> contenidoCreado = new HashSet<>();

    @Relationship(type = "VIO", direction = Direction.OUTGOING)
    private Set<VioRelacion> contenidosVistos = new HashSet<>();

    @Relationship(type = "GUSTA", direction = Direction.OUTGOING)
    private Set<Contenido> gustos = new HashSet<>(); // No tiene propiedades, mapeo directo

    @Relationship(type = "COLABORA_EN", direction = Direction.OUTGOING)
    private Set<ColaboraRelacion> colaboraciones = new HashSet<>();

    @Relationship(type = "ES_MIEMBRO_DE", direction = Direction.OUTGOING)
    private Set<EsMiembroRelacion> membresias = new HashSet<>();

    // Getters y Setters... (Omitidos por brevedad)
}