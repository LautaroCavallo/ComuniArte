package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "artistas")
public class Artista {

    @Id
    // Es el mismo ID del Usuario (si tipo_usuario = 'creador')
    private String id; 

    private String trayectoria;
    private String bio;
    private String enfoqueSocial;
    private String colectivoId; // ID de un colectivo si aplica
    
    // Referencias a los contenidos que creó (IDs)
    private List<String> contenidoCreado; 
}