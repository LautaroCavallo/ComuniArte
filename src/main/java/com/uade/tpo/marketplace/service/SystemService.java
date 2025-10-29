package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final LogService logService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Obtiene el estado general de los servicios
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        
        // Estado de MongoDB (si llegamos aquí, está funcionando)
        status.put("mongodb", "CONNECTED");
        
        // Estado de Redis
        try {
            redisTemplate.opsForValue().get("health:check");
            status.put("redis", "CONNECTED");
        } catch (Exception e) {
            status.put("redis", "ERROR: " + e.getMessage());
        }
        
        // Neo4j (asumimos que está conectado si la app arrancó)
        status.put("neo4j", "CONNECTED");
        
        // WebSocket
        status.put("websocket", "ACTIVE");
        
        return status;
    }

    /**
     * Obtiene configuración del sistema
     */
    public Map<String, Object> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("application", "ComuniArte");
        config.put("version", "0.0.1-SNAPSHOT");
        config.put("environment", "development");
        
        // Configuraciones de bases de datos
        Map<String, Object> databases = new HashMap<>();
        databases.put("mongodb", Map.of("status", "active", "database", "comuniarte"));
        databases.put("neo4j", Map.of("status", "active", "uri", "bolt://localhost:7687"));
        databases.put("redis", Map.of("status", "active", "host", "localhost:6379"));
        config.put("databases", databases);
        
        // Configuraciones de WebSocket
        config.put("websocket", Map.of(
            "endpoint", "/ws",
            "broker", "Simple Broker",
            "prefixes", List.of("/app", "/topic", "/queue")
        ));
        
        return config;
    }

    /**
     * Lista sesiones activas (placeholder - requiere implementación de gestión de sesiones)
     */
    public Map<String, Object> getActiveSessions() {
        Map<String, Object> sessions = new HashMap<>();
        
        // Obtener sesiones de Redis si están implementadas
        try {
            sessions.put("totalSessions", 0);
            sessions.put("activeSessions", List.of());
            sessions.put("message", "Session management not fully implemented");
        } catch (Exception e) {
            sessions.put("error", e.getMessage());
        }
        
        return sessions;
    }

    /**
     * Consulta logs del sistema
     */
    public Map<String, Object> getSystemLogs(String level, String source, Integer limit) {
        Map<String, Object> logsResponse = new HashMap<>();
        
        int logLimit = (limit != null && limit > 0) ? limit : 50;
        
        List<LogEntry> logs;
        
        if (level != null && !level.isEmpty()) {
            logs = logService.getLogsByLevel(level.toUpperCase(), logLimit);
        } else if (source != null && !source.isEmpty()) {
            logs = logService.getLogsBySource(source.toUpperCase());
            if (logs.size() > logLimit) {
                logs = logs.subList(0, logLimit);
            }
        } else {
            logs = logService.getRecentLogs(logLimit);
        }
        
        logsResponse.put("logs", logs);
        logsResponse.put("count", logs.size());
        logsResponse.put("timestamp", LocalDateTime.now());
        
        return logsResponse;
    }

    /**
     * Obtiene logs de Redis específicamente
     */
    public Map<String, Object> getRedisLogs(Integer limit) {
        Map<String, Object> response = new HashMap<>();
        
        int logLimit = (limit != null && limit > 0) ? limit : 50;
        
        List<LogEntry> redisLogs = logService.getRedisLogs(logLimit);
        List<LogEntry> redisErrors = logService.getRedisErrors(10);
        
        response.put("logs", redisLogs);
        response.put("errors", redisErrors);
        response.put("count", redisLogs.size());
        response.put("errorCount", redisErrors.size());
        
        return response;
    }

    /**
     * Obtiene métricas técnicas del sistema
     */
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Runtime metrics
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory() / 1024 / 1024 + " MB");
        memory.put("free", runtime.freeMemory() / 1024 / 1024 + " MB");
        memory.put("max", runtime.maxMemory() / 1024 / 1024 + " MB");
        memory.put("used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
        metrics.put("memory", memory);
        
        // CPU
        metrics.put("processors", runtime.availableProcessors());
        
        // Logs statistics
        metrics.put("logStatistics", logService.getLogStatistics());
        
        // Redis info (si está disponible)
        try {
            Map<String, Object> redisInfo = new HashMap<>();
            redisInfo.put("status", "connected");
            // Aquí podrías agregar más métricas de Redis si es necesario
            metrics.put("redis", redisInfo);
        } catch (Exception e) {
            metrics.put("redis", Map.of("status", "error", "message", e.getMessage()));
        }
        
        metrics.put("timestamp", LocalDateTime.now());
        
        return metrics;
    }

    /**
     * Obtiene estadísticas de logs
     */
    public Map<String, Object> getLogStatistics() {
        return logService.getLogStatistics();
    }
}

