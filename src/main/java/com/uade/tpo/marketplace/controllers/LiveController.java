package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.mongodb.Transmision;
import com.uade.tpo.marketplace.service.LiveService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveService liveService;
    private final WebSocketChatController webSocketChatController;

    @GetMapping("/{id}/chat")
    public ResponseEntity<?> liveChat(@PathVariable String id) {
        return ResponseEntity.ok(liveService.liveChat(id));
    }

    @PostMapping("/{id}/chat")
    public ResponseEntity<Void> sendChatMessage(
            @PathVariable String id, 
            @RequestParam String userId, 
            @RequestBody Map<String, String> body) {
        String mensaje = body.get("mensaje");
        liveService.sendChatMessage(id, userId, mensaje);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/donate")
    public ResponseEntity<Void> donate(
            @PathVariable String id, 
            @RequestParam String donorId,
            @RequestParam String donorName,
            @RequestParam String creatorId,
            @RequestParam Double amount) {
        liveService.donate(id, donorId, creatorId, amount);
        
        // Notificar via WebSocket a todos los espectadores
        webSocketChatController.sendDonationNotification(id, donorName, amount);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<Void> sendQuestion(
            @PathVariable String id, 
            @RequestParam String userId,
            @RequestBody Map<String, String> body) {
        String question = body.get("question");
        liveService.sendQuestion(id, userId, question);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable String id) {
        return ResponseEntity.ok(liveService.getQuestions(id));
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<?> streamEvents(@PathVariable String id) {
        return ResponseEntity.ok(liveService.streamEvents(id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinLive(@PathVariable String id, @RequestParam String userId) {
        liveService.joinLive(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveLive(@PathVariable String id, @RequestParam String userId) {
        liveService.leaveLive(id, userId);
        return ResponseEntity.ok().build();
    }

    // ========== GESTIÓN DE TRANSMISIONES ==========

    /**
     * POST /api/live/start
     * Inicia una nueva transmisión en vivo
     */
    @PostMapping("/start")
    public ResponseEntity<Transmision> startLive(@RequestBody StartLiveRequest request) {
        try {
            Transmision transmision = liveService.startLive(
                    request.getCreadorId(),
                    request.getTitulo(),
                    request.getDescripcion(),
                    request.getCategoria(),
                    request.getEtiquetas()
            );
            return ResponseEntity.ok(transmision);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/live/{id}/end
     * Finaliza una transmisión en vivo
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<Transmision> endLive(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean guardarComoContenido) {
        try {
            Transmision transmision = liveService.endLive(id, guardarComoContenido);
            return ResponseEntity.ok(transmision);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/live/{id}
     * Obtiene información de una transmisión
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transmision> getTransmision(@PathVariable String id) {
        return liveService.getTransmision(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/live/active
     * Obtiene todas las transmisiones activas
     */
    @GetMapping("/active")
    public ResponseEntity<List<Transmision>> getActiveTransmissions() {
        List<Transmision> transmissions = liveService.getActiveTransmissions();
        return ResponseEntity.ok(transmissions);
    }

    /**
     * GET /api/live/creator/{creadorId}
     * Obtiene todas las transmisiones de un creador
     */
    @GetMapping("/creator/{creadorId}")
    public ResponseEntity<List<Transmision>> getCreatorTransmissions(@PathVariable String creadorId) {
        List<Transmision> transmissions = liveService.getCreatorTransmissions(creadorId);
        return ResponseEntity.ok(transmissions);
    }

    // ========== ESPECTADORES ==========

    /**
     * GET /api/live/{id}/viewers
     * Obtiene la lista de espectadores activos
     */
    @GetMapping("/{id}/viewers")
    public ResponseEntity<Map<String, Object>> getViewers(@PathVariable String id) {
        List<String> viewers = liveService.getActiveViewers(id);
        long count = liveService.getViewersCount(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("viewers", viewers);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/live/{id}/viewers/count
     * Obtiene solo el número de espectadores activos
     */
    @GetMapping("/{id}/viewers/count")
    public ResponseEntity<Map<String, Long>> getViewersCount(@PathVariable String id) {
        long count = liveService.getViewersCount(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ========== DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartLiveRequest {
        private String creadorId;
        private String titulo;
        private String descripcion;
        private String categoria;
        private List<String> etiquetas;
    }
}
