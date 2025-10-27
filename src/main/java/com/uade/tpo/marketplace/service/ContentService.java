package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.Contenido;
import com.uade.tpo.marketplace.repository.mongodb.ContenidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private final ContenidoRepository contenidoRepository;

    public ContentService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    /**
     * Guarda un nuevo contenido.
     * La lógica de mapeo de DTO y la asignación de fechaPublicacion
     * ahora se manejan en el ContentController.
     */
    public Contenido saveContent(Contenido content) {
        // La fecha de publicación ahora la asigna el controlador
        return contenidoRepository.save(content);
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
        contenidoRepository.deleteById(id);
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
