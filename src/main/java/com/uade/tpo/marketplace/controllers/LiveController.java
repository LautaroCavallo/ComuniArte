package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveService liveService;

    @GetMapping("/{id}/chat")
    public ResponseEntity<?> liveChat(@PathVariable String id) {
        return ResponseEntity.ok(liveService.liveChat(id));
    }

    @PostMapping("/{id}/donate")
    public ResponseEntity<Void> donate(@PathVariable String id, @RequestParam Double amount) {
        liveService.donate(id, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<Void> sendQuestion(@PathVariable String id, @RequestBody String question) {
        liveService.sendQuestion(id, question);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<?> streamEvents(@PathVariable String id) {
        return ResponseEntity.ok(liveService.streamEvents(id));
    }
}
