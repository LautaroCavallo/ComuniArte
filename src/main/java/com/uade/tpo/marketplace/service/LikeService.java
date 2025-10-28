package com.uade.tpo.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient; // Usar Neo4jClient es más moderno que Neo4jTemplate
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
// No necesitamos @Transactional aquí si tratamos Redis y Neo4j como independientes

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Para la lista de likes

/**
 * Servicio para gestión de likes (reacciones) en contenidos
 * Implementa persistencia híbrida:
 * - Redis: Contadores en tiempo real y sets de usuario para acceso rápido
 * - Neo4j: Relaciones GUSTA para recomendaciones y análisis de red
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String, String> redisTemplate;
    // Usaremos Neo4jClient en lugar de Neo4jTemplate (más flexible)
    private final Neo4jClient neo4jClient; 

    private static final String LIKES_COUNTER_PREFIX = "likes:count:";
    private static final String LIKES_RANKING_KEY = "ranking:likes:global";
    private static final String USER_LIKES_PREFIX = "user:likes:";

    /**
     * Registra un like de un usuario en un contenido.
     * Actualiza Redis y intenta crear la relación GUSTA en Neo4j.
     * Si Neo4j falla, la operación aún se considera exitosa (Eventual Consistency).
     *
     * @param usuarioId ID del usuario (de MongoDB)
     * @param contenidoId ID del contenido (de MongoDB)
     * @return true si se registró exitosamente en Redis, false si ya existía el like.
     */
    public boolean likeContent(String usuarioId, String contenidoId) {
        log.info("Usuario {} está dando like a contenido {}", usuarioId, contenidoId);

        String userLikesKey = USER_LIKES_PREFIX + usuarioId;
        String likesKey = LIKES_COUNTER_PREFIX + contenidoId;

        try {
            // Usar SADD que devuelve 1 si se añadió, 0 si ya existía
            Long added = redisTemplate.opsForSet().add(userLikesKey, contenidoId);

            if (added != null && added == 1) {
                // Si se añadió (no existía):
                // 1. Incrementar contador
                redisTemplate.opsForValue().increment(likesKey, 1);
                // 2. Actualizar ranking
                redisTemplate.opsForZSet().incrementScore(LIKES_RANKING_KEY, contenidoId, 1.0);
                // 3. Crear relación GUSTA en Neo4j (best-effort)
                createGustaRelationInNeo4j(usuarioId, contenidoId);

                log.info("Like registrado exitosamente en Redis (y Neo4j intentado)");
                return true;
            } else {
                // Si ya existía (add devolvió 0 o null)
                log.warn("Usuario {} ya había dado like al contenido {}", usuarioId, contenidoId);
                return false;
            }

        } catch (Exception e) {
            log.error("Error al registrar like para Usuario {} en Contenido {}: {}", usuarioId, contenidoId, e.getMessage(), e);
            // Considera deshacer operaciones de Redis si es crítico, pero para likes usualmente no lo es.
            // Por ahora, lanzamos excepción para que el Controller devuelva error.
            throw new RuntimeException("Error al procesar like", e);
        }
    }

    /**
     * Elimina un like de un usuario en un contenido.
     * Actualiza Redis y intenta eliminar la relación GUSTA en Neo4j.
     * Si Neo4j falla, la operación aún se considera exitosa (Eventual Consistency).
     *
     * @param usuarioId ID del usuario (de MongoDB)
     * @param contenidoId ID del contenido (de MongoDB)
     * @return true si se eliminó exitosamente de Redis, false si no existía el like.
     */
    public boolean unlikeContent(String usuarioId, String contenidoId) {
        log.info("Usuario {} está quitando like a contenido {}", usuarioId, contenidoId);

        String userLikesKey = USER_LIKES_PREFIX + usuarioId;
        String likesKey = LIKES_COUNTER_PREFIX + contenidoId;

        try {
             // Usar SREM que devuelve 1 si se eliminó, 0 si no existía
            Long removed = redisTemplate.opsForSet().remove(userLikesKey, contenidoId);

            if (removed != null && removed == 1) {
                 // Si se eliminó (existía):
                // 1. Decrementar contador (asegurarse de no ir bajo cero)
                Long currentCount = redisTemplate.opsForValue().decrement(likesKey, 1);
                if (currentCount != null && currentCount < 0) {
                    redisTemplate.opsForValue().set(likesKey, "0"); // Corregir si baja de 0
                }
                // 2. Actualizar ranking
                redisTemplate.opsForZSet().incrementScore(LIKES_RANKING_KEY, contenidoId, -1.0);
                // 3. Eliminar relación GUSTA en Neo4j (best-effort)
                deleteGustaRelationInNeo4j(usuarioId, contenidoId);

                log.info("Like eliminado exitosamente de Redis (y Neo4j intentado)");
                return true;
            } else {
                 // Si no existía (remove devolvió 0 o null)
                log.warn("Usuario {} no había dado like al contenido {}", usuarioId, contenidoId);
                return false;
            }

        } catch (Exception e) {
            log.error("Error al eliminar like para Usuario {} en Contenido {}: {}", usuarioId, contenidoId, e.getMessage(), e);
            throw new RuntimeException("Error al procesar unlike", e);
        }
    }

    /** Obtiene el número total de likes (desde Redis) */
    public long getLikesCount(String contenidoId) {
        try {
            String likesKey = LIKES_COUNTER_PREFIX + contenidoId;
            String count = redisTemplate.opsForValue().get(likesKey);
            return count != null ? Long.parseLong(count) : 0L;
        } catch (Exception e) {
            log.error("Error al obtener contador de likes para {}: {}", contenidoId, e.getMessage());
            return 0L;
        }
    }

    /** Verifica si un usuario dio like (desde Redis) */
    public boolean hasUserLiked(String usuarioId, String contenidoId) {
        try {
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userLikesKey, contenidoId));
        } catch (Exception e) {
            log.error("Error al verificar like para Usuario {} en Contenido {}: {}", usuarioId, contenidoId, e.getMessage());
            return false;
        }
    }

    /** Obtiene todos los IDs de contenidos que le gustaron a un usuario (desde Redis) */
    public List<String> getUserLikes(String usuarioId) {
        try {
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            var members = redisTemplate.opsForSet().members(userLikesKey);
            return (members != null) ? members.stream().toList() : List.of();
        } catch (Exception e) {
            log.error("Error al obtener likes del usuario {}: {}", usuarioId, e.getMessage());
            return List.of();
        }
    }

    /** Obtiene estadísticas de likes (desde Redis) */
    public Map<String, Object> getContentLikeStats(String contenidoId) {
        Map<String, Object> stats = new HashMap<>();
        try {
            long likesCount = getLikesCount(contenidoId);
            Long rank = redisTemplate.opsForZSet().reverseRank(LIKES_RANKING_KEY, contenidoId);
            Double score = redisTemplate.opsForZSet().score(LIKES_RANKING_KEY, contenidoId);

            stats.put("totalLikes", likesCount);
            stats.put("rankingPosition", rank != null ? rank + 1 : null);
            stats.put("rankingScore", score != null ? score.longValue() : 0L);

            log.debug("Estadísticas de likes para {}: {}", contenidoId, stats); // Cambiado a debug
            return stats;

        } catch (Exception e) {
            log.error("Error al obtener estadísticas de likes para {}: {}", contenidoId, e.getMessage());
            stats.put("totalLikes", 0L);
            stats.put("error", "Error al obtener estadísticas");
            return stats;
        }
    }

    // --- Métodos Privados para Neo4j ---

    /**
     * Crea relación GUSTA en Neo4j (Idempotente gracias a MERGE).
     * Usa la propiedad 'mongoUserId' para encontrar el nodo Usuario.
     */
    private void createGustaRelationInNeo4j(String usuarioId, String contenidoId) {
        log.debug("Intentando crear relación GUSTA en Neo4j: {} -> {}", usuarioId, contenidoId);
        try {
            // Usamos MERGE en los nodos para asegurarnos de que existan
            // (aunque deberían existir gracias al Outbox)
            // Usamos MERGE en la relación para evitar duplicados
            String query =
                "MERGE (u:Usuario {mongoUserId: $usuarioId}) " + // <-- CORRECCIÓN AQUÍ
                "MERGE (c:Contenido {contenidoId: $contenidoId}) " +
                "MERGE (u)-[g:GUSTA]->(c)"; // MERGE previene duplicados

            Map<String, Object> params = Map.of(
                "usuarioId", usuarioId,
                "contenidoId", contenidoId
            );

            // Ejecutar con Neo4jClient (run() para operaciones de escritura sin retorno)
            neo4jClient.query(query).bindAll(params).run();

            log.info("Relación GUSTA creada/verificada en Neo4j para {} -> {}", usuarioId, contenidoId);

        } catch (Exception e) {
            log.error("Error al crear relación GUSTA en Neo4j (Usuario: {}, Contenido: {}): {}",
                      usuarioId, contenidoId, e.getMessage(), e);
            // No lanzamos excepción para mantener la consistencia eventual
        }
    }

    /**
     * Elimina relación GUSTA en Neo4j.
     * Usa la propiedad 'mongoUserId' para encontrar el nodo Usuario.
     */
    private void deleteGustaRelationInNeo4j(String usuarioId, String contenidoId) {
         log.debug("Intentando eliminar relación GUSTA en Neo4j: {} -> {}", usuarioId, contenidoId);
        try {
            // Usamos MATCH aquí porque si los nodos o la relación no existen,
            // no queremos que MERGE los cree.
            String query =
                "MATCH (u:Usuario {mongoUserId: $usuarioId})-[g:GUSTA]->(c:Contenido {contenidoId: $contenidoId}) " + // <-- CORRECCIÓN AQUÍ
                "DELETE g";

            Map<String, Object> params = Map.of(
                "usuarioId", usuarioId,
                "contenidoId", contenidoId
            );

            neo4jClient.query(query).bindAll(params).run();

            log.info("Relación GUSTA eliminada (si existía) en Neo4j para {} -> {}", usuarioId, contenidoId);

        } catch (Exception e) {
            log.error("Error al eliminar relación GUSTA en Neo4j (Usuario: {}, Contenido: {}): {}",
                      usuarioId, contenidoId, e.getMessage(), e);
             // No lanzamos excepción
        }
    }
}
