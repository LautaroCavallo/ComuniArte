package com.uade.tpo.marketplace.entity.redis;

import lombok.Data;
import java.io.Serializable;
import java.time.Instant;

@Data
public class LiveComentario implements Serializable {

    private String userId;
    private String texto;
    private Long timestamp; // Usar Instant.now().toEpochMilli()
    private String tipoEvento; // "comentario", "donacion", "pregunta"
}