package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Usuario;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
}
