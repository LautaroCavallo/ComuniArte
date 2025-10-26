package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.ListaPersonalizada;
import com.uade.tpo.marketplace.repository.mongodb.ListaPersonalizadaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de listas personalizadas de usuarios
 * Permite crear, modificar y gestionar colecciones de contenidos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserListService {

    private final ListaPersonalizadaRepository listaRepository;

    /**
     * Obtiene todas las listas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de listas personalizadas
     */
    public List<ListaPersonalizada> getUserLists(String usuarioId) {
        log.info("Obteniendo listas de usuario {}", usuarioId);
        return listaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene una lista específica por ID
     * Verifica que pertenezca al usuario
     * 
     * @param listId ID de la lista
     * @param usuarioId ID del usuario propietario
     * @return Lista personalizada
     */
    public ListaPersonalizada getListById(String listId, String usuarioId) {
        log.info("Obteniendo lista {} de usuario {}", listId, usuarioId);
        return listaRepository.findByIdAndUsuarioId(listId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Lista no encontrada o no pertenece al usuario"));
    }

    /**
     * Crea una nueva lista personalizada
     * @param usuarioId ID del usuario propietario
     * @param nombre Nombre de la lista
     * @param descripcion Descripción opcional
     * @param esPublica Si la lista es pública o privada
     * @return Lista creada
     */
    public ListaPersonalizada createList(String usuarioId, String nombre, String descripcion, boolean esPublica) {
        log.info("Creando lista '{}' para usuario {}", nombre, usuarioId);
        
        ListaPersonalizada lista = ListaPersonalizada.builder()
                .usuarioId(usuarioId)
                .nombre(nombre)
                .descripcion(descripcion)
                .esPublica(esPublica)
                .contenidosIds(new ArrayList<>())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        ListaPersonalizada saved = listaRepository.save(lista);
        log.info("Lista creada con ID {}", saved.getId());
        
        return saved;
    }

    /**
     * Agrega un contenido a una lista
     * @param listId ID de la lista
     * @param usuarioId ID del usuario (para verificación)
     * @param contenidoId ID del contenido a agregar
     * @return Lista actualizada
     */
    public ListaPersonalizada addContentToList(String listId, String usuarioId, String contenidoId) {
        log.info("Agregando contenido {} a lista {} de usuario {}", contenidoId, listId, usuarioId);
        
        ListaPersonalizada lista = getListById(listId, usuarioId);
        
        if (!lista.getContenidosIds().contains(contenidoId)) {
            lista.getContenidosIds().add(contenidoId);
            lista.setFechaActualizacion(LocalDateTime.now());
            
            ListaPersonalizada saved = listaRepository.save(lista);
            log.info("Contenido agregado exitosamente");
            return saved;
        } else {
            log.warn("El contenido {} ya está en la lista {}", contenidoId, listId);
            return lista;
        }
    }

    /**
     * Elimina un contenido de una lista
     * @param listId ID de la lista
     * @param usuarioId ID del usuario (para verificación)
     * @param contenidoId ID del contenido a eliminar
     * @return Lista actualizada
     */
    public ListaPersonalizada removeContentFromList(String listId, String usuarioId, String contenidoId) {
        log.info("Eliminando contenido {} de lista {} de usuario {}", contenidoId, listId, usuarioId);
        
        ListaPersonalizada lista = getListById(listId, usuarioId);
        
        if (lista.getContenidosIds().remove(contenidoId)) {
            lista.setFechaActualizacion(LocalDateTime.now());
            
            ListaPersonalizada saved = listaRepository.save(lista);
            log.info("Contenido eliminado exitosamente");
            return saved;
        } else {
            log.warn("El contenido {} no está en la lista {}", contenidoId, listId);
            return lista;
        }
    }

    /**
     * Actualiza información de una lista (nombre, descripción, privacidad)
     * @param listId ID de la lista
     * @param usuarioId ID del usuario (para verificación)
     * @param nombre Nuevo nombre (opcional)
     * @param descripcion Nueva descripción (opcional)
     * @param esPublica Nueva configuración de privacidad (opcional)
     * @return Lista actualizada
     */
    public ListaPersonalizada updateList(String listId, String usuarioId, 
                                        String nombre, String descripcion, Boolean esPublica) {
        log.info("Actualizando lista {} de usuario {}", listId, usuarioId);
        
        ListaPersonalizada lista = getListById(listId, usuarioId);
        
        boolean updated = false;
        
        if (nombre != null && !nombre.equals(lista.getNombre())) {
            lista.setNombre(nombre);
            updated = true;
        }
        
        if (descripcion != null && !descripcion.equals(lista.getDescripcion())) {
            lista.setDescripcion(descripcion);
            updated = true;
        }
        
        if (esPublica != null && esPublica != lista.isEsPublica()) {
            lista.setEsPublica(esPublica);
            updated = true;
        }
        
        if (updated) {
            lista.setFechaActualizacion(LocalDateTime.now());
            ListaPersonalizada saved = listaRepository.save(lista);
            log.info("Lista actualizada exitosamente");
            return saved;
        }
        
        return lista;
    }

    /**
     * Elimina una lista
     * @param listId ID de la lista
     * @param usuarioId ID del usuario (para verificación)
     */
    public void deleteList(String listId, String usuarioId) {
        log.info("Eliminando lista {} de usuario {}", listId, usuarioId);
        
        ListaPersonalizada lista = getListById(listId, usuarioId);
        listaRepository.delete(lista);
        
        log.info("Lista eliminada exitosamente");
    }

    /**
     * Verifica si un contenido está en alguna lista del usuario
     * @param usuarioId ID del usuario
     * @param contenidoId ID del contenido
     * @return Lista de nombres de listas que contienen el contenido
     */
    public List<String> getListsContainingContent(String usuarioId, String contenidoId) {
        log.info("Buscando listas que contienen contenido {} para usuario {}", contenidoId, usuarioId);
        
        List<ListaPersonalizada> listas = getUserLists(usuarioId);
        List<String> listNames = new ArrayList<>();
        
        for (ListaPersonalizada lista : listas) {
            if (lista.getContenidosIds().contains(contenidoId)) {
                listNames.add(lista.getNombre());
            }
        }
        
        return listNames;
    }

    /**
     * Obtiene listas públicas de la plataforma
     * @return Lista de listas públicas
     */
    public List<ListaPersonalizada> getPublicLists() {
        log.info("Obteniendo listas públicas");
        return listaRepository.findByEsPublica(true);
    }

    /**
     * Obtiene listas públicas de un usuario específico
     * @param usuarioId ID del usuario
     * @return Lista de listas públicas del usuario
     */
    public List<ListaPersonalizada> getUserPublicLists(String usuarioId) {
        log.info("Obteniendo listas públicas de usuario {}", usuarioId);
        return listaRepository.findByUsuarioIdAndEsPublica(usuarioId, true);
    }
}

