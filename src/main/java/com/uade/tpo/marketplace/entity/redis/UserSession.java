package com.uade.tpo.marketplace.entity.redis;
import lombok.Data;
import java.io.Serializable;
import java.time.Instant;

@Data
public class UserSession implements Serializable {

    private String userId;
    private String estadoOnline; // "online", "away", "live_viewing"
    private Instant lastSeen;
}