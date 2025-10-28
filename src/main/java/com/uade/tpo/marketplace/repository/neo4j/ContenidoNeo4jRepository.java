package com.uade.tpo.marketplace.repository.neo4j;

import com.uade.tpo.marketplace.entity.neo4j.ContenidoNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContenidoNeo4jRepository extends Neo4jRepository<ContenidoNeo4j, Long> {

    /**
     * Busca un nodo Contenido por su ID de MongoDB.
     * @param contenidoId El ID del documento Contenido en MongoDB.
     * @return Un Optional que contiene el nodo si se encuentra.
     */
    Optional<ContenidoNeo4j> findByContenidoId(String contenidoId);

    /**
     * Elimina un nodo Contenido por su ID de MongoDB.
     * @param contenidoId El ID del documento Contenido en MongoDB.
     */
    @Query("MATCH (c:Contenido {contenidoId: $contenidoId}) DETACH DELETE c")
    void deleteByContenidoId(@Param("contenidoId") String contenidoId);
}
