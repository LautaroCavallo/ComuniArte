package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transacciones")
public class Transaccion {

    @Id
    private String id;

    private String usuarioId; // Donante
    private String creadorId; // Receptor
    private String tipo; // Generalmente 'donacion'
    private Double monto;
    private LocalDateTime fecha;
    private String metodoPago;
}