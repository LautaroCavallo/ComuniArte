package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.neo4j.SigueRelacion;
// Importa la entidad correcta de Neo4j
import com.uade.tpo.marketplace.entity.neo4j.UsuarioNeo4j; 
import com.uade.tpo.marketplace.repository.neo4j.UsuarioNeo4jRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
// Asegura que todas las transacciones de esta clase usan el gestor de Neo4j
@Transactional("neo4jTransactionManager") 
public class NetworkService {

    private final UsuarioNeo4jRepository usuarioNeo4jRepository;
    private final Neo4jClient neo4jClient;
    private final LogService logService;

    /**
     * Un usuario comienza a seguir a otro creador
     */
    @Transactional
    public void followUser(String followerMongoId, String creatorMongoId) {
        log.info("Usuario {} sigue a {}", followerMongoId, creatorMongoId);
        logService.logInfo("NEO4J", "NetworkService", "followUser", "FOLLOW_USER",
            "Usuario siguiendo a creador", followerMongoId, creatorMongoId, null);
        
        // Buscar o crear nodos de usuarios en Neo4j
        UsuarioNeo4j follower = findOrCreateUsuarioNode(followerMongoId, "Usuario " + followerMongoId);
        UsuarioNeo4j creator = findOrCreateUsuarioNode(creatorMongoId, "Usuario " + creatorMongoId);
        
        // Verificar si la relación ya existe (evitar duplicados)
        boolean yaExiste = follower.getSeguidos().stream()
                .anyMatch(rel -> rel.getSeguido().getMongoUserId().equals(creatorMongoId));
        
        if (yaExiste) {
            log.info("La relación SIGUE ya existe entre {} y {}", followerMongoId, creatorMongoId);
            return; // No crear duplicado
        }
        
        // Crear relación SIGUE
        SigueRelacion relacion = new SigueRelacion();
        relacion.setFechaInicio(LocalDateTime.now());
        relacion.setSeguido(creator);
        
        // Añadir la relación al set del seguidor y guardar
        follower.getSeguidos().add(relacion);
        usuarioNeo4jRepository.save(follower);
        
        logService.logNeo4jOperation("CREATE_RELATIONSHIP", followerMongoId, creatorMongoId, 0L, true);
        
        log.info("Relación SIGUE creada exitosamente");
    }

    /**
     * Un usuario deja de seguir a un creador
     */
    @Transactional
    public void unfollowUser(String followerMongoId, String creatorMongoId) {
        log.info("Usuario {} deja de seguir a {}", followerMongoId, creatorMongoId);
        
        // Carga el nodo del seguidor
        Optional<UsuarioNeo4j> followerOpt = usuarioNeo4jRepository.findByMongoUserId(followerMongoId);
        if (followerOpt.isEmpty()) {
            log.warn("Se intentó dejar de seguir desde un usuario que no existe en Neo4j: {}", followerMongoId);
            return;
        }

        UsuarioNeo4j follower = followerOpt.get();
        // Elimina la relación del Set si el ID del "seguido" coincide
        boolean removed = follower.getSeguidos().removeIf(relacion ->
            // Usa el método correcto: getMongoUserId()
            relacion.getSeguido().getMongoUserId().equals(creatorMongoId)
        );
        
        if (removed) {
            // Guarda la entidad follower actualizada (sin la relación)
            usuarioNeo4jRepository.save(follower);
            log.info("Relación SIGUE eliminada exitosamente");
        } else {
            log.info("No se encontró una relación SIGUE para eliminar entre {} y {}", followerMongoId, creatorMongoId);
        }
    }

