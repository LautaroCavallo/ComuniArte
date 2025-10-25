package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.dto.ArtistaRequest;
import com.uade.tpo.marketplace.entity.mongodb.Artista;
import com.uade.tpo.marketplace.repository.mongodb.ArtistaRepository;

import java.util.List;

import org.springframework.stereotype.Service;
@Service
public class ArtistService {

    private final ArtistaRepository repository;

    public ArtistService(ArtistaRepository repository) {
        this.repository = repository;
    }

    // New method that accepts a DTO
    public Artista createArtist(ArtistaRequest dto) {
        // 1. Create the new Artista object
        Artista newArtist = new Artista();
        
        // 2. Map DTO fields to the entity fields
        // IMPORTANT: The Artista ID is the same as the User ID
        newArtist.setId(dto.getUserId()); 
        newArtist.setTrayectoria(dto.getTrayectoria());
        newArtist.setBio(dto.getBio());
        newArtist.setEnfoqueSocial(dto.getEnfoqueSocial());
        // Initialize lists to avoid null pointers
        newArtist.setContenidoCreado(List.of()); 

        // 3. Save the entity
        return repository.save(newArtist);
    }
}