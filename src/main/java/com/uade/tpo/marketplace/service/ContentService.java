package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Content;
import com.uade.tpo.marketplace.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private static final String VIEW_COUNT_KEY_PREFIX = "content:viewcount:";

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    // Crear o actualizar contenido
    public Content saveContent(Content content) {
        return contentRepository.save(content);
    }

    // Obtener por ID
    public Optional<Content> getContentById(String id) {
        return contentRepository.findById(id);
    }

    // Buscar por filtros opcionales
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
    public List<Content> findContentsWithFilters(String category, String creatorId, String mediaType, String tag, int page, int size) {
        // Por ahora: filtrado simple en memoria (hasta tener query específica o Pageable)
        List<Content> allContents = contentRepository.findAll();

        return allContents.stream()
                .filter(c -> category == null || c.getCategory().equalsIgnoreCase(category))
                .filter(c -> creatorId == null || c.getCreatorId().equalsIgnoreCase(creatorId))
                .filter(c -> mediaType == null || c.getMediaType().equalsIgnoreCase(mediaType))
                .filter(c -> tag == null || (c.getTags() != null && c.getTags().contains(tag)))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    // Incrementar contador de vistas (puede integrarse con Redis más adelante)
    public void incrementViewCount(String contentId) {
        String key = VIEW_COUNT_KEY_PREFIX + contentId;
        // Si integrás Redis, podés descomentar algo como:
        // redisTemplate.opsForValue().increment(key);
    }


    // COMMENTS
    public List<CommentResponse> getComments(String contentId) {
        // TODO: fetch from CommentRepository when available
        return new ArrayList<>();
    }

    public CommentResponse addComment(String contentId, CommentRequest request) {
        CommentResponse response = new CommentResponse();
        response.id = UUID.randomUUID().toString();
        response.userId = request.userId;
        response.text = request.text;
        response.createdAt = LocalDateTime.now().toString();
        return response;
    }

    // REACTIONS
    public ReactionResponse reactToContent(String id, ReactionRequest request) {
        ReactionResponse resp = new ReactionResponse();
        resp.contentId = id;
        resp.totalLikes = new Random().nextInt(500); // mock placeholder
        resp.totalReactions = resp.totalLikes;
        return resp;
    }

    // LIVE
    public LiveResponse startLive(LiveStartRequest request) {
        LiveResponse resp = new LiveResponse();
        resp.liveId = UUID.randomUUID().toString();
        resp.status = "STARTED";
        resp.startedAt = LocalDateTime.now().toString();
        return resp;
    }

    public Optional<LiveResponse> endLive(String id) {
        LiveResponse resp = new LiveResponse();
        resp.liveId = id;
        resp.status = "ENDED";
        resp.endedAt = LocalDateTime.now().toString();
        return Optional.of(resp);
    }

    // DTOs (mover a paquete dto)
    public static class CommentRequest {
        public String userId;
        public String text;
    }

    public static class CommentResponse {
        public String id;
        public String userId;
        public String text;
        public String createdAt;
    }

    public static class ReactionRequest {
        public String userId;
        public String reactionType;
    }

    public static class ReactionResponse {
        public String contentId;
        public long totalLikes;
        public long totalReactions;
    }

    public static class LiveStartRequest {
        public String creatorId;
        public String title;
        public String language;
        public String region;
    }

    public static class LiveResponse {
        public String liveId;
        public String contentId;
        public String status;
        public String startedAt;
        public String endedAt;
    }
}
