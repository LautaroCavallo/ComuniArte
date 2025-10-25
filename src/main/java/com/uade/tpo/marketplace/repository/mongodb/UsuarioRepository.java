package com.uade.tpo.marketplace.repository.mongodb;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNombre(String nombre);
}
