package com.uade.tpo.marketplace.repository.neo4j;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import com.uade.tpo.marketplace.entity.neo4j.Colectivo;

public interface ColectivoNeo4jRepository extends Neo4jRepository<Colectivo, Long> {
    
    @Query("MATCH (c:Colectivo {nombre: $nombre})<-[:ES_MIEMBRO_DE]-(miembro:Usuario) RETURN miembro")
    List<Object> findMiembros(@Param("nombre") String nombre);
    
    @Query("MATCH (c:Colectivo {nombre: $nombre}) RETURN c")
    Colectivo findByNombre(@Param("nombre") String nombre);
}
