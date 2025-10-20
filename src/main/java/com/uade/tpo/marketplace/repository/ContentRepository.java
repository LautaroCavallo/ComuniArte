package com.uade.tpo.marketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.uade.tpo.marketplace.entity.Content;

import java.util.List;

public interface ContentRepository extends MongoRepository<Content, String> {
    List<Content> findByCategory(String category);
    List<Content> findByCreatorId(String creatorId);
    List<Content> findByCategoryAndCreatorId(String category, String creatorId);
}
