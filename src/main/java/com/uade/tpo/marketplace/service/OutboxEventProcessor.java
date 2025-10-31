package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.repository.mongodb.OutboxEvent;
import com.uade.tpo.marketplace.repository.mongodb.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
// ¡Volvemos a importar Transactional y Propagation!
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final Neo4jContentService neo4jContentService; 
    private final Neo4jUserService neo4jUserService;
    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedDelay = 10000)
    @Transactional(propagation = Propagation.NOT_SUPPORTED) 
    public void processOutboxEvents() {

        List<OutboxEvent> eventsToProcess = outboxEventRepository
            .findByProcessedFalseAndRetryCountLessThan(MAX_RETRIES);

        if (eventsToProcess.isEmpty()) {
            return; 
        }

        log.info("Se encontraron {} eventos pendientes. Procesando...", eventsToProcess.size());

        for (OutboxEvent event : eventsToProcess) {
            log.debug("Procesando evento Outbox ID: {}, Tipo: {}", event.getId(), event.getEventType());
            boolean processedSuccessfully = false;
            try {
                switch (event.getEventType()) {
                    case "USER_REGISTERED":
                        processUserRegistration(event.getPayload());
                        processedSuccessfully = true; // Marcar éxito si no hay excepción
                        break;
                    case "CONTENT_CREATED":
                        processContentCreation(event.getPayload());
                        processedSuccessfully = true; // Marcar éxito
                        break;
                    case "CONTENT_DELETED":
                        processContentDeletion(event.getPayload());
                        processedSuccessfully = true; // Marcar éxito
                        break;
                    // Añadir más casos aquí si es necesario
                    default:
                        log.warn("Tipo de evento Outbox desconocido encontrado: {}. Marcando como fallido para evitar reintentos.", event.getEventType());
                        event.setRetryCount(MAX_RETRIES); // Poner reintentos al máximo para detenerlo
                        event.setLastError("Tipo de evento desconocido: " + event.getEventType());
                        break; // Salir del switch
                }
                // Si llegamos aquí sin excepción Y el tipo era conocido, marcamos como procesado
                if (processedSuccessfully) {
                    event.setProcessed(true);
                    event.setProcessedAt(java.time.Instant.now()); // Guardar cuándo se procesó
                    event.setLastError(null); // Limpiar errores previos si los hubo
                    log.info("Evento {} ({}) procesado exitosamente.", event.getId(), event.getEventType());
                }
            }  catch (Exception e) {
                // Si algo falla, incrementamos el reintento y guardamos el error
                log.error(
                    "Fallo al procesar evento {} ({}): {}. Reintento {}/{}",
                    event.getId(),
                    event.getEventType(),
                    e.getMessage(), // Mensaje de error específico
                    event.getRetryCount() + 1,
                    MAX_RETRIES
                );
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastError(e.getMessage().substring(0, Math.min(e.getMessage().length(), 255))); // Guardar mensaje de error (limitado)

                 // Si alcanzamos el máximo de reintentos, lo marcamos como procesado (fallido)
                 if (event.getRetryCount() >= MAX_RETRIES) {
                     log.error("Evento {} ({}) alcanzó el máximo de reintentos y será ignorado.", event.getId(), event.getEventType());
                     event.setProcessed(true); // Marcar como procesado para no volver a intentarlo
                     event.setProcessedAt(java.time.Instant.now());
                 }

            } finally {
                // Guardamos el estado actualizado del evento (processed, retryCount, lastError)
                try {
                     outboxEventRepository.save(event);
                } catch (Exception saveEx) {
                    log.error("¡FALLO CRÍTICO AL GUARDAR ESTADO DE OUTBOX EVENT {}! Error: {}", event.getId(), saveEx.getMessage());
                    // Si falla el guardado, el evento se reintentará en la próxima ejecución.
                }
            }
        }
    }

    private void processUserRegistration(Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        String nombre = (String) payload.get("nombre");
        String tipoUsuario = (String) payload.get("tipoUsuario");
        log.info("Procesando evento USER_REGISTERED. Payload: userId={}, nombre={}, tipoUsuario={}", userId, nombre, tipoUsuario);
        if (userId == null || nombre == null) {
            throw new IllegalArgumentException("Payload incompleto para USER_REGISTERED: falta userId o nombre.");
        }
        // Si no viene tipoUsuario por alguna razón, usar ESPECTADOR por defecto
        neo4jUserService.createUsuarioNeo4j(userId, nombre, tipoUsuario != null ? tipoUsuario : "ESPECTADOR");
    }

    /** 3. NUEVO: Lógica para CONTENT_CREATED */
    private void processContentCreation(Map<String, Object> payload) {
        String contentId = (String) payload.get("contentId");
        String titulo = (String) payload.get("titulo");
        log.info("Procesando evento CONTENT_CREATED. Payload: contentId={}, titulo={}", contentId, titulo);
        if (contentId == null) {
            log.error("Error en payload de CONTENT_CREATED: falta contentId. Payload: {}", payload);
            throw new IllegalArgumentException("Payload incompleto para CONTENT_CREATED: falta contentId");
        }
        neo4jContentService.createContenidoNeo4j(contentId, titulo != null ? titulo : "Contenido sin título");
    }

    /** 4. NUEVO: Lógica para CONTENT_DELETED */
     private void processContentDeletion(Map<String, Object> payload) {
        String contentId = (String) payload.get("contentId");
        log.info("Procesando evento CONTENT_DELETED. Payload: contentId={}", contentId);
        if (contentId == null) {
             log.error("Error en payload de CONTENT_DELETED: falta contentId. Payload: {}", payload);
            throw new IllegalArgumentException("Payload incompleto para CONTENT_DELETED: falta contentId");
        }
        neo4jContentService.deleteContenidoNeo4j(contentId);
    }
}