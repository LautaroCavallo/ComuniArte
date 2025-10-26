package com.uade.tpo.marketplace.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración de Redis para:
 * - Cache de contenidos y perfiles
 * - Contadores de vistas y likes en tiempo real
 * - Pub/Sub para eventos de live streaming
 * - Rankings de popularidad con Sorted Sets
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate para operaciones con String keys y String values
     * Usado para contadores, cache simple y Pub/Sub
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Serializers para keys y values
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedisTemplate para operaciones con objetos complejos
     * Usado para cache de entidades completas
     */
    @Bean
    public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Usar Jackson para serializar objetos
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Listener container para Pub/Sub
     * Permite suscribirse a canales de Redis para eventos en tiempo real
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * Topic para eventos de live streaming
     * Formato del canal: "live:events:{liveId}"
     */
    @Bean
    public ChannelTopic liveEventsTopic() {
        return new ChannelTopic("live:events:*");
    }

    /**
     * Topic para notificaciones generales
     */
    @Bean
    public ChannelTopic notificationsTopic() {
        return new ChannelTopic("notifications");
    }
}


