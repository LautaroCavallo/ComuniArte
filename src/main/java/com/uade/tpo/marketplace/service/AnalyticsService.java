package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.AnalisisHistorico;
import com.uade.tpo.marketplace.entity.mongodb.Contenido;
import com.uade.tpo.marketplace.repository.mongodb.AnalisisHistoricoRepository;
import com.uade.tpo.marketplace.repository.mongodb.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ContenidoRepository contenidoRepository;
    private final AnalisisHistoricoRepository analisisHistoricoRepository;

    private static final String VIEWS_KEY = "ranking:vistas:global";
    private static final String LIKES_KEY = "ranking:likes:global";
    private static final String CONTENT_VIEWS_PREFIX = "analytics:content:";
    private static final String CREATOR_STATS_PREFIX = "analytics:creator:";

    /**
     * Registra una nueva vista o interacción en un contenido
     */
    public void registerView(String contentId) {
        log.info("Registrando vista para contenido {}", contentId);
        
        try {
            // Incrementar contador en Redis (Sorted Set para ranking)
            redisTemplate.opsForZSet().incrementScore(VIEWS_KEY, contentId, 1.0);
            
            // Incrementar contador específico del contenido
            String contentKey = CONTENT_VIEWS_PREFIX + contentId;
            redisTemplate.opsForValue().increment(contentKey, 1);
            
            log.info("Vista registrada exitosamente");
        } catch (Exception e) {
            log.error("Error al registrar vista: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra un "like" en un contenido
     */
    public void registerLike(String contentId) {
        log.info("Registrando like para contenido {}", contentId);
        
        try {
            redisTemplate.opsForZSet().incrementScore(LIKES_KEY, contentId, 1.0);
            log.info("Like registrado exitosamente");
        } catch (Exception e) {
            log.error("Error al registrar like: {}", e.getMessage(), e);
        }
    }

    /**
     * Devuelve métricas detalladas de un contenido
     */
    public Object getContentMetrics(String contentId) {
        log.info("Obteniendo métricas para contenido {}", contentId);
        
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Obtener información del contenido desde MongoDB
            Optional<Contenido> contenidoOpt = contenidoRepository.findById(contentId);
            if (contenidoOpt.isPresent()) {
                Contenido contenido = contenidoOpt.get();
                metrics.put("titulo", contenido.getTitulo());
                metrics.put("tipo", contenido.getTipo());
                metrics.put("categoria", contenido.getCategoria());
                metrics.put("creadorId", contenido.getCreadorId());
                metrics.put("fechaPublicacion", contenido.getFechaPublicacion());
            }
            
            // Obtener métricas desde Redis
            Double vistas = redisTemplate.opsForZSet().score(VIEWS_KEY, contentId);
            Double likes = redisTemplate.opsForZSet().score(LIKES_KEY, contentId);
            
            metrics.put("vistas", vistas != null ? vistas.longValue() : 0L);
            metrics.put("likes", likes != null ? likes.longValue() : 0L);
            
            // Calcular engagement (likes / vistas)
            if (vistas != null && vistas > 0) {
                double engagement = (likes != null ? likes : 0.0) / vistas * 100;
                metrics.put("engagement", String.format("%.2f%%", engagement));
            } else {
                metrics.put("engagement", "0.00%");
            }
            
            log.info("Métricas obtenidas: {} vistas, {} likes", 
                    metrics.get("vistas"), metrics.get("likes"));
            
        } catch (Exception e) {
            log.error("Error al obtener métricas de contenido: {}", e.getMessage(), e);
        }
        
        return metrics;
    }

    /**
     * Devuelve métricas del creador (seguidores, interacciones, impacto regional)
     */
    public Object getCreatorMetrics(String creatorId) {
        log.info("Obteniendo métricas para creador {}", creatorId);
        
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Obtener todos los contenidos del creador
            List<Contenido> contenidos = contenidoRepository.findByCreadorId(creatorId);
            metrics.put("totalContenidos", contenidos.size());
            
            // Calcular vistas totales y likes totales
            long vistasTotal = 0;
            long likesTotal = 0;
            
            for (Contenido contenido : contenidos) {
                Double vistas = redisTemplate.opsForZSet().score(VIEWS_KEY, contenido.getId());
                Double likes = redisTemplate.opsForZSet().score(LIKES_KEY, contenido.getId());
                
                vistasTotal += (vistas != null ? vistas.longValue() : 0);
                likesTotal += (likes != null ? likes.longValue() : 0);
            }
            
            metrics.put("vistasTotal", vistasTotal);
            metrics.put("likesTotal", likesTotal);
            metrics.put("promedioVistasPorContenido", 
                    contenidos.isEmpty() ? 0 : vistasTotal / contenidos.size());
            
            // Engagement promedio
            if (vistasTotal > 0) {
                double engagementPromedio = (double) likesTotal / vistasTotal * 100;
                metrics.put("engagementPromedio", String.format("%.2f%%", engagementPromedio));
            } else {
                metrics.put("engagementPromedio", "0.00%");
            }
            
            log.info("Métricas de creador obtenidas: {} contenidos, {} vistas totales", 
                    contenidos.size(), vistasTotal);
            
        } catch (Exception e) {
            log.error("Error al obtener métricas de creador: {}", e.getMessage(), e);
        }
        
        return metrics;
    }

    /**
     * Genera rankings de popularidad por categoría, región o tipo
     */
    public List<Object> getRanking(String category, String region, String type) {
        log.info("Generando ranking - categoria: {}, region: {}, tipo: {}", 
                category, region, type);
        
        List<Object> ranking = new ArrayList<>();
        
        try {
            // Obtener top contenidos desde Redis (Sorted Set ordenado por vistas)
            Set<ZSetOperations.TypedTuple<String>> topContenidos = 
                    redisTemplate.opsForZSet().reverseRangeWithScores(VIEWS_KEY, 0, 49); // Top 50
            
            if (topContenidos != null) {
                for (ZSetOperations.TypedTuple<String> tuple : topContenidos) {
                    String contentId = tuple.getValue();
                    Double score = tuple.getScore();
                    
                    // Obtener detalles del contenido desde MongoDB
                    Optional<Contenido> contenidoOpt = contenidoRepository.findById(contentId);
                    if (contenidoOpt.isPresent()) {
                        Contenido contenido = contenidoOpt.get();
                        
                        // Aplicar filtros
                        boolean match = true;
                        if (category != null && !category.isEmpty()) {
                            match = category.equals(contenido.getCategoria());
                        }
                        if (type != null && !type.isEmpty()) {
                            match = match && type.equals(contenido.getTipo());
                        }
                        // Filtro por región requeriría metadatos adicionales
                        
                        if (match) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("contenidoId", contentId);
                            item.put("titulo", contenido.getTitulo());
                            item.put("tipo", contenido.getTipo());
                            item.put("categoria", contenido.getCategoria());
                            item.put("vistas", score != null ? score.longValue() : 0);
                            ranking.add(item);
                        }
                    }
                }
            }
            
            log.info("Ranking generado con {} elementos", ranking.size());
            
        } catch (Exception e) {
            log.error("Error al generar ranking: {}", e.getMessage(), e);
        }
        
        return ranking;
    }

    /**
     * Mide el impacto social y cultural por región
     */
    public Object getImpact(String region) {
        log.info("Midiendo impacto social para región: {}", region);
        
        Map<String, Object> impact = new HashMap<>();
        
        try {
            // Buscar análisis históricos por región
            List<AnalisisHistorico> analisis = analisisHistoricoRepository.findByRegion(region);
            
            if (!analisis.isEmpty()) {
                // Obtener el análisis más reciente
                AnalisisHistorico ultimoAnalisis = analisis.stream()
                        .max(Comparator.comparing(AnalisisHistorico::getFechaAnalisis))
                        .orElse(null);
                
                if (ultimoAnalisis != null) {
                    impact.put("region", ultimoAnalisis.getRegion());
                    impact.put("fechaAnalisis", ultimoAnalisis.getFechaAnalisis());
                    impact.put("metricasResumidas", ultimoAnalisis.getMetricasResumidas());
                    impact.put("preferenciasCulturales", 
                            ultimoAnalisis.getPreferenciasCulturalesDetectadas());
                }
            } else {
                // Generar análisis en tiempo real
                impact.put("region", region != null ? region : "global");
                impact.put("fechaAnalisis", LocalDateTime.now());
                impact.put("mensaje", "Análisis en tiempo real - datos históricos no disponibles");
                
                // Calcular métricas básicas
                long vistasTotal = 0;
                Set<String> allContentIds = redisTemplate.opsForZSet().range(VIEWS_KEY, 0, -1);
                if (allContentIds != null) {
                    for (String contentId : allContentIds) {
                        Double vistas = redisTemplate.opsForZSet().score(VIEWS_KEY, contentId);
                        vistasTotal += (vistas != null ? vistas.longValue() : 0);
                    }
                }
                
                Map<String, Object> metricas = new HashMap<>();
                metricas.put("vistasTotal", vistasTotal);
                metricas.put("contenidosActivos", allContentIds != null ? allContentIds.size() : 0);
                impact.put("metricasResumidas", metricas);
            }
            
            log.info("Impacto social calculado para región: {}", region);
            
        } catch (Exception e) {
            log.error("Error al calcular impacto social: {}", e.getMessage(), e);
        }
        
        return impact;
    }
}
