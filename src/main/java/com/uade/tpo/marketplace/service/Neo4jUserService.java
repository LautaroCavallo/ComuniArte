package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.neo4j.UsuarioNeo4j;
import com.uade.tpo.marketplace.repository.neo4j.UsuarioNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import de Spring

@Service
// ¡ESTA ANOTACIÓN BUSCA EL BEAN!
// Asegúrate de que usa el nombre "neo4jTransactionManager"
@Transactional("neo4jTransactionManager") 
@RequiredArgsConstructor
public class Neo4jUserService {

    private final UsuarioNeo4jRepository neo4jRepository;

    public void createUsuarioNeo4j(String mongoId, String nombre) {
        createUsuarioNeo4j(mongoId, nombre, "ESPECTADOR"); // default
    }
    
    public void createUsuarioNeo4j(String mongoId, String nombre, String tipoUsuario) {
        if (neo4jRepository.findByMongoUserId(mongoId).isEmpty()) {
            UsuarioNeo4j neo4jNode = new UsuarioNeo4j(mongoId, nombre, tipoUsuario);
            neo4jRepository.save(neo4jNode);
        } else {
            // Si ya existe, actualizar el tipo de usuario (por si cambió)
            UsuarioNeo4j existing = neo4jRepository.findByMongoUserId(mongoId).get();
            existing.setTipoUsuario(tipoUsuario);
            neo4jRepository.save(existing);
        }
    }
}

