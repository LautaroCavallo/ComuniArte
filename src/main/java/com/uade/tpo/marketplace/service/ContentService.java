package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.Contenido;
import com.uade.tpo.marketplace.repository.mongodb.ContenidoRepository;
import com.uade.tpo.marketplace.repository.mongodb.OutboxEvent;
import com.uade.tpo.marketplace.repository.mongodb.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j // Añadir para logging
@Service
@RequiredArgsConstructor // Usar inyección por constructor
public class ContentService {

    private final ContenidoRepository contenidoRepository;
    private final OutboxEventRepository outboxEventRepository; // Inyectar repo Outbox

    /**
     * Guarda un nuevo contenido.
     * La lógica de mapeo de DTO y la asignación de fechaPublicacion
     * ahora se manejan en el ContentController.
     */
     public Contenido saveContent(Contenido content) {
        // 1. Guardar en MongoDB (se confirma al instante)
        Contenido savedMongoContent = contenidoRepository.save(content);

        // --- INICIO LÓGICA OUTBOX ---
        // 2. Crear el evento Outbox (Mejor esfuerzo)
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("contentId", savedMongoContent.getId());
            payload.put("titulo", savedMongoContent.getTitulo()); // Útil para depurar

            var outboxEvent = OutboxEvent.builder()
                    .eventType("CONTENT_CREATED") // Nuevo tipo de evento
                    .payload(payload)
                    .createdAt(Instant.now())
                    .processed(false)
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(outboxEvent); // Guardado separado
            log.info("Evento Outbox CONTENT_CREATED generado para contenido {}", savedMongoContent.getId());

        } catch (Exception e) {
            // ¡MITIGACIÓN DE RIESGO!
            log.error(
                "¡FALLO CRÍTICO DE OUTBOX! El contenido {} ({}) fue creado en MongoDB pero falló la creación de su OutboxEvent. " +
                "El nodo en Neo4j NO se creará automáticamente. Error: {}",
                savedMongoContent.getId(),
                savedMongoContent.getTitulo(),
                e.getMessage()
            );
            // No lanzamos excepción, el guardado en Mongo fue exitoso.
        }
        // --- FIN LÓGICA OUTBOX ---

        // 3. Devolver el contenido guardado en Mongo
        return savedMongoContent;
    }

    // Actualizar contenido existente
    public Contenido updateContent(String id, Contenido updatedContent) {
        return contenidoRepository.findById(id)
                .map(existingContent -> {
                    // Actualizar solo los campos que no son null
                    if (updatedContent.getTitulo() != null) {
                        existingContent.setTitulo(updatedContent.getTitulo());
                    }
                    if (updatedContent.getTipo() != null) {
                        existingContent.setTipo(updatedContent.getTipo());
                    }
                    if (updatedContent.getUrlArchivo() != null) {
                        existingContent.setUrlArchivo(updatedContent.getUrlArchivo());
                    }
                    if (updatedContent.getMetadatosEnriquecidos() != null) {
                        // Considera "mergear" los mapas en lugar de reemplazar
                        existingContent.setMetadatosEnriquecidos(updatedContent.getMetadatosEnriquecidos());
                    }
                    if (updatedContent.getCategoria() != null) {
                        existingContent.setCategoria(updatedContent.getCategoria());
                    }
                    if (updatedContent.getEtiquetas() != null) {
                        existingContent.setEtiquetas(updatedContent.getEtiquetas());
                    }
                    return contenidoRepository.save(existingContent);
                })
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con id: " + id));
    }

    // Eliminar contenido
    public void deleteContent(String id) {
        // Verificar si existe antes de intentar eliminar (evita errores si ya no está)
        Optional<Contenido> contentOptional = contenidoRepository.findById(id);
        if (contentOptional.isEmpty()) {
            log.warn("Intento de eliminar contenido con ID {} que no existe.", id);
            return; // No hacer nada si no existe
        }

        contenidoRepository.deleteById(id);
        log.info("Contenido {} eliminado de MongoDB.", id);


        // --- INICIO LÓGICA OUTBOX ---
        // Generar evento para eliminar nodo en Neo4j
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("contentId", id);

            var outboxEvent = OutboxEvent.builder()
                    .eventType("CONTENT_DELETED")
                    .payload(payload)
                    .createdAt(Instant.now())
                    .processed(false)
                    .retryCount(0)
                    .build();
            outboxEventRepository.save(outboxEvent);
            log.info("Evento Outbox CONTENT_DELETED generado para contenido {}", id);
        } catch (Exception e) {
             log.error(
                "¡FALLO CRÍTICO DE OUTBOX! El contenido {} fue eliminado en MongoDB pero falló la creación de su OutboxEvent 'CONTENT_DELETED'. " +
                "El nodo en Neo4j NO se eliminará automáticamente. Error: {}",
                id, e.getMessage()
            );
        }
        // --- FIN LÓGICA OUTBOX ---
    }

    // Obtener por ID
    public Optional<Contenido> getContentById(String id) {
        return contenidoRepository.findById(id);
    }

    // Buscar por filtros opcionales (simple)
    public List<Contenido> findContents(String category, String creatorId) {
        if (category != null && creatorId != null) {
            return contenidoRepository.findByCategoriaAndCreadorId(category, creatorId);
        } else if (category != null) {
            return contenidoRepository.findByCategoria(category);
        } else if (creatorId != null) {
            return contenidoRepository.findByCreadorId(creatorId);
        } else {
            return contenidoRepository.findAll();
        }
    }

    /**
     * Busca contenidos con filtros paginados.
     * Modificado para usar 'type' en lugar de 'mediaType' para coincidir con el Controller.
     */
    public List<Contenido> findContentsWithFilters(String category, String creatorId, String type, String tag, int page, int size) {
        // TODO: Implementar esto con Criteria API o una @Query de Mongo para paginación
        // El filtrado en memoria es ineficiente para grandes volúmenes de datos.
        List<Contenido> allContents = contenidoRepository.findAll();

        return allContents.stream()
                .filter(c -> category == null || c.getCategoria().equalsIgnoreCase(category))
                .filter(c -> creatorId == null || c.getCreadorId().equalsIgnoreCase(creatorId))
                .filter(c -> type == null || (c.getTipo() != null && c.getTipo().equalsIgnoreCase(type)))
                .filter(c -> tag == null || (c.getEtiquetas() != null && c.getEtiquetas().contains(tag)))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    // Incrementar contador de vistas (placeholder)
    public void incrementViewCount(String contentId) {
        // Lógica para incrementar vistas (ej. con Redis o en un campo del documento)
        // String key = "view_count::" + contentId;
        // redisTemplate.opsForValue().increment(key);
    }

    // --- MÉTODOS MOCK ELIMINADOS ---
    // Los métodos getComments, addComment, reactToContent, startLive, endLive
    // y sus DTOs internos han sido eliminados, ya que el Controller
    // utiliza CommentService y LikeService dedicados.
}
