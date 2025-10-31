package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recomendaciones de contenido desde Neo4j
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRecommendation {
    private String contentId;
    private String titulo;
    private Long popularidad;
}

