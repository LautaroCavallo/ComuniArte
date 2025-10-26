package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.mongodb.Usuario;
import com.uade.tpo.marketplace.repository.mongodb.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de perfiles de usuario
 * Maneja información personal, preferencias, historial de reproducción
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el perfil completo de un usuario
     * @param userId ID del usuario
     * @return Usuario con toda su información
     */
    public Optional<Usuario> getUserProfile(String userId) {
        log.info("Obteniendo perfil de usuario {}", userId);
        return usuarioRepository.findById(userId);
    }

    /**
     * Actualiza el perfil de un usuario
     * Solo actualiza campos no nulos
     * 
     * @param userId ID del usuario
     * @param updatedUser Datos a actualizar
     * @return Usuario actualizado
     */
    public Usuario updateUserProfile(String userId, Usuario updatedUser) {
        log.info("Actualizando perfil de usuario {}", userId);
        
        return usuarioRepository.findById(userId)
                .map(existingUser -> {
                    // Actualizar solo campos no nulos
                    if (updatedUser.getNombre() != null) {
                        existingUser.setNombre(updatedUser.getNombre());
                    }
                    if (updatedUser.getEmail() != null) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getRegion() != null) {
                        existingUser.setRegion(updatedUser.getRegion());
                    }
                    if (updatedUser.getIntereses() != null) {
                        existingUser.setIntereses(updatedUser.getIntereses());
                    }
                    if (updatedUser.getPerfilRedes() != null) {
                        existingUser.setPerfilRedes(updatedUser.getPerfilRedes());
                    }
                    if (updatedUser.getTipoUsuario() != null) {
                        existingUser.setTipoUsuario(updatedUser.getTipoUsuario());
                    }
                    if (updatedUser.getRol() != null) {
                        existingUser.setRol(updatedUser.getRol());
                    }
                    
                    Usuario saved = usuarioRepository.save(existingUser);
                    log.info("Perfil actualizado exitosamente");
                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));
    }

    /**
     * Obtiene el historial de reproducción de un usuario
     * @param userId ID del usuario
     * @return Lista de IDs de contenidos vistos
     */
    public List<String> getUserHistory(String userId) {
        log.info("Obteniendo historial de usuario {}", userId);
        
        return usuarioRepository.findById(userId)
                .map(user -> user.getHistorialReproduccion() != null 
                        ? user.getHistorialReproduccion() 
                        : new ArrayList<String>())
                .orElse(new ArrayList<>());
    }

    /**
     * Agrega un contenido al historial de reproducción
     * @param userId ID del usuario
     * @param contenidoId ID del contenido
     */
    public void addToHistory(String userId, String contenidoId) {
        log.info("Agregando contenido {} al historial de usuario {}", contenidoId, userId);
        
        usuarioRepository.findById(userId).ifPresent(user -> {
            if (user.getHistorialReproduccion() == null) {
                user.setHistorialReproduccion(new ArrayList<>());
            }
            
            // Evitar duplicados - eliminar si ya existe y agregar al final
            user.getHistorialReproduccion().remove(contenidoId);
            user.getHistorialReproduccion().add(contenidoId);
            
            // Limitar historial a últimos 100 elementos
            if (user.getHistorialReproduccion().size() > 100) {
                user.setHistorialReproduccion(
                    user.getHistorialReproduccion()
                        .subList(user.getHistorialReproduccion().size() - 100, 
                                user.getHistorialReproduccion().size())
                );
            }
            
            usuarioRepository.save(user);
            log.info("Historial actualizado");
        });
    }

    /**
     * Limpia el historial de reproducción de un usuario
     * @param userId ID del usuario
     */
    public void clearHistory(String userId) {
        log.info("Limpiando historial de usuario {}", userId);
        
        usuarioRepository.findById(userId).ifPresent(user -> {
            user.setHistorialReproduccion(new ArrayList<>());
            usuarioRepository.save(user);
        });
    }

    /**
     * Segmenta usuarios según criterios
     * @param tipoUsuario Tipo de usuario (espectador/creador)
     * @param region Región
     * @param interes Interés específico
     * @return Lista de usuarios que coinciden
     */
    public List<Usuario> segmentUsers(String tipoUsuario, String region, String interes) {
        log.info("Segmentando usuarios - tipo: {}, región: {}, interés: {}", 
                tipoUsuario, region, interes);
        
        List<Usuario> allUsers = usuarioRepository.findAll();
        
        return allUsers.stream()
                .filter(user -> tipoUsuario == null || tipoUsuario.equalsIgnoreCase(user.getTipoUsuario()))
                .filter(user -> region == null || region.equalsIgnoreCase(user.getRegion()))
                .filter(user -> interes == null || 
                        (user.getIntereses() != null && 
                         user.getIntereses().stream().anyMatch(i -> i.equalsIgnoreCase(interes))))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas básicas del usuario
     * @param userId ID del usuario
     * @return Mapa con estadísticas
     */
    public Map<String, Object> getUserStats(String userId) {
        log.info("Obteniendo estadísticas de usuario {}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        usuarioRepository.findById(userId).ifPresent(user -> {
            stats.put("nombre", user.getNombre());
            stats.put("tipoUsuario", user.getTipoUsuario());
            stats.put("region", user.getRegion());
            stats.put("intereses", user.getIntereses() != null ? user.getIntereses().size() : 0);
            stats.put("historialSize", user.getHistorialReproduccion() != null 
                    ? user.getHistorialReproduccion().size() : 0);
            stats.put("listasCount", user.getListasPersonalizadas() != null 
                    ? user.getListasPersonalizadas().size() : 0);
            stats.put("fechaRegistro", user.getFechaRegistro());
        });
        
        return stats;
    }

    /**
     * Elimina un usuario
     * @param userId ID del usuario
     */
    public void deleteUser(String userId) {
        log.info("Eliminando usuario {}", userId);
        usuarioRepository.deleteById(userId);
    }
}

