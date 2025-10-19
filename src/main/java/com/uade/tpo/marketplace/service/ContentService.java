package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Content;
import com.uade.tpo.marketplace.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    private static final String VIEW_COUNT_KEY_PREFIX = "content:viewcount:";

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Content saveContent(Content content) {
        return contentRepository.save(content);
    }

    public Optional<Content> getContentById(String id) {
        return contentRepository.findById(id);
    }

    public List<Content> findContents(String category, String creatorId) {
        if (category != null && creatorId != null) {
            return contentRepository.findByCategoryAndCreatorId(category, creatorId);
        } else if (category != null) {
            return contentRepository.findByCategory(category);
        } else if (creatorId != null) {
            return contentRepository.findByCreatorId(creatorId);
        } else {
            return contentRepository.findAll();
        }
    }

    public void incrementViewCount(String contentId) {
        String key = VIEW_COUNT_KEY_PREFIX + contentId;
        // redisTemplate.opsForValue().increment(key);
    }
}
