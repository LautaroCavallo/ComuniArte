package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.neo4j.SigueRelacion;
import com.uade.tpo.marketplace.entity.neo4j.Usuario;
import com.uade.tpo.marketplace.repository.neo4j.UsuarioNeo4jRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetworkService {

    private final UsuarioNeo4jRepository usuarioNeo4jRepository;

    /**
     * Un usuario comienza a seguir a otro creador
     */
    @Transactional
    public void followUser(String followerId, String creatorId) {
        log.info("Usuario {} sigue a {}", followerId, creatorId);
        
        // Buscar o crear nodos de usuarios en Neo4j
        Usuario follower = findOrCreateUsuarioNode(followerId);
        Usuario creator = findOrCreateUsuarioNode(creatorId);
        
        // Crear relación SIGUE
        SigueRelacion relacion = new SigueRelacion();
        relacion.setFechaInicio(LocalDateTime.now());
        relacion.setSeguido(creator);
        
        follower.getSeguidos().add(relacion);
        usuarioNeo4jRepository.save(follower);
        
        log.info("Relación SIGUE creada exitosamente");
    }

    /**
     * Un usuario deja de seguir a un creador
     */
    @Transactional
    public void unfollowUser(String followerId, String creatorId) {
        log.info("Usuario {} deja de seguir a {}", followerId, creatorId);
        
        // Ejecutar query personalizada para eliminar la relación
        usuarioNeo4jRepository.findSeguidos(followerId).stream()
                .filter(u -> u.getUserId().equals(creatorId))
                .findFirst()
                .ifPresent(creator -> {
                    Usuario follower = findOrCreateUsuarioNode(followerId);
                    follower.getSeguidos().removeIf(rel -> 
                        rel.getSeguido().getUserId().equals(creatorId));
                    usuarioNeo4jRepository.save(follower);
                });
        
        log.info("Relación SIGUE eliminada exitosamente");
    }

    /**
     * Obtiene la lista de seguidores de un usuario
     */
    public List<String> getFollowers(String userId) {
        log.info("Obteniendo seguidores de usuario {}", userId);
        
        List<Usuario> seguidores = usuarioNeo4jRepository.findSeguidores(userId);
        return seguidores.stream()
                .map(Usuario::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de creadores seguidos por un usuario
     */
    public List<String> getFollowing(String userId) {
        log.info("Obteniendo seguidos de usuario {}", userId);
        
        List<Usuario> seguidos = usuarioNeo4jRepository.findSeguidos(userId);
        return seguidos.stream()
                .map(Usuario::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * Genera recomendaciones de creadores o contenidos según cercanía de red
     * Algoritmo: Recomienda usuarios seguidos por los usuarios que yo sigo (amigos de amigos)
     */
    public List<String> getRecommendations(String userId) {
        log.info("Generando recomendaciones para usuario {}", userId);
        
        // Obtener usuarios seguidos por el usuario actual
        List<Usuario> seguidos = usuarioNeo4jRepository.findSeguidos(userId);
        
        // Obtener usuarios seguidos por cada uno de los seguidos (amigos de amigos)
        Set<String> recomendaciones = new HashSet<>();
        for (Usuario seguido : seguidos) {
            List<Usuario> amigosDeAmigos = usuarioNeo4jRepository.findSeguidos(seguido.getUserId());
            amigosDeAmigos.stream()
                    .map(Usuario::getUserId)
                    .filter(id -> !id.equals(userId)) // Excluir al usuario actual
                    .filter(id -> seguidos.stream().noneMatch(s -> s.getUserId().equals(id))) // Excluir ya seguidos
                    .forEach(recomendaciones::add);
        }
        
        log.info("Se generaron {} recomendaciones", recomendaciones.size());
        return new ArrayList<>(recomendaciones);
    }

    /**
     * Devuelve un grafo simplificado de relaciones para análisis social
     */
    public Object getNetworkGraph() {
        log.info("Generando grafo de red social");
        
        // Obtener todos los usuarios y sus relaciones
        List<Usuario> todosUsuarios = (List<Usuario>) usuarioNeo4jRepository.findAll();
        
        Map<String, Object> grafo = new HashMap<>();
        List<Map<String, String>> nodos = new ArrayList<>();
        List<Map<String, String>> enlaces = new ArrayList<>();
        
        for (Usuario usuario : todosUsuarios) {
            // Agregar nodo
            Map<String, String> nodo = new HashMap<>();
            nodo.put("id", usuario.getUserId());
            nodo.put("nombre", usuario.getNombre());
            nodos.add(nodo);
            
            // Agregar enlaces (relaciones SIGUE)
            for (SigueRelacion relacion : usuario.getSeguidos()) {
                Map<String, String> enlace = new HashMap<>();
                enlace.put("source", usuario.getUserId());
                enlace.put("target", relacion.getSeguido().getUserId());
                enlace.put("type", "SIGUE");
                enlaces.add(enlace);
            }
        }
        
        grafo.put("nodes", nodos);
        grafo.put("links", enlaces);
        
        log.info("Grafo generado con {} nodos y {} enlaces", nodos.size(), enlaces.size());
        return grafo;
    }

    /**
     * Método auxiliar para buscar o crear un nodo de usuario en Neo4j
     */
    private Usuario findOrCreateUsuarioNode(String userId) {
        // Buscar por userId (el ID de MongoDB)
        return usuarioNeo4jRepository.findAll().stream()
                .filter(u -> u.getUserId() != null && u.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setUserId(userId);
                    nuevoUsuario.setNombre("Usuario " + userId); // Placeholder
                    return usuarioNeo4jRepository.save(nuevoUsuario);
                });
    }
}
