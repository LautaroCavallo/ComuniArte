package com.uade.tpo.marketplace.entity.neo4j;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node("Contenido") // Etiqueta en Neo4j
public class ContenidoNeo4j {

    @Id @GeneratedValue
    private Long id; // ID interno de Neo4j

    /** ID del documento en MongoDB. */
    @Property("contenidoId") // Cambiado para claridad
    @Indexed(unique = true) // Asegurar unicidad y búsquedas rápidas
    private String contenidoId;

    /** Título (opcional, para depuración) */
    @Property("titulo")
    private String titulo;

    // Constructor para facilitar la creación
    public ContenidoNeo4j(String contenidoId, String titulo) {
        this.contenidoId = contenidoId;
        this.titulo = titulo;
    }
}