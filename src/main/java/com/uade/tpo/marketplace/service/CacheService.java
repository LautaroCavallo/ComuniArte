package com.uade.tpo.marketplace.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    public CacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Guardar en cache
    public void saveCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Obtener del cache
    public String getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Incrementar contador (views, likes, etc.)
    public Long incrementCounter(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // Obtener contador actual
    public Long getCounter(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.valueOf(value) : 0L;
    }
}
