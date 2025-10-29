package com.uade.tpo.marketplace.repository.mongodb;

import com.uade.tpo.marketplace.entity.mongodb.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LogEntryRepository extends MongoRepository<LogEntry, String> {
    
    /**
     * Encuentra logs por nivel
     */
    List<LogEntry> findByLevel(String level);
    
    /**
     * Encuentra logs por servicio
     */
    List<LogEntry> findByService(String service);
    
    /**
     * Encuentra logs por fuente (MONGODB, REDIS, NEO4J, etc.)
     */
    List<LogEntry> findBySource(String source);
    
    /**
     * Encuentra logs en un rango de fechas
     */
    List<LogEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Encuentra logs de un usuario
     */
    List<LogEntry> findByUserId(String userId);
    
    /**
     * Encuentra errores recientes
     */
    List<LogEntry> findByLevelOrderByTimestampDesc(String level, Pageable pageable);
    
    /**
     * Encuentra logs por servicio y nivel
     */
    List<LogEntry> findByServiceAndLevel(String service, String level);
    
    /**
     * Encuentra logs paginados
     */
    Page<LogEntry> findAllByOrderByTimestampDesc(Pageable pageable);
    
    /**
     * Cuenta logs por nivel
     */
    long countByLevel(String level);
    
    /**
     * Cuenta logs de errores en las últimas horas
     */
    long countByLevelAndTimestampAfter(String level, LocalDateTime after);
    
    /**
     * Encuentra logs por acción
     */
    List<LogEntry> findByAction(String action);
    
    /**
     * Encuentra logs por fuente y nivel con paginación
     */
    Page<LogEntry> findBySourceAndLevelOrderByTimestampDesc(String source, String level, Pageable pageable);
    
    /**
     * Query personalizada para estadísticas
     */
    @Query("{ 'timestamp': { $gte: ?0 }, 'level': ?1 }")
    List<LogEntry> findRecentByLevel(LocalDateTime since, String level);
}

