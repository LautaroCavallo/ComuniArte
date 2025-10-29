package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.LogEntry;
import com.uade.tpo.marketplace.repository.mongodb.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio centralizado para gestión de logs del sistema
 * Registra operaciones de MongoDB, Neo4j, Redis, WebSocket y aplicación
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;

    /**
     * Registra un log de información de forma asíncrona
     */
    @Async
    public void logInfo(String service, String source, String method, String action, 
                       String message, String userId, String resourceId, Map<String, Object> metadata) {
        try {
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level("INFO")
                    .service(service)
                    .source(source)
                    .method(method)
                    .action(action)
                    .message(message)
                    .userId(userId)
                    .resourceId(resourceId)
                    .metadata(metadata)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log INFO: {}", e.getMessage());
        }
    }

    /**
     * Registra un log de error con stack trace
     */
    @Async
    public void logError(String service, String source, String method, String action, 
                        String message, Exception exception, String userId, String resourceId) {
        try {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level("ERROR")
                    .service(service)
                    .source(source)
                    .method(method)
                    .action(action)
                    .message(message + ": " + exception.getMessage())
                    .userId(userId)
                    .resourceId(resourceId)
                    .stackTrace(stackTrace)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log ERROR: {}", e.getMessage());
        }
    }

    /**
     * Registra un log de warning
     */
    @Async
    public void logWarning(String service, String source, String method, String action, 
                          String message, String userId, String resourceId) {
        try {
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level("WARN")
                    .service(service)
                    .source(source)
                    .method(method)
                    .action(action)
                    .message(message)
                    .userId(userId)
                    .resourceId(resourceId)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log WARN: {}", e.getMessage());
        }
    }

    /**
     * Registra operación de Redis con duración
     */
    @Async
    public void logRedisOperation(String operation, String key, Long duration, boolean success) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("key", key);
            metadata.put("success", success);
            
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level(success ? "INFO" : "ERROR")
                    .service("RedisService")
                    .source("REDIS")
                    .method(operation)
                    .action("REDIS_OPERATION")
                    .message(operation + " on key: " + key)
                    .duration(duration)
                    .metadata(metadata)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log de Redis: {}", e.getMessage());
        }
    }

    /**
     * Registra operación de Neo4j
     */
    @Async
    public void logNeo4jOperation(String operation, String userId, String resourceId, 
                                  Long duration, boolean success) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("success", success);
            
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level(success ? "INFO" : "ERROR")
                    .service("Neo4jService")
                    .source("NEO4J")
                    .method(operation)
                    .action("NEO4J_OPERATION")
                    .message(operation + " completed")
                    .userId(userId)
                    .resourceId(resourceId)
                    .duration(duration)
                    .metadata(metadata)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log de Neo4j: {}", e.getMessage());
        }
    }

    /**
     * Registra operación de MongoDB
     */
    @Async
    public void logMongoOperation(String operation, String collection, String documentId, 
                                  Long duration, boolean success) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("collection", collection);
            metadata.put("success", success);
            
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level(success ? "INFO" : "ERROR")
                    .service("MongoService")
                    .source("MONGODB")
                    .method(operation)
                    .action("MONGO_OPERATION")
                    .message(operation + " on collection: " + collection)
                    .resourceId(documentId)
                    .duration(duration)
                    .metadata(metadata)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log de MongoDB: {}", e.getMessage());
        }
    }

    /**
     * Registra evento de WebSocket
     */
    @Async
    public void logWebSocketEvent(String event, String liveId, String userId, String message) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("event", event);
            
            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level("INFO")
                    .service("WebSocketService")
                    .source("WEBSOCKET")
                    .method(event)
                    .action("WEBSOCKET_EVENT")
                    .message(message)
                    .userId(userId)
                    .resourceId(liveId)
                    .metadata(metadata)
                    .build();
            
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar log de WebSocket: {}", e.getMessage());
        }
    }

    // ========== CONSULTAS DE LOGS ==========

    /**
     * Obtiene logs recientes (últimos 100)
     */
    public List<LogEntry> getRecentLogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return logEntryRepository.findAllByOrderByTimestampDesc(pageable).getContent();
    }

    /**
     * Obtiene logs por nivel
     */
    public List<LogEntry> getLogsByLevel(String level, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return logEntryRepository.findByLevelOrderByTimestampDesc(level, pageable);
    }

    /**
     * Obtiene logs por fuente
     */
    public List<LogEntry> getLogsBySource(String source) {
        return logEntryRepository.findBySource(source);
    }

    /**
     * Obtiene logs por servicio
     */
    public List<LogEntry> getLogsByService(String service) {
        return logEntryRepository.findByService(service);
    }

    /**
     * Obtiene logs en rango de fechas
     */
    public List<LogEntry> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return logEntryRepository.findByTimestampBetween(start, end);
    }

    /**
     * Obtiene logs de un usuario
     */
    public List<LogEntry> getLogsByUser(String userId) {
        return logEntryRepository.findByUserId(userId);
    }

    /**
     * Obtiene errores recientes
     */
    public List<LogEntry> getRecentErrors(int limit) {
        return getLogsByLevel("ERROR", limit);
    }

    /**
     * Obtiene estadísticas de logs
     */
    public Map<String, Object> getLogStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        
        stats.put("totalLogs", logEntryRepository.count());
        stats.put("errorsLast24h", logEntryRepository.countByLevelAndTimestampAfter("ERROR", last24Hours));
        stats.put("warningsLast24h", logEntryRepository.countByLevelAndTimestampAfter("WARN", last24Hours));
        stats.put("infoLast24h", logEntryRepository.countByLevelAndTimestampAfter("INFO", last24Hours));
        
        // Estadísticas por fuente
        Map<String, Long> bySource = new HashMap<>();
        bySource.put("REDIS", logEntryRepository.findBySource("REDIS").stream().count());
        bySource.put("MONGODB", logEntryRepository.findBySource("MONGODB").stream().count());
        bySource.put("NEO4J", logEntryRepository.findBySource("NEO4J").stream().count());
        bySource.put("WEBSOCKET", logEntryRepository.findBySource("WEBSOCKET").stream().count());
        stats.put("bySource", bySource);
        
        return stats;
    }

    /**
     * Obtiene logs de Redis específicamente
     */
    public List<LogEntry> getRedisLogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return logEntryRepository.findBySourceAndLevelOrderByTimestampDesc("REDIS", "INFO", pageable).getContent();
    }

    /**
     * Obtiene errores de Redis
     */
    public List<LogEntry> getRedisErrors(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return logEntryRepository.findBySourceAndLevelOrderByTimestampDesc("REDIS", "ERROR", pageable).getContent();
    }

    /**
     * Limpia logs antiguos (más de 30 días)
     */
    public void cleanOldLogs() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<LogEntry> oldLogs = logEntryRepository.findByTimestampBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0), 
                thirtyDaysAgo
            );
            logEntryRepository.deleteAll(oldLogs);
            log.info("Limpiados {} logs antiguos", oldLogs.size());
        } catch (Exception e) {
            log.error("Error al limpiar logs antiguos: {}", e.getMessage());
        }
    }
}

