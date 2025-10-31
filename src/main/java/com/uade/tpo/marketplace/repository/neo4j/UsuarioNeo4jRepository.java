package com.uade.tpo.marketplace.repository.neo4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import com.uade.tpo.marketplace.entity.neo4j.UsuarioNeo4j;

public interface UsuarioNeo4jRepository extends Neo4jRepository<UsuarioNeo4j, Long> {
    
    @Query("MATCH (u:Usuario {mongoUserId: $userId})-[:SIGUE]->(seguido:Usuario) RETURN seguido")
    List<UsuarioNeo4j> findSeguidos(@Param("userId") String userId);
    
    @Query("MATCH (u:Usuario {mongoUserId: $userId})<-[:SIGUE]-(seguidor:Usuario) RETURN seguidor")
    List<UsuarioNeo4j> findSeguidores(@Param("userId") String userId);
    
    @Query("MATCH (u:Usuario {mongoUserId: $userId})-[:VIO]->(c:Contenido) RETURN c")
    List<Object> findContenidoVisto(@Param("userId") String userId);
    
    @Query("MATCH (u:Usuario {mongoUserId: $userId})-[:GUSTA]->(c:Contenido) RETURN c")
    List<Object> findContenidoGustado(@Param("userId") String userId);
    
    // Recomendación de contenidos basada en lo que les gusta a las personas que sigo
    @Query("MATCH (yo:Usuario {mongoUserId: $userId})-[:SIGUE]->(seguido:Usuario)-[:GUSTA]->(c:Contenido) " +
           "WHERE NOT (yo)-[:GUSTA]->(c) " +
           "WITH c, count(*) AS popularidad " +
           "RETURN c.contenidoId AS contentId, c.titulo AS titulo, popularidad " +
           "ORDER BY popularidad DESC " +
           "LIMIT 10")
    List<Map<String, Object>> findRecommendedContent(@Param("userId") String userId);

    Optional<UsuarioNeo4j> findByMongoUserId(String mongoUserId);
}
