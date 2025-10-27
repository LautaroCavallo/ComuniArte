package com.uade.tpo.marketplace.service;

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
    private final Neo4jUserService neo4jUserService;
    private static final int MAX_RETRIES = 3;

    /**
     * Procesa los eventos pendientes de la "bandeja de salida" (outbox).
     * Se ejecuta cada 10 segundos.
     * * --- ¡NUEVO CAMBIO! ---
     * * Forzamos a Spring a NO usar una transacción para este método.
     * * Esto debería evitar que busque el 'transactionManager' por defecto.
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // <-- ¡AQUÍ ESTÁ LA CORRECCIÓN!
    public void processOutboxEvents() {

        List<OutboxEvent> eventsToProcess = outboxEventRepository
            .findByProcessedFalseAndRetryCountLessThan(MAX_RETRIES);

        if (eventsToProcess.isEmpty()) {
            return; // No hay trabajo que hacer
        }

        log.info("Se encontraron {} eventos pendientes. Procesando...", eventsToProcess.size());

        for (OutboxEvent event : eventsToProcess) {
            // ... (el resto del código sigue igual)
            boolean processedSuccessfully = false;
            try {
                if ("USER_REGISTERED".equals(event.getEventType())) {
                    processUserRegistration(event.getPayload());
                }
                event.setProcessed(true);
                processedSuccessfully = true;
                log.info("Evento {} procesado exitosamente.", event.getId());
            } catch (Exception e) {
                log.error(
                    "Fallo al procesar evento {}: {}. Reintento {}/{}",
                    event.getId(),
                    e.getMessage(),
                    event.getRetryCount() + 1,
                    MAX_RETRIES
                );
                event.setRetryCount(event.getRetryCount() + 1);
            } finally {
                try {
                     outboxEventRepository.save(event);
                } catch (Exception saveEx) {
                    log.error("¡FALLO AL GUARDAR ESTADO DE OUTBOX EVENT {}! Error: {}", event.getId(), saveEx.getMessage());
                }
            }
        }
    }

    /**
     * Lógica específica para el evento USER_REGISTERED.
     * Esta llamada SÍ usará la transacción de Neo4j (definida en Neo4jUserService).
     */
    private void processUserRegistration(Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        String nombre = (String) payload.get("nombre");

        if (userId == null || nombre == null) {
            throw new IllegalArgumentException("Payload incompleto para USER_REGISTERED: falta userId o nombre.");
        }

        neo4jUserService.createUsuarioNeo4j(userId, nombre);
    }
}