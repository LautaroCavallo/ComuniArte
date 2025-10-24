package com.uade.tpo.marketplace.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.uade.tpo.marketplace.entity.mongodb.Artista;

public interface ArtistRepository extends Neo4jRepository<Artista, String> {
}
