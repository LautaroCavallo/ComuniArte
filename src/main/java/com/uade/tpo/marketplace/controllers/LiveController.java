package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
