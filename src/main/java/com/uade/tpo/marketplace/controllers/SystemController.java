package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para endpoints del sistema
 * Incluye estado, configuración, logs, métricas y sesiones
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    /**
     * GET /api/system/status
     * Obtiene el estado de todos los servicios (MongoDB, Neo4j, Redis, WebSocket)
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        return ResponseEntity.ok(systemService.getSystemStatus());
    }

    /**
     * GET /api/system/config
     * Obtiene la configuración del sistema
     */
    @GetMapping("/config")
    public ResponseEntity<?> getSystemConfig() {
        return ResponseEntity.ok(systemService.getSystemConfig());
    }

    /**
     * GET /api/system/sessions
     * Lista las sesiones activas
     */
    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(systemService.getActiveSessions());
    }

    /**
     * GET /api/system/logs?level={level}&source={source}&limit={limit}
     * Obtiene logs del sistema con filtros opcionales
     * 
     * @param level Nivel de log: INFO, WARN, ERROR, DEBUG (opcional)
     * @param source Fuente: MONGODB, NEO4J, REDIS, WEBSOCKET, APPLICATION (opcional)
     * @param limit Número máximo de logs a retornar (default: 50)
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getSystemLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(systemService.getSystemLogs(level, source, limit));
    }

    /**
     * GET /api/system/logs/redis?limit={limit}
     * Obtiene logs específicos de Redis
     * 
     * @param limit Número máximo de logs a retornar (default: 50)
     */
    @GetMapping("/logs/redis")
    public ResponseEntity<?> getRedisLogs(@RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(systemService.getRedisLogs(limit));
    }

    /**
     * GET /api/system/logs/statistics
     * Obtiene estadísticas de logs (por nivel, fuente, últimas 24h)
     */
    @GetMapping("/logs/statistics")
    public ResponseEntity<?> getLogStatistics() {
        return ResponseEntity.ok(systemService.getLogStatistics());
    }

    /**
     * GET /api/system/metrics
     * Obtiene métricas técnicas del sistema (CPU, memoria, base de datos)
     */
    @GetMapping("/metrics")
    public ResponseEntity<?> getSystemMetrics() {
        return ResponseEntity.ok(systemService.getSystemMetrics());
    }
}
