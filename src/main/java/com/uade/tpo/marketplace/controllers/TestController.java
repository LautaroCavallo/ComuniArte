package com.uade.tpo.marketplace.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth")
    public Map<String, Object> testAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Authentication endpoint is accessible!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @org.springframework.web.bind.annotation.PostMapping("/echo")
    public Map<String, Object> echo(@org.springframework.web.bind.annotation.RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", body);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
