package com.uade.tpo.marketplace.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "transacciones")
public class Transaccion {

    @Id
    private String id;

    private String usuarioId; // Donante
    private String creadorId; // Receptor
    private String tipo; // Generalmente 'donacion'
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String metodoPago;
}