package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Content;
import com.uade.tpo.marketplace.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    // POST /api/contents
    @PostMapping
    public ResponseEntity<Content> uploadContent(@RequestBody Content content) {
        Content saved = contentService.saveContent(content);
        return ResponseEntity.ok(saved);
    }

    // GET /api/contents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable String id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/contents?category=&creatorId=
    @GetMapping
    public ResponseEntity<List<Content>> getContentsByFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String creatorId) {
        List<Content> results = contentService.findContents(category, creatorId);
        return ResponseEntity.ok(results);
    }

    // POST /api/contents/{id}/view
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> registerView(@PathVariable String id) {
        contentService.incrementViewCount(id);
        return ResponseEntity.accepted().build();
    }
}
