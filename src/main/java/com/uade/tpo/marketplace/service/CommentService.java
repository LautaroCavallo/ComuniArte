package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.Comentario;
import com.uade.tpo.marketplace.repository.mongodb.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de comentarios en contenidos
 * Almacena comentarios en MongoDB para persistencia
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final ComentarioRepository comentarioRepository;

    /**
     * Obtiene todos los comentarios de un contenido
     * @param contenidoId ID del contenido
     * @return Lista de comentarios
     */
    public List<Comentario> getCommentsByContentId(String contenidoId) {
        log.info("Obteniendo comentarios para contenido {}", contenidoId);
        return comentarioRepository.findByContenidoId(contenidoId);
    }

    /**
     * Agrega un nuevo comentario a un contenido
     * @param contenidoId ID del contenido
     * @param usuarioId ID del usuario que comenta
     * @param texto Texto del comentario
     * @param esLive Si es un comentario en vivo
     * @return Comentario creado
     */
    public Comentario addComment(String contenidoId, String usuarioId, String texto, boolean esLive) {
        log.info("Agregando comentario al contenido {} por usuario {}", contenidoId, usuarioId);
        
        Comentario comentario = new Comentario();
        comentario.setContenidoId(contenidoId);
        comentario.setUsuarioId(usuarioId);
        comentario.setTexto(texto);
        comentario.setFecha(LocalDateTime.now());
        comentario.setEsLive(esLive);
        
        Comentario saved = comentarioRepository.save(comentario);
        log.info("Comentario creado exitosamente con ID {}", saved.getId());
        
        return saved;
    }

    /**
     * Obtiene todos los comentarios de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de comentarios del usuario
     */
    public List<Comentario> getCommentsByUserId(String usuarioId) {
        log.info("Obteniendo comentarios del usuario {}", usuarioId);
        return comentarioRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene comentarios de transmisiones en vivo
     * @param esLive true para comentarios en vivo, false para regulares
     * @return Lista de comentarios
     */
    public List<Comentario> getCommentsByLiveStatus(boolean esLive) {
        log.info("Obteniendo comentarios con esLive={}", esLive);
        return comentarioRepository.findByEsLive(esLive);
    }

    /**
     * Elimina un comentario
     * @param comentarioId ID del comentario a eliminar
     */
    public void deleteComment(String comentarioId) {
        log.info("Eliminando comentario {}", comentarioId);
        comentarioRepository.deleteById(comentarioId);
    }

    /**
     * Cuenta los comentarios de un contenido
     * @param contenidoId ID del contenido
     * @return Número de comentarios
     */
    public long countCommentsByContentId(String contenidoId) {
        return comentarioRepository.findByContenidoId(contenidoId).size();
    }
}

