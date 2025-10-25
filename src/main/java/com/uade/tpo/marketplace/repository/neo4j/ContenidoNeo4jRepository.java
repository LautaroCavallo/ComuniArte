package com.uade.tpo.marketplace.repository.neo4j;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import com.uade.tpo.marketplace.entity.neo4j.Contenido;

public interface ContenidoNeo4jRepository extends Neo4jRepository<Contenido, Long> {
    
    @Query("MATCH (c:Contenido {contenidoId: $contenidoId})<-[:CREO]-(creador:Usuario) RETURN creador")
    List<Object> findCreador(@Param("contenidoId") String contenidoId);
    
    @Query("MATCH (c:Contenido {contenidoId: $contenidoId})<-[:VIO]-(usuario:Usuario) RETURN usuario")
    List<Object> findUsuariosQueVieron(@Param("contenidoId") String contenidoId);
    
    @Query("MATCH (c:Contenido {contenidoId: $contenidoId})<-[:GUSTA]-(usuario:Usuario) RETURN usuario")
    List<Object> findUsuariosQueGustaron(@Param("contenidoId") String contenidoId);
    
    @Query("MATCH (c:Contenido {contenidoId: $contenidoId})<-[:COLABORA_EN]-(colaborador:Usuario) RETURN colaborador")
    List<Object> findColaboradores(@Param("contenidoId") String contenidoId);
}
