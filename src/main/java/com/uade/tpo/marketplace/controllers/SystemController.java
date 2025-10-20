package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        return ResponseEntity.ok(systemService.getSystemStatus());
    }

    @GetMapping("/config")
    public ResponseEntity<?> getSystemConfig() {
        return ResponseEntity.ok(systemService.getSystemConfig());
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(systemService.getActiveSessions());
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getSystemLogs() {
        return ResponseEntity.ok(systemService.getSystemLogs());
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> getSystemMetrics() {
        return ResponseEntity.ok(systemService.getSystemMetrics());
    }
}
