package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "analisis_historico")
public class AnalisisHistorico {

    @Id
    private String id;

    private String region;
    private LocalDate fechaAnalisis;
    
    // Mapa flexible para métricas (vistas totales, interacciones medias, etc.)
    private Map<String, Object> metricasResumidas; 
    
    private List<String> preferenciasCulturalesDetectadas; 
}