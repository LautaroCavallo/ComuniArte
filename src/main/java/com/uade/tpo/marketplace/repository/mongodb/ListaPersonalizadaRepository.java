package com.uade.tpo.marketplace.repository.mongodb;

import com.uade.tpo.marketplace.entity.mongodb.ListaPersonalizada;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ListaPersonalizadaRepository extends MongoRepository<ListaPersonalizada, String> {
    
    /**
     * Encuentra todas las listas de un usuario
     */
    List<ListaPersonalizada> findByUsuarioId(String usuarioId);
    
    /**
     * Encuentra una lista específica de un usuario
     */
    Optional<ListaPersonalizada> findByIdAndUsuarioId(String id, String usuarioId);
    
    /**
     * Encuentra listas públicas
     */
    List<ListaPersonalizada> findByEsPublica(boolean esPublica);
    
    /**
     * Encuentra listas públicas de un usuario
     */
    List<ListaPersonalizada> findByUsuarioIdAndEsPublica(String usuarioId, boolean esPublica);
}

