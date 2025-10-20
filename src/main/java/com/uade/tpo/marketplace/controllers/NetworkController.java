package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.service.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkService networkService;

    @PostMapping("/follow")
    public ResponseEntity<Void> followUser(@RequestParam String followerId, @RequestParam String creatorId) {
        networkService.followUser(followerId, creatorId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestParam String followerId, @RequestParam String creatorId) {
        networkService.unfollowUser(followerId, creatorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<?> getFollowers(@PathVariable String id) {
        return ResponseEntity.ok(networkService.getFollowers(id));
    }

    @GetMapping("/following/{id}")
    public ResponseEntity<?> getFollowing(@PathVariable String id) {
        return ResponseEntity.ok(networkService.getFollowing(id));
    }

    @GetMapping("/recommendations/{id}")
    public ResponseEntity<?> getRecommendations(@PathVariable String id) {
        return ResponseEntity.ok(networkService.getRecommendations(id));
    }

    @GetMapping("/graph")
    public ResponseEntity<?> getNetworkGraph() {
        return ResponseEntity.ok(networkService.getNetworkGraph());
    }
}
