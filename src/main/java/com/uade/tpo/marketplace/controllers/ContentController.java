package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.mongodb.Contenido;
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

    // POST /api/contents/{id}/view
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> registerView(@PathVariable String id) {
        contentService.incrementViewCount(id);
        return ResponseEntity.accepted().build();
    }
}
