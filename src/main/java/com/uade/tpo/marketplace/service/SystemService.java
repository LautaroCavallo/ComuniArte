package com.uade.tpo.marketplace.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SystemService {

    public Object getSystemStatus() {
        // TODO: estado general de servicios
        return null;
    }

    public Object getSystemConfig() {
        // TODO: devolver configuraciones
        return null;
    }

    public List<String> getActiveSessions() {
        // TODO: listar sesiones activas
        return Collections.emptyList();
    }

    public List<String> getSystemLogs() {
        // TODO: consultar logs del sistema
        return Collections.emptyList();
    }

    public Object getSystemMetrics() {
        // TODO: métricas técnicas (CPU, memoria, red)
        return null;
    }
}
