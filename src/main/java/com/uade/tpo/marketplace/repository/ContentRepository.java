package com.uade.tpo.marketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.uade.tpo.marketplace.entity.mongodb.Contenido;

import java.util.List;

public interface ContentRepository extends MongoRepository<Contenido, String> {
    List<Contenido> findByCategory(String category);
    List<Contenido> findByCreatorId(String creatorId);
    List<Contenido> findByCategoryAndCreatorId(String category, String creatorId);
}
