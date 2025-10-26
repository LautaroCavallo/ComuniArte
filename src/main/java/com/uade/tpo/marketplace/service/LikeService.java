package com.uade.tpo.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de likes (reacciones) en contenidos
 * Implementa persistencia híbrida:
 * - Redis: Contadores en tiempo real para acceso rápido
 * - Neo4j: Relaciones GUSTA para recomendaciones y análisis de red
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Neo4jTemplate neo4jTemplate;

    private static final String LIKES_COUNTER_PREFIX = "likes:count:";
    private static final String LIKES_RANKING_KEY = "ranking:likes:global";
    private static final String USER_LIKES_PREFIX = "user:likes:";

    /**
     * Registra un like de un usuario en un contenido
     * Actualiza tanto Redis (contadores) como Neo4j (relación GUSTA)
     * 
     * @param usuarioId ID del usuario
     * @param contenidoId ID del contenido
     * @return true si se registró exitosamente, false si ya existía
     */
    public boolean likeContent(String usuarioId, String contenidoId) {
        log.info("Usuario {} está dando like a contenido {}", usuarioId, contenidoId);
        
        try {
            // Verificar si ya existe el like en Redis
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            Boolean alreadyLiked = redisTemplate.opsForSet().isMember(userLikesKey, contenidoId);
            
            if (Boolean.TRUE.equals(alreadyLiked)) {
                log.warn("Usuario {} ya había dado like al contenido {}", usuarioId, contenidoId);
                return false;
            }
            
            // 1. Actualizar contador en Redis
            String likesKey = LIKES_COUNTER_PREFIX + contenidoId;
            redisTemplate.opsForValue().increment(likesKey, 1);
            
            // 2. Actualizar ranking global de likes (Sorted Set)
            redisTemplate.opsForZSet().incrementScore(LIKES_RANKING_KEY, contenidoId, 1.0);
            
            // 3. Registrar que el usuario dio like (para evitar duplicados)
            redisTemplate.opsForSet().add(userLikesKey, contenidoId);
            
            // 4. Crear relación GUSTA en Neo4j para recomendaciones
            createGustaRelationInNeo4j(usuarioId, contenidoId);
            
            log.info("Like registrado exitosamente en Redis y Neo4j");
            return true;
            
        } catch (Exception e) {
            log.error("Error al registrar like: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar like", e);
        }
    }

    /**
     * Elimina un like de un usuario en un contenido
     * 
     * @param usuarioId ID del usuario
     * @param contenidoId ID del contenido
     * @return true si se eliminó exitosamente, false si no existía
     */
    public boolean unlikeContent(String usuarioId, String contenidoId) {
        log.info("Usuario {} está quitando like a contenido {}", usuarioId, contenidoId);
        
        try {
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            Boolean hadLiked = redisTemplate.opsForSet().isMember(userLikesKey, contenidoId);
            
            if (Boolean.FALSE.equals(hadLiked)) {
                log.warn("Usuario {} no había dado like al contenido {}", usuarioId, contenidoId);
                return false;
            }
            
            // 1. Decrementar contador en Redis
            String likesKey = LIKES_COUNTER_PREFIX + contenidoId;
            redisTemplate.opsForValue().decrement(likesKey, 1);
            
            // 2. Actualizar ranking global
            redisTemplate.opsForZSet().incrementScore(LIKES_RANKING_KEY, contenidoId, -1.0);
            
            // 3. Eliminar de likes del usuario
            redisTemplate.opsForSet().remove(userLikesKey, contenidoId);
            
            // 4. Eliminar relación GUSTA en Neo4j
            deleteGustaRelationInNeo4j(usuarioId, contenidoId);
            
            log.info("Like eliminado exitosamente");
            return true;
            
        } catch (Exception e) {
            log.error("Error al eliminar like: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar unlike", e);
        }
    }

    /**
     * Obtiene el número total de likes de un contenido
     * 
     * @param contenidoId ID del contenido
     * @return Número de likes (desde Redis para máxima velocidad)
     */
    public long getLikesCount(String contenidoId) {
        try {
            String likesKey = LIKES_COUNTER_PREFIX + contenidoId;
            String count = redisTemplate.opsForValue().get(likesKey);
            return count != null ? Long.parseLong(count) : 0L;
        } catch (Exception e) {
            log.error("Error al obtener contador de likes: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * Verifica si un usuario dio like a un contenido
     * 
     * @param usuarioId ID del usuario
     * @param contenidoId ID del contenido
     * @return true si el usuario dio like, false en caso contrario
     */
    public boolean hasUserLiked(String usuarioId, String contenidoId) {
        try {
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userLikesKey, contenidoId));
        } catch (Exception e) {
            log.error("Error al verificar like: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los contenidos que le gustaron a un usuario
     * 
     * @param usuarioId ID del usuario
     * @return Lista de IDs de contenidos
     */
    public List<String> getUserLikes(String usuarioId) {
        try {
            String userLikesKey = USER_LIKES_PREFIX + usuarioId;
            var members = redisTemplate.opsForSet().members(userLikesKey);
            if (members == null) {
                return List.of();
            }
            return members.stream().toList();
        } catch (Exception e) {
            log.error("Error al obtener likes del usuario: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene estadísticas de likes de un contenido
     * 
     * @param contenidoId ID del contenido
     * @return Mapa con estadísticas
     */
    public Map<String, Object> getContentLikeStats(String contenidoId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Contador de likes
            long likesCount = getLikesCount(contenidoId);
            stats.put("totalLikes", likesCount);
            
            // Ranking global (posición en el ranking de likes)
            Long rank = redisTemplate.opsForZSet().reverseRank(LIKES_RANKING_KEY, contenidoId);
            stats.put("rankingPosition", rank != null ? rank + 1 : null);
            
            // Score en el ranking
            Double score = redisTemplate.opsForZSet().score(LIKES_RANKING_KEY, contenidoId);
            stats.put("rankingScore", score != null ? score.longValue() : 0L);
            
            log.info("Estadísticas de likes para {}: {}", contenidoId, stats);
            return stats;
            
        } catch (Exception e) {
            log.error("Error al obtener estadísticas de likes: {}", e.getMessage());
            stats.put("totalLikes", 0L);
            stats.put("error", e.getMessage());
            return stats;
        }
    }

    /**
     * Crea relación GUSTA en Neo4j para recomendaciones
     */
    private void createGustaRelationInNeo4j(String usuarioId, String contenidoId) {
        try {
            String query = 
                "MERGE (u:Usuario {userId: $usuarioId}) " +
                "MERGE (c:Contenido {contenidoId: $contenidoId}) " +
                "MERGE (u)-[g:GUSTA]->(c) " +
                "RETURN u, g, c";
            
            Map<String, Object> params = Map.of(
                "usuarioId", usuarioId,
                "contenidoId", contenidoId
            );
            
            // Ejecutar query de creación en Neo4j
            neo4jTemplate.findAll(query, params, Object.class);
            
            log.info("Relación GUSTA creada en Neo4j");
            
        } catch (Exception e) {
            log.error("Error al crear relación GUSTA en Neo4j: {}", e.getMessage(), e);
            // No lanzamos excepción para no fallar el like si Neo4j falla
        }
    }

    /**
     * Elimina relación GUSTA en Neo4j
     */
    private void deleteGustaRelationInNeo4j(String usuarioId, String contenidoId) {
        try {
            String query = 
                "MATCH (u:Usuario {userId: $usuarioId})-[g:GUSTA]->(c:Contenido {contenidoId: $contenidoId}) " +
                "DELETE g";
            
            Map<String, Object> params = Map.of(
                "usuarioId", usuarioId,
                "contenidoId", contenidoId
            );
            
            // Ejecutar query de eliminación en Neo4j
            neo4jTemplate.findAll(query, params, Object.class);
            
            log.info("Relación GUSTA eliminada en Neo4j");
            
        } catch (Exception e) {
            log.error("Error al eliminar relación GUSTA en Neo4j: {}", e.getMessage(), e);
        }
    }
}

