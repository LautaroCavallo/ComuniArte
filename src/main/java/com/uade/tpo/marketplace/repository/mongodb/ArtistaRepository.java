package com.uade.tpo.marketplace.repository.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.Artista;

public interface ArtistaRepository extends MongoRepository<Artista, String> {
    List<Artista> findByTrayectoria(String trayectoria);
    List<Artista> findByColectivoId(String colectivoId);
    List<Artista> findByEnfoqueSocial(String enfoqueSocial);
}
