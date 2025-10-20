package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.AnalyticsService;
import com.uade.tpo.marketplace.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/views")
    public ResponseEntity<Void> registerView(@RequestParam String contentId) {
        analyticsService.registerView(contentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> getContentMetrics(@PathVariable String id) {
        return ResponseEntity.ok(analyticsService.getContentMetrics(id));
    }

    @GetMapping("/creator/{id}")
    public ResponseEntity<?> getCreatorMetrics(@PathVariable String id) {
        return ResponseEntity.ok(analyticsService.getCreatorMetrics(id));
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String region,
                                        @RequestParam(required = false) String type) {
        return ResponseEntity.ok(analyticsService.getRanking(category, region, type));
    }

    @GetMapping("/impact")
    public ResponseEntity<?> getImpact(@RequestParam(required = false) String region) {
        return ResponseEntity.ok(analyticsService.getImpact(region));
    }
}

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
class ReportsController {

    private final ReportsService reportsService;

    @PostMapping("/custom")
    public ResponseEntity<?> generateCustomReport(@RequestBody String reportRequest) {
        return ResponseEntity.ok(reportsService.generateCustomReport(reportRequest));
    }
}
