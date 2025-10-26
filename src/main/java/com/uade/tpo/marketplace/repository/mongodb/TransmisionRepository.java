package com.uade.tpo.marketplace.repository.mongodb;

import com.uade.tpo.marketplace.entity.mongodb.Transmision;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransmisionRepository extends MongoRepository<Transmision, String> {
    
    /**
     * Encuentra todas las transmisiones de un creador
     */
    List<Transmision> findByCreadorId(String creadorId);
    
    /**
     * Encuentra transmisiones por estado
     */
    List<Transmision> findByEstado(String estado);
    
    /**
     * Encuentra transmisiones activas de un creador
     */
    List<Transmision> findByCreadorIdAndEstado(String creadorId, String estado);
    
    /**
     * Encuentra transmisiones por categoría
     */
    List<Transmision> findByCategoria(String categoria);
    
    /**
     * Encuentra todas las transmisiones activas
     */
    default List<Transmision> findAllActive() {
        return findByEstado("ACTIVA");
    }
}

