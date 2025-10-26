package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.Transaccion;
import com.uade.tpo.marketplace.repository.mongodb.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TransaccionRepository transaccionRepository;

    private static final String LIVE_CHAT_PREFIX = "live:comentarios:";
    private static final String LIVE_VIEWERS_PREFIX = "live:viewers:";
    private static final String LIVE_QUESTIONS_PREFIX = "live:preguntas:";
    private static final String LIVE_DONATIONS_PREFIX = "live:donaciones:";

    /**
     * Obtiene los mensajes del chat en vivo
     * Usa Redis Streams para mensajería en tiempo real
     */
    public Object liveChat(String liveId) {
        log.info("Obteniendo chat en vivo para transmisión {}", liveId);
        
        try {
            String chatKey = LIVE_CHAT_PREFIX + liveId;
            
            // Obtener últimos 50 mensajes del chat (usando List en Redis)
            List<String> mensajes = redisTemplate.opsForList().range(chatKey, -50, -1);
            
            List<Map<String, Object>> chatMessages = new ArrayList<>();
            if (mensajes != null) {
                for (String mensaje : mensajes) {
                    // Parsear mensaje (formato: timestamp|userId|texto)
                    String[] parts = mensaje.split("\\|", 3);
                    if (parts.length == 3) {
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("timestamp", parts[0]);
                        msg.put("userId", parts[1]);
                        msg.put("texto", parts[2]);
                        chatMessages.add(msg);
                    }
                }
            }
            
            log.info("Se obtuvieron {} mensajes del chat", chatMessages.size());
            return chatMessages;
            
        } catch (Exception e) {
            log.error("Error al obtener chat en vivo: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Envía un mensaje al chat en vivo
     */
    public void sendChatMessage(String liveId, String userId, String mensaje) {
        log.info("Enviando mensaje al chat de transmisión {}", liveId);
        
        try {
            String chatKey = LIVE_CHAT_PREFIX + liveId;
            String timestamp = Instant.now().toString();
            String fullMessage = timestamp + "|" + userId + "|" + mensaje;
            
            // Agregar mensaje a la lista (Redis List)
            redisTemplate.opsForList().rightPush(chatKey, fullMessage);
            
            // Limitar a últimos 100 mensajes
            redisTemplate.opsForList().trim(chatKey, -100, -1);
            
            // Publicar evento en canal Pub/Sub para notificaciones en tiempo real
            redisTemplate.convertAndSend("live:events:" + liveId, 
                    "CHAT|" + userId + "|" + mensaje);
            
            log.info("Mensaje enviado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al enviar mensaje: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra una donación a un creador durante la transmisión
     */
    public void donate(String liveId, String donorId, String creatorId, Double amount) {
        log.info("Registrando donación de {} para transmisión {}", amount, liveId);
        
        try {
            // Guardar transacción en MongoDB para persistencia
            Transaccion transaccion = Transaccion.builder()
                    .usuarioId(donorId)
                    .creadorId(creatorId)
                    .tipo("donacion")
                    .monto(amount)
                    .fecha(LocalDateTime.now())
                    .metodoPago("online")
                    .build();
            
            transaccionRepository.save(transaccion);
            
            // Registrar donación en Redis para estadísticas en tiempo real
            String donationsKey = LIVE_DONATIONS_PREFIX + liveId;
            redisTemplate.opsForList().rightPush(donationsKey, 
                    donorId + "|" + amount + "|" + Instant.now());
            
            // Publicar evento de donación
            redisTemplate.convertAndSend("live:events:" + liveId, 
                    "DONATION|" + donorId + "|" + amount);
            
            log.info("Donación registrada exitosamente: ${}", amount);
            
        } catch (Exception e) {
            log.error("Error al registrar donación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar donación", e);
        }
    }

    /**
     * Envía una pregunta al presentador
     */
    public void sendQuestion(String liveId, String userId, String question) {
        log.info("Enviando pregunta para transmisión {}", liveId);
        
        try {
            String questionsKey = LIVE_QUESTIONS_PREFIX + liveId;
            String timestamp = Instant.now().toString();
            String fullQuestion = timestamp + "|" + userId + "|" + question;
            
            // Agregar pregunta a la lista
            redisTemplate.opsForList().rightPush(questionsKey, fullQuestion);
            
            // Publicar evento de nueva pregunta
            redisTemplate.convertAndSend("live:events:" + liveId, 
                    "QUESTION|" + userId + "|" + question);
            
            log.info("Pregunta enviada exitosamente");
            
        } catch (Exception e) {
            log.error("Error al enviar pregunta: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene las preguntas pendientes para el presentador
     */
    public List<Map<String, Object>> getQuestions(String liveId) {
        log.info("Obteniendo preguntas para transmisión {}", liveId);
        
        try {
            String questionsKey = LIVE_QUESTIONS_PREFIX + liveId;
            List<String> preguntas = redisTemplate.opsForList().range(questionsKey, 0, -1);
            
            List<Map<String, Object>> questions = new ArrayList<>();
            if (preguntas != null) {
                for (String pregunta : preguntas) {
                    String[] parts = pregunta.split("\\|", 3);
                    if (parts.length == 3) {
                        Map<String, Object> q = new HashMap<>();
                        q.put("timestamp", parts[0]);
                        q.put("userId", parts[1]);
                        q.put("question", parts[2]);
                        questions.add(q);
                    }
                }
            }
            
            return questions;
            
        } catch (Exception e) {
            log.error("Error al obtener preguntas: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene eventos en tiempo real de la transmisión
     * (Comentarios, reacciones, nuevos espectadores)
     */
    public Object streamEvents(String liveId) {
        log.info("Obteniendo eventos en tiempo real para transmisión {}", liveId);
        
        try {
            Map<String, Object> events = new HashMap<>();
            
            // Obtener espectadores activos
            String viewersKey = LIVE_VIEWERS_PREFIX + liveId;
            Long viewersCount = redisTemplate.opsForSet().size(viewersKey);
            events.put("viewersCount", viewersCount != null ? viewersCount : 0);
            
            // Obtener últimas donaciones
            String donationsKey = LIVE_DONATIONS_PREFIX + liveId;
            List<String> recentDonations = redisTemplate.opsForList().range(donationsKey, -10, -1);
            events.put("recentDonations", recentDonations != null ? recentDonations.size() : 0);
            
            // Calcular total de donaciones
            double totalDonations = 0.0;
            if (recentDonations != null) {
                for (String donation : recentDonations) {
                    String[] parts = donation.split("\\|");
                    if (parts.length >= 2) {
                        try {
                            totalDonations += Double.parseDouble(parts[1]);
                        } catch (NumberFormatException e) {
                            log.warn("Error al parsear donación: {}", donation);
                        }
                    }
                }
            }
            events.put("totalDonations", totalDonations);
            
            // Obtener preguntas pendientes
            String questionsKey = LIVE_QUESTIONS_PREFIX + liveId;
            Long questionsCount = redisTemplate.opsForList().size(questionsKey);
            events.put("pendingQuestions", questionsCount != null ? questionsCount : 0);
            
            log.info("Eventos obtenidos: {} espectadores, {} preguntas", 
                    viewersCount, questionsCount);
            
            return events;
            
        } catch (Exception e) {
            log.error("Error al obtener eventos: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Registra un espectador activo en la transmisión
     */
    public void joinLive(String liveId, String userId) {
        log.info("Usuario {} se unió a transmisión {}", userId, liveId);
        
        try {
            String viewersKey = LIVE_VIEWERS_PREFIX + liveId;
            redisTemplate.opsForSet().add(viewersKey, userId);
            
            // Publicar evento de nuevo espectador
            redisTemplate.convertAndSend("live:events:" + liveId, 
                    "JOIN|" + userId);
            
        } catch (Exception e) {
            log.error("Error al registrar espectador: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina un espectador de la transmisión
     */
    public void leaveLive(String liveId, String userId) {
        log.info("Usuario {} salió de transmisión {}", userId, liveId);
        
        try {
            String viewersKey = LIVE_VIEWERS_PREFIX + liveId;
            redisTemplate.opsForSet().remove(viewersKey, userId);
            
            // Publicar evento de salida
            redisTemplate.convertAndSend("live:events:" + liveId, 
                    "LEAVE|" + userId);
            
        } catch (Exception e) {
            log.error("Error al eliminar espectador: {}", e.getMessage(), e);
        }
    }
}
