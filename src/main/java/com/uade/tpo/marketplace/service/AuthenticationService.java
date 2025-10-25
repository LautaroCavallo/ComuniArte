package com.uade.tpo.marketplace.service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

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
import com.uade.tpo.marketplace.repository.mongodb.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya registrado");
        });

        var fechaRegistro = Date.from(Instant.now());

        var user = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaRegistro(fechaRegistro)
                .tipoUsuario("espectador")
                .rol("ESPECTADOR")
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
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