package com.uade.tpo.marketplace.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.uade.tpo.marketplace.entity.mongodb.Usuario;

public interface UserRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
}
