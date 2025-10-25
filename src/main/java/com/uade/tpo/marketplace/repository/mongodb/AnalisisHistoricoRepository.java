package com.uade.tpo.marketplace.repository.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.uade.tpo.marketplace.entity.mongodb.AnalisisHistorico;

public interface AnalisisHistoricoRepository extends MongoRepository<AnalisisHistorico, String> {
    List<AnalisisHistorico> findByRegion(String region);
    List<AnalisisHistorico> findByFechaAnalisisBetween(String fechaInicio, String fechaFin);
}
