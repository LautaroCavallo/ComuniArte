package com.uade.tpo.marketplace.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.controllers.auth.AuthenticationRequest;
import com.uade.tpo.marketplace.controllers.auth.AuthenticationResponse;
import com.uade.tpo.marketplace.controllers.auth.RegisterRequest;
import com.uade.tpo.marketplace.controllers.config.JWTService;
import com.uade.tpo.marketplace.entity.Rol;
import com.uade.tpo.marketplace.entity.mongodb.Usuario;
import com.uade.tpo.marketplace.repository.mongodb.OutboxEvent;
import com.uade.tpo.marketplace.repository.mongodb.OutboxEventRepository;
import com.uade.tpo.marketplace.repository.mongodb.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OutboxEventRepository outboxEventRepository;

public AuthenticationResponse register(RegisterRequest request) {
        
        // 1. Verificar si el email ya existe en MongoDB
        repository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya registrado");
        });

        var fechaRegistro = Date.from(Instant.now());

        // 2. Construir la entidad de MongoDB
        var user = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaRegistro(fechaRegistro)
                .tipoUsuario("espectador")
                .rol(request.getRol() != null ? request.getRol() : Rol.ESPECTADOR)
                .build();

        // 3. Guardar en MongoDB (Esto se confirma/commit al instante)
        Usuario savedMongoUser = repository.save(user);

        // 4. Crear el evento Outbox (Mejor esfuerzo)
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", savedMongoUser.getId());
            payload.put("nombre", savedMongoUser.getNombre());
            payload.put("tipoUsuario", savedMongoUser.getRol().name()); // Agregar rol en MAYÚSCULAS

            var outboxEvent = OutboxEvent.builder()
                    .eventType("USER_REGISTERED")
                    .payload(payload)
                    .createdAt(Instant.now())
                    .processed(false)
                    .retryCount(0)
                    .build();
                    
            outboxEventRepository.save(outboxEvent); // Guardado separado

        } catch (Exception e) {
            // ¡MITIGACIÓN DE RIESGO!
            // Si esto falla, el usuario está creado pero el evento no.
            // Logueamos un error crítico para que un admin pueda arreglarlo manualmente.
            log.error(
                "¡FALLO CRÍTICO DE OUTBOX! El usuario {} fue creado en MongoDB pero falló la creación de su OutboxEvent. " +
                "El nodo en Neo4j NO se creará automáticamente. Error: {}",
                savedMongoUser.getId(), 
                e.getMessage()
            );
            // NOTA: NO lanzamos la excepción, porque el registro del usuario SÍ fue exitoso.
        }

        // 5. Generar token y respuesta
        var jwtToken = jwtService.generateToken(savedMongoUser);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(savedMongoUser.getId())
                .build();
}
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
                        
        // Mejorar la excepción para ser más específica que orElseThrow()
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + request.getEmail()));
                
        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .build();
    }

    public void deleteUser(AuthenticationRequest request) {
        // 1. Autenticar para asegurar que el usuario es quien dice ser
        authenticate(request); 
        
        // 2. Buscar el usuario
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado para eliminar."));
                
        // 3. Eliminar
        repository.delete(user);
    }
}