package com.uade.tpo.comuniarte;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.uade.tpo.marketplace.controllers.auth.RegisterRequest;
import com.uade.tpo.marketplace.service.AuthenticationService;

@SpringBootTest
class AuthFlowTests {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    void register_user_smoke() {
        RegisterRequest req = RegisterRequest.builder()
                .nombre("Test User JUnit")
                .email("junit-user@comuniarte.com")
                .password("password123")
                .build();

        authenticationService.register(req);
    }
}


