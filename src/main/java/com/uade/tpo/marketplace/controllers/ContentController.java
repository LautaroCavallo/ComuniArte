package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.mongodb.Comentario;
import com.uade.tpo.marketplace.entity.mongodb.Contenido;
import com.uade.tpo.marketplace.service.CommentService;
import com.uade.tpo.marketplace.service.ContentService;
import com.uade.tpo.marketplace.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final CommentService commentService;
    private final LikeService likeService;

    // POST /api/contents
    @PostMapping
    public ResponseEntity<Contenido> uploadContent(@RequestBody Contenido content) {
        Contenido saved = contentService.saveContent(content);
        return ResponseEntity.ok(saved);
    }

    // GET /api/contents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Contenido> getContentById(@PathVariable String id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/content/list?category=&tag=&creatorId=&type=&page=&size=
    @GetMapping("/list")
    public ResponseEntity<List<Contenido>> getContentsByFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Contenido> results = contentService.findContentsWithFilters(category, tag, creatorId, type, page, size);
        return ResponseEntity.ok(results);
    }

    // PUT /api/contents/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Contenido> updateContent(
            @PathVariable String id,
            @RequestBody Contenido updatedContent) {
        try {
            Contenido updated = contentService.updateContent(id, updatedContent);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/contents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/contents/{id}/view
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> registerView(@PathVariable String id) {
        contentService.incrementViewCount(id);
        return ResponseEntity.accepted().build();
    }

    // ========== COMENTARIOS ==========

    // GET /api/contents/{id}/comments
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comentario>> getComments(@PathVariable String id) {
        List<Comentario> comments = commentService.getCommentsByContentId(id);
        return ResponseEntity.ok(comments);
    }

    // POST /api/contents/{id}/comments
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comentario> addComment(
            @PathVariable String id,
            @RequestBody AddCommentRequest request) {
        Comentario comment = commentService.addComment(
                id,
                request.getUsuarioId(),
                request.getTexto(),
                false // No es comentario en vivo
        );
        return ResponseEntity.ok(comment);
    }

    // ========== LIKES / REACCIONES ==========

    // POST /api/contents/{id}/like
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> likeContent(
            @PathVariable String id,
            @RequestBody LikeRequest request) {
        
        boolean success = likeService.likeContent(request.getUsuarioId(), id);
        
        LikeResponse response = new LikeResponse();
        response.setContentId(id);
        response.setUserId(request.getUsuarioId());
        response.setSuccess(success);
        response.setMessage(success ? "Like registrado exitosamente" : "Ya había dado like a este contenido");
        response.setTotalLikes(likeService.getLikesCount(id));
        
        return ResponseEntity.ok(response);
    }

    // DELETE /api/contents/{id}/like (para quitar like)
    @DeleteMapping("/{id}/like")
    public ResponseEntity<LikeResponse> unlikeContent(
            @PathVariable String id,
            @RequestParam String usuarioId) {
        
        boolean success = likeService.unlikeContent(usuarioId, id);
        
        LikeResponse response = new LikeResponse();
        response.setContentId(id);
        response.setUserId(usuarioId);
        response.setSuccess(success);
        response.setMessage(success ? "Like eliminado exitosamente" : "No había dado like a este contenido");
        response.setTotalLikes(likeService.getLikesCount(id));
        
        return ResponseEntity.ok(response);
    }

    // GET /api/contents/{id}/likes
    @GetMapping("/{id}/likes")
    public ResponseEntity<Map<String, Object>> getLikesStats(@PathVariable String id) {
        Map<String, Object> stats = likeService.getContentLikeStats(id);
        return ResponseEntity.ok(stats);
    }

    // GET /api/contents/{id}/liked (verificar si usuario dio like)
    @GetMapping("/{id}/liked")
    public ResponseEntity<Map<String, Boolean>> checkUserLiked(
            @PathVariable String id,
            @RequestParam String usuarioId) {
        boolean liked = likeService.hasUserLiked(usuarioId, id);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    // ========== DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCommentRequest {
        private String usuarioId;
        private String texto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeRequest {
        private String usuarioId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        private String contentId;
        private String userId;
        private boolean success;
        private String message;
        private long totalLikes;
    }
}
