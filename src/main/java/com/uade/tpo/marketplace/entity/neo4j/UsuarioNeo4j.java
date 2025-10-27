package com.uade.tpo.marketplace.entity.neo4j;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.neo4j.core.schema.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Usuario")
public class UsuarioNeo4j {

    @Id @GeneratedValue // ID interno de Neo4j
    private Long neo4jId;

    // ID de MongoDB para sincronización y consultas externas
    @Property("mongoUserId")
    @Indexed(unique = true) // Importante para búsquedas rápidas
    private String mongoUserId;
    
    // Propiedades adicionales del nodo...
    private String nombre;

    // Relaciones: Mapeamos los campos a las clases de relación
    @Relationship(type = "SIGUE", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<SigueRelacion> seguidos = new HashSet<>();

    @Relationship(type = "CREO", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<Contenido> contenidoCreado = new HashSet<>();

    @Relationship(type = "VIO", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<VioRelacion> contenidosVistos = new HashSet<>();

    @Relationship(type = "GUSTA", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<Contenido> gustos = new HashSet<>(); // No tiene propiedades, mapeo directo

    @Relationship(type = "COLABORA_EN", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<ColaboraRelacion> colaboraciones = new HashSet<>();

    @Relationship(type = "ES_MIEMBRO_DE", direction = Direction.OUTGOING)
    @Builder.Default
    private Set<EsMiembroRelacion> membresias = new HashSet<>();

    
    public UsuarioNeo4j(String mongoUserId, String nombre) {
        this.mongoUserId = mongoUserId;
        this.nombre = nombre;
    }
}