package com.uade.tpo.marketplace.repository.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.Transaccion;

public interface TransaccionRepository extends MongoRepository<Transaccion, String> {
    List<Transaccion> findByUsuarioId(String usuarioId);
    List<Transaccion> findByCreadorId(String creadorId);
    List<Transaccion> findByTipo(String tipo);
}