    /**
     * Obtiene la lista de seguidores de un usuario
     * (Quién me sigue a mí)
     */
    public List<String> getFollowers(String mongoUserId) {
        log.info("Obteniendo seguidores de usuario {}", mongoUserId);
        
        List<UsuarioNeo4j> seguidores = usuarioNeo4jRepository.findSeguidores(mongoUserId);
        return seguidores.stream()
                // Usa el método correcto: getMongoUserId()
                .map(UsuarioNeo4j::getMongoUserId) 
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de creadores seguidos por un usuario
     * (A quién sigo yo)
     */
    public List<String> getFollowing(String mongoUserId) {
        log.info("Obteniendo seguidos de usuario {}", mongoUserId);
        
        List<UsuarioNeo4j> seguidos = usuarioNeo4jRepository.findSeguidos(mongoUserId);
        return seguidos.stream()
                // Usa el método correcto: getMongoUserId()
                .map(UsuarioNeo4j::getMongoUserId)
                .collect(Collectors.toList());
    }

    /**
     * Genera recomendaciones de contenidos según lo que les gusta a las personas que sigo
     * Algoritmo: Recomienda contenidos que les gustan a usuarios que sigo, pero que yo no he marcado como "me gusta"
     */
    public List<Map<String, Object>> getRecommendations(String mongoUserId) {
        log.info("Generando recomendaciones de contenidos para usuario {}", mongoUserId);
        
        String query = 
            "MATCH (yo:Usuario {mongoUserId: $userId})-[:SIGUE]->(seguido:Usuario)-[:GUSTA]->(c:Contenido) " +
            "WHERE NOT (yo)-[:GUSTA]->(c) " +
            "WITH c, count(*) AS popularidad " +
            "RETURN c.contenidoId AS contentId, c.titulo AS titulo, popularidad " +
            "ORDER BY popularidad DESC " +
            "LIMIT 10";
        
        // Usar Neo4jClient para mapear manualmente
        Collection<Map<String, Object>> results = neo4jClient
            .query(query)
            .bind(mongoUserId).to("userId")
            .fetch()
            .all()
            .stream()
            .map(record -> {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("contentId", record.get("contentId"));
                recommendation.put("titulo", record.get("titulo"));
                recommendation.put("popularidad", record.get("popularidad"));
                return recommendation;
            })
            .collect(Collectors.toList());
        
        log.info("Se generaron {} recomendaciones de contenidos", results.size());
        return new ArrayList<>(results);
    }
    
    /**
     * Genera recomendaciones de usuarios basadas en amigos de amigos
     */
    public List<String> getUserRecommendations(String mongoUserId) {
        log.info("Generando recomendaciones de usuarios para {}", mongoUserId);
        
        // Obtener usuarios seguidos por el usuario actual
        List<UsuarioNeo4j> seguidos = usuarioNeo4jRepository.findSeguidos(mongoUserId);
        Set<String> seguidosIds = seguidos.stream()
                                         .map(UsuarioNeo4j::getMongoUserId)
                                         .collect(Collectors.toSet());
        
        // Obtener usuarios seguidos por cada uno de los seguidos (amigos de amigos)
        Set<String> recomendaciones = new HashSet<>();
        for (UsuarioNeo4j seguido : seguidos) {
            List<UsuarioNeo4j> amigosDeAmigos = usuarioNeo4jRepository.findSeguidos(seguido.getMongoUserId());
            amigosDeAmigos.stream()
                    .map(UsuarioNeo4j::getMongoUserId)
                    .filter(id -> !id.equals(mongoUserId)) // Excluir al usuario actual
                    .filter(id -> !seguidosIds.contains(id)) // Excluir usuarios que ya sigo
                    .forEach(recomendaciones::add);
        }
        
        log.info("Se generaron {} recomendaciones de usuarios", recomendaciones.size());
        return new ArrayList<>(recomendaciones);
    }

    /**
     * Devuelve un grafo simplificado de relaciones para análisis social
     */
    public Object getNetworkGraph() {
        log.info("Generando grafo de red social");
        
        // Obtener todos los usuarios y sus relaciones
        List<UsuarioNeo4j> todosUsuarios = (List<UsuarioNeo4j>) usuarioNeo4jRepository.findAll();
        
        Map<String, Object> grafo = new HashMap<>();
        List<Map<String, String>> nodos = new ArrayList<>();
        List<Map<String, String>> enlaces = new ArrayList<>();
        
        for (UsuarioNeo4j usuario : todosUsuarios) {
            // Agregar nodo
            Map<String, String> nodo = new HashMap<>();
            // Usa el método correcto: getMongoUserId()
            nodo.put("id", usuario.getMongoUserId()); 
            nodo.put("nombre", usuario.getNombre());
            nodos.add(nodo);
            
            // Agregar enlaces (relaciones SIGUE)
            if (usuario.getSeguidos() != null) {
                for (SigueRelacion relacion : usuario.getSeguidos()) {
                    Map<String, String> enlace = new HashMap<>();
                    // Usa el método correcto: getMongoUserId()
                    enlace.put("source", usuario.getMongoUserId());
                    enlace.put("target", relacion.getSeguido().getMongoUserId());
                    enlace.put("type", "SIGUE");
                    enlaces.add(enlace);
                }
            }
        }
        
        grafo.put("nodes", nodos);
        grafo.put("links", enlaces);
        
        log.info("Grafo generado con {} nodos y {} enlaces", nodos.size(), enlaces.size());
        return grafo;
    }

    /**
     * Método auxiliar para buscar o crear un nodo de usuario en Neo4j
     * @param mongoUserId El ID del usuario en MongoDB
     * @param nombre (Opcional) El nombre a usar si el nodo se crea
     */
    private UsuarioNeo4j findOrCreateUsuarioNode(String mongoUserId, String nombre) {
        // Buscar por mongoUserId (el ID de MongoDB)
        return usuarioNeo4jRepository.findByMongoUserId(mongoUserId)
                .orElseGet(() -> {
                    // Si no existe, lo crea.
                    // El 'nombre' es un placeholder 
                    String nombreAMostrar = (nombre != null) ? nombre : "Usuario " + mongoUserId;
                    UsuarioNeo4j nuevoUsuario = new UsuarioNeo4j(mongoUserId, nombreAMostrar);
                    log.info("Creando nodo sombra en Neo4j para usuario: {}", mongoUserId);
                    return usuarioNeo4jRepository.save(nuevoUsuario);
                });
    }
}

