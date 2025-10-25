package com.uade.tpo.marketplace.repository.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.Comentario;

public interface ComentarioRepository extends MongoRepository<Comentario, String> {
    List<Comentario> findByContenidoId(String contenidoId);
    List<Comentario> findByUsuarioId(String usuarioId);
    List<Comentario> findByEsLive(boolean esLive);
}
