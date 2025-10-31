package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.dto.ContentRequest;
import com.uade.tpo.marketplace.entity.mongodb.Comentario;
import com.uade.tpo.marketplace.entity.mongodb.Contenido;
import com.uade.tpo.marketplace.service.CommentService;
import com.uade.tpo.marketplace.service.ContentService;
import com.uade.tpo.marketplace.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@Tag(name = "1. Gestión de Contenidos", description = "Endpoints para subir, listar, actualizar y eliminar contenidos (videos, audios, textos)")
public class ContentController {

    private final ContentService contentService;
    private final CommentService commentService;
    private final LikeService likeService;

    // POST /api/contents
    // Modificado para usar ContentRequest DTO
    @PostMapping
    public ResponseEntity<Contenido> uploadContent(@RequestBody ContentRequest request) {

        // 1. Mapear DTO a Entidad Contenido
        Map<String, Object> metadatos = new HashMap<>();
        // Añadimos la descripción al mapa de metadatos
        if (request.getDescripcion() != null && !request.getDescripcion().isEmpty()) {
            metadatos.put("descripcion", request.getDescripcion());
        }
        // Aquí podrías añadir más campos del DTO al mapa si fuera necesario

        Contenido nuevoContenido = Contenido.builder()
                .titulo(request.getTitle())
                .tipo(request.getMediaType())
                .urlArchivo(request.getMediaUrl())
                .creadorId(request.getCreatorId())
                .categoria(request.getCategory())
                .etiquetas(request.getTags())
                .metadatosEnriquecidos(metadatos)
                .fechaPublicacion(LocalDateTime.now()) // Establecer la fecha de publicación en el servidor
                .build();

        // 2. Guardar la nueva entidad
        Contenido saved = contentService.saveContent(nuevoContenido);
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
    // NOTA: Este endpoint sigue aceptando la entidad completa.
    // Considera crear un DTO 'UpdateContentRequest' similar para las actualizaciones.
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
