package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.neo4j.ContenidoNeo4j;
import com.uade.tpo.marketplace.repository.neo4j.ContenidoNeo4jRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; // Importar Optional

@Slf4j // Asegúrate de tener @Slf4j
@Service
@RequiredArgsConstructor
@Transactional("neo4jTransactionManager")
public class Neo4jContentService {

    private final ContenidoNeo4jRepository contenidoNeo4jRepository;

    public void createContenidoNeo4j(String mongoContentId, String titulo) {
        log.info("Neo4jContentService: Intentando crear nodo Contenido para ID de Mongo: {}", mongoContentId); // <-- LOG AÑADIDO
        if (mongoContentId == null || mongoContentId.isBlank()) {
             log.error("Neo4jContentService: Se recibió un mongoContentId nulo o vacío. No se puede crear el nodo."); // <-- LOG AÑADIDO
             // Lanzar excepción para que el OutboxProcessor sepa que falló
             throw new IllegalArgumentException("mongoContentId no puede ser nulo o vacío");
        }
        try {
            Optional<ContenidoNeo4j> existing = contenidoNeo4jRepository.findByContenidoId(mongoContentId);
            if (existing.isEmpty()) {
                 ContenidoNeo4j neo4jNode = new ContenidoNeo4j(mongoContentId, titulo);
                 log.debug("Neo4jContentService: Nodo a guardar: {}", neo4jNode); // <-- LOG AÑADIDO (debug)
                 ContenidoNeo4j savedNode = contenidoNeo4jRepository.save(neo4jNode);
                 // Comprobar si el guardado realmente devolvió algo
                 if (savedNode != null && savedNode.getId() != null) {
                    log.info("Neo4jContentService: Nodo Contenido creado/guardado exitosamente en Neo4j con ID interno {}: {}", savedNode.getId(), mongoContentId); // <-- LOG MEJORADO
                 } else {
                    log.error("Neo4jContentService: ¡FALLO SILENCIOSO! La operación save() para Contenido {} no devolvió un nodo guardado.", mongoContentId); // <-- LOG AÑADIDO
                    // Lanzar excepción para que el OutboxProcessor reintente
                     throw new RuntimeException("Fallo silencioso al guardar nodo Contenido en Neo4j para ID: " + mongoContentId);
                 }
            } else {
                 log.warn("Neo4jContentService: El nodo Contenido para ID {} ya existía en Neo4j. No se creó duplicado.", mongoContentId);
            }
        } catch (Exception e) {
             log.error("Neo4jContentService: ¡ERROR EXPLÍCITO al guardar nodo Contenido {} en Neo4j! Error: {}", mongoContentId, e.getMessage(), e); // <-- LOG AÑADIDO (con stack trace)
             throw e; // Re-lanzar para que OutboxProcessor reintente
        }
    }

    public void deleteContenidoNeo4j(String mongoContentId) {
         log.info("Neo4jContentService: Intentando eliminar nodo Contenido para ID de Mongo: {}", mongoContentId); // <-- LOG AÑADIDO
         if (mongoContentId == null || mongoContentId.isBlank()) {
             log.error("Neo4jContentService: Se recibió un mongoContentId nulo o vacío. No se puede eliminar el nodo."); // <-- LOG AÑADIDO
             throw new IllegalArgumentException("mongoContentId no puede ser nulo o vacío para eliminar");
         }
        try {
            contenidoNeo4jRepository.deleteByContenidoId(mongoContentId);
            log.info("Neo4jContentService: Nodo Contenido eliminado (o no existía) en Neo4j para ID: {}", mongoContentId); // <-- LOG MEJORADO
        } catch (Exception e) {
             log.error("Neo4jContentService: ¡ERROR EXPLÍCITO al eliminar nodo Contenido {} en Neo4j! Error: {}", mongoContentId, e.getMessage(), e); // <-- LOG AÑADIDO
             throw e; // Re-lanzar
        }
    }
}

