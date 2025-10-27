package com.uade.tpo.marketplace.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.service.OutboxEvent;

import java.util.List;

/**
 * Repositorio para la colección 'outbox_events' de MongoDB.
 * Utilizado por el Patrón Outbox para gestionar eventos de forma asíncrona.
 */
@Repository
public interface OutboxEventRepository extends MongoRepository<OutboxEvent, String> {

    /**
     * Este es el método clave que utiliza el OutboxEventProcessor.
     * Spring Data MongoDB creará automáticamente la consulta para:
     * "Encontrar todos los eventos donde 'processed' sea 'false' Y 'retryCount' sea 'menor que' (LessThan) el valor proporcionado".
     *
     * @param maxRetries El número máximo de reintentos permitidos.
     * @return Una lista de eventos pendientes de procesar.
     */
    List<OutboxEvent> findByProcessedFalseAndRetryCountLessThan(int maxRetries);

}
