package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Artist;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ArtistRepository extends Neo4jRepository<Artist, Long> {
}
