package com.uade.tpo.marketplace.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NetworkService {

    public void followUser(String followerId, String creatorId) {
        // TODO: implementar lógica
    }

    public void unfollowUser(String followerId, String creatorId) {
        // TODO: implementar lógica
    }

    public List<String> getFollowers(String id) {
        // TODO: retornar lista de seguidores
        return Collections.emptyList();
    }

    public List<String> getFollowing(String id) {
        // TODO: retornar lista de creadores seguidos
        return Collections.emptyList();
    }

    public List<String> getRecommendations(String id) {
        // TODO: generar recomendaciones
        return Collections.emptyList();
    }

    public Object getNetworkGraph() {
        // TODO: devolver grafo simplificado
        return null;
    }
}
