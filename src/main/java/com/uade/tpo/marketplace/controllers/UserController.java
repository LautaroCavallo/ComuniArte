package com.uade.tpo.marketplace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.auth.AuthenticationRequest;
import com.uade.tpo.marketplace.controllers.auth.AuthenticationResponse;
import com.uade.tpo.marketplace.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService service;

    @DeleteMapping("/usuario/delete")
    public ResponseEntity<AuthenticationResponse> deleteUser(@RequestBody AuthenticationRequest request) {
        service.deleteUser(request); // Llama al método del servicio
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}