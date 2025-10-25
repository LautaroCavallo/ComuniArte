package com.uade.tpo.marketplace.entity.dto;
// Archivo: com.uade.tpo.marketplace.dto.ArtistCreationDTO.java
import lombok.Data;

@Data
public class ArtistaRequest {
    private String userId; // Needed to set the ID in the Artista entity
    private String trayectoria;
    private String bio;
    private String enfoqueSocial;
    // Add other fields needed for creation
}