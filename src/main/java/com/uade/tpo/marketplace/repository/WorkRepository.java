package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Work;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface WorkRepository extends Neo4jRepository<Work, Long> {
}
