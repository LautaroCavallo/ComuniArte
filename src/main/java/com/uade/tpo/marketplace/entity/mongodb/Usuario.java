package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id; // Mapea a _id en MongoDB
    private String password;
    private String nombre;
    private String email;
    private String tipoUsuario; // 'espectador' o 'creador'
    private String region;
    private List<String> intereses;
    private Map<String, String> perfilRedes; // Ej: {"instagram": "url", "twitter": "url"}
    private Date fechaRegistro;
    // Referencias al Contenido (IDs) para el historial
    private List<String> historialReproduccion; 
    
    private List<String> listasPersonalizadas; // IDs o nombres de listas
}