package com.uade.tpo.marketplace.repository.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.Contenido;

public interface ContenidoRepository extends MongoRepository<Contenido, String> {
    List<Contenido> findByCategoria(String categoria);
    List<Contenido> findByCreadorId(String creadorId);
    List<Contenido> findByCategoriaAndCreadorId(String categoria, String creadorId);
    List<Contenido> findByTipo(String tipo);
    List<Contenido> findByEtiquetasContaining(String etiqueta);
}
