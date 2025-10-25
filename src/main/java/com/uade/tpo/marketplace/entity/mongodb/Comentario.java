package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Document(collection = "comentarios")
public class Comentario {

    @Id
    private String id;

    private String contenidoId;
    private String usuarioId;
    private String texto;
    private LocalDateTime fecha;
    private boolean esLive;
}