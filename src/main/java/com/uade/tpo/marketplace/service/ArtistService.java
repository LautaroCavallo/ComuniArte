package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Artist;
import com.uade.tpo.marketplace.repository.ArtistRepository;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private final ArtistRepository repository;

    public ArtistService(ArtistRepository repository) {
        this.repository = repository;
    }

    public Artist createArtist(String name) {
        return repository.save(new Artist(name));
    }
}
