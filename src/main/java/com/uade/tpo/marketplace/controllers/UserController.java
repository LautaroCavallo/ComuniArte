package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.mongodb.ListaPersonalizada;
import com.uade.tpo.marketplace.entity.mongodb.Usuario;
import com.uade.tpo.marketplace.service.UserListService;
import com.uade.tpo.marketplace.service.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de usuarios
 * Endpoints para perfiles, historial, listas personalizadas y segmentación
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserListService userListService;

    // ========== PERFIL DE USUARIO ==========

    /**
     * GET /api/users/{id}
     * Obtiene el perfil completo de un usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserProfile(@PathVariable String id) {
        return userProfileService.getUserProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/users/{id}
     * Actualiza el perfil de un usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUserProfile(
            @PathVariable String id,
            @RequestBody Usuario updatedUser) {
        try {
            Usuario updated = userProfileService.updateUserProfile(id, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un usuario (solo admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userProfileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users/{id}/stats
     * Obtiene estadísticas del usuario
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
        Map<String, Object> stats = userProfileService.getUserStats(id);
        if (stats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    // ========== HISTORIAL DE REPRODUCCIÓN ==========

    /**
     * GET /api/users/{id}/history
     * Obtiene el historial de reproducción del usuario
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<String>> getUserHistory(@PathVariable String id) {
        List<String> history = userProfileService.getUserHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * POST /api/users/{id}/history
     * Agrega un contenido al historial
     */
    @PostMapping("/{id}/history")
    public ResponseEntity<Void> addToHistory(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String contenidoId = body.get("contenidoId");
        if (contenidoId == null) {
            return ResponseEntity.badRequest().build();
        }
        userProfileService.addToHistory(id, contenidoId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/users/{id}/history
     * Limpia el historial de reproducción
     */
    @DeleteMapping("/{id}/history")
    public ResponseEntity<Void> clearHistory(@PathVariable String id) {
        userProfileService.clearHistory(id);
        return ResponseEntity.noContent().build();
    }

    // ========== LISTAS PERSONALIZADAS ==========

    /**
     * GET /api/users/{id}/lists
     * Obtiene todas las listas personalizadas del usuario
     */
    @GetMapping("/{id}/lists")
    public ResponseEntity<List<ListaPersonalizada>> getUserLists(@PathVariable String id) {
        List<ListaPersonalizada> lists = userListService.getUserLists(id);
        return ResponseEntity.ok(lists);
    }

    /**
     * GET /api/users/{id}/lists/{listId}
     * Obtiene una lista específica
     */
    @GetMapping("/{id}/lists/{listId}")
    public ResponseEntity<ListaPersonalizada> getListById(
            @PathVariable String id,
            @PathVariable String listId) {
        try {
            ListaPersonalizada lista = userListService.getListById(listId, id);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/users/{id}/lists
     * Crea una nueva lista personalizada
     */
    @PostMapping("/{id}/lists")
    public ResponseEntity<ListaPersonalizada> createList(
            @PathVariable String id,
            @RequestBody CreateListRequest request) {
        ListaPersonalizada lista = userListService.createList(
                id,
                request.getNombre(),
                request.getDescripcion(),
                request.isEsPublica()
        );
        return ResponseEntity.ok(lista);
    }

    /**
     * PUT /api/users/{id}/lists/{listId}
     * Actualiza una lista (agrega/elimina contenido o actualiza metadatos)
     */
    @PutMapping("/{id}/lists/{listId}")
    public ResponseEntity<ListaPersonalizada> updateList(
            @PathVariable String id,
            @PathVariable String listId,
            @RequestBody UpdateListRequest request) {
        try {
            ListaPersonalizada lista;
            
            // Si se especifica una acción de agregar/eliminar contenido
            if (request.getAccion() != null) {
                switch (request.getAccion().toLowerCase()) {
                    case "add":
                        if (request.getContenidoId() == null) {
                            return ResponseEntity.badRequest().build();
                        }
                        lista = userListService.addContentToList(listId, id, request.getContenidoId());
                        break;
                    case "remove":
                        if (request.getContenidoId() == null) {
                            return ResponseEntity.badRequest().build();
                        }
                        lista = userListService.removeContentFromList(listId, id, request.getContenidoId());
                        break;
                    default:
                        return ResponseEntity.badRequest().build();
                }
            } else {
                // Actualizar metadatos de la lista
                lista = userListService.updateList(
                        listId,
                        id,
                        request.getNombre(),
                        request.getDescripcion(),
                        request.getEsPublica()
                );
            }
            
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/users/{id}/lists/{listId}
     * Elimina una lista personalizada
     */
    @DeleteMapping("/{id}/lists/{listId}")
    public ResponseEntity<Void> deleteList(
            @PathVariable String id,
            @PathVariable String listId) {
        try {
            userListService.deleteList(listId, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/users/{id}/lists/contains/{contenidoId}
     * Verifica en qué listas está un contenido específico
     */
    @GetMapping("/{id}/lists/contains/{contenidoId}")
    public ResponseEntity<List<String>> getListsContainingContent(
            @PathVariable String id,
            @PathVariable String contenidoId) {
        List<String> listNames = userListService.getListsContainingContent(id, contenidoId);
        return ResponseEntity.ok(listNames);
    }

    // ========== LISTAS PÚBLICAS ==========

    /**
     * GET /api/users/lists/public
     * Obtiene todas las listas públicas de la plataforma
     */
    @GetMapping("/lists/public")
    public ResponseEntity<List<ListaPersonalizada>> getPublicLists() {
        List<ListaPersonalizada> lists = userListService.getPublicLists();
        return ResponseEntity.ok(lists);
    }

    /**
     * GET /api/users/{id}/lists/public
     * Obtiene las listas públicas de un usuario específico
     */
    @GetMapping("/{id}/lists/public")
    public ResponseEntity<List<ListaPersonalizada>> getUserPublicLists(@PathVariable String id) {
        List<ListaPersonalizada> lists = userListService.getUserPublicLists(id);
        return ResponseEntity.ok(lists);
    }

    // ========== SEGMENTACIÓN DE USUARIOS ==========

    /**
     * GET /api/users/segment
     * Segmenta usuarios según criterios (tipo, región, intereses)
     */
    @GetMapping("/segment")
    public ResponseEntity<List<Usuario>> segmentUsers(
            @RequestParam(required = false) String tipoUsuario,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String interes) {
        List<Usuario> users = userProfileService.segmentUsers(tipoUsuario, region, interes);
        return ResponseEntity.ok(users);
    }

    // ========== DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateListRequest {
        private String nombre;
        private String descripcion;
        private boolean esPublica;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateListRequest {
        private String accion; // "add" o "remove" para gestionar contenidos
        private String contenidoId; // ID del contenido a agregar/eliminar
        private String nombre; // Para actualizar el nombre de la lista
        private String descripcion; // Para actualizar la descripción
        private Boolean esPublica; // Para cambiar privacidad
    }
}

