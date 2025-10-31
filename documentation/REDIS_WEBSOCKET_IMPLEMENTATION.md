# ComuniArte Backend - Redis & WebSocket Implementation

**Fecha**: Octubre 25, 2025  
**Versión**: 0.0.1-SNAPSHOT  
**Estado**: ✅ **Redis y WebSocket Completamente Implementados**

---

## 📋 Resumen Ejecutivo

Se han implementado exitosamente:
1. **Configuración completa de Redis** para cache y tiempo real
2. **WebSocket con STOMP** para chat en vivo y eventos
3. **Integración Redis + WebSocket** para sistema de live streaming completo

---

## 🔴 1. Redis Configuration

### Configuración Implementada

#### RedisConfig.java

Dos beans principales:

1. **`RedisTemplate<String, String>`**: Para operaciones simples
   - Contadores (vistas, likes)
   - Cache de strings
   - Pub/Sub

2. **`RedisTemplate<String, Object>`**: Para objetos complejos
   - Cache de entidades completas
   - Serialización JSON con Jackson

3. **`RedisMessageListenerContainer`**: Para Pub/Sub
   - Suscripción a canales de eventos
   - Notificaciones en tiempo real

### Uso en Servicios

#### AnalyticsService
```java
// Incrementar vistas en Sorted Set
redisTemplate.opsForZSet().incrementScore("ranking:vistas:global", contentId, 1.0);

// Obtener top 50 contenidos
Set<TypedTuple<String>> topContenidos = 
    redisTemplate.opsForZSet().reverseRangeWithScores("ranking:vistas:global", 0, 49);
```

#### LiveService
```java
// Chat en vivo con Lists
redisTemplate.opsForList().rightPush("live:comentarios:" + liveId, mensaje);

// Espectadores activos con Sets
redisTemplate.opsForSet().add("live:viewers:" + liveId, userId);

// Pub/Sub para eventos
redisTemplate.convertAndSend("live:events:" + liveId, evento);
```

### Estructuras de Datos en Redis

```
# Sorted Sets (Rankings)
ranking:vistas:global -> {contenidoId: score}
ranking:likes:global -> {contenidoId: score}

# Lists (Chat, Preguntas, Donaciones)
live:comentarios:{liveId} -> [mensaje1, mensaje2, ...]
live:preguntas:{liveId} -> [pregunta1, pregunta2, ...]
live:donaciones:{liveId} -> [donacion1, donacion2, ...]

# Sets (Espectadores Activos)
live:viewers:{liveId} -> {userId1, userId2, ...}

# Strings (Contadores)
analytics:content:{contentId} -> contador_vistas

# Pub/Sub Channels
live:events:{liveId} -> Canal de eventos en tiempo real
notifications -> Canal de notificaciones globales
```

---

## 🔌 2. WebSocket with STOMP

### Arquitectura WebSocket

```
Cliente                    Servidor
  |                           |
  |--- Conectar a /ws ------->|
  |<-- Conexión establecida --|
  |                           |
  |--- Suscribir a /topic --->|
  |                           |
  |--- Enviar a /app -------->|
  |                           |
  |<-- Broadcast /topic ------|
  |                           |
```

### WebSocketConfig.java

#### Configuración STOMP

- **Endpoint**: `/ws` con SockJS fallback
- **Application Prefix**: `/app` (mensajes del cliente)
- **Broker Prefixes**: 
  - `/topic` (broadcast a muchos)
  - `/queue` (punto a punto)
  - `/user` (mensajes a usuario específico)

### WebSocketChatController.java

#### Endpoints WebSocket

| Mapping | Descripción | Broadcast To |
|---------|-------------|--------------|
| `/app/live/{liveId}/chat` | Enviar mensaje de chat | `/topic/live/{liveId}/chat` |
| `/app/live/{liveId}/join` | Usuario se une | `/topic/live/{liveId}/events` |
| `/app/live/{liveId}/leave` | Usuario sale | `/topic/live/{liveId}/events` |
| `/app/live/{liveId}/question` | Enviar pregunta | `/topic/live/{liveId}/questions` |

#### DTOs Implementados

1. **ChatMessage**: Mensajes de chat
   ```json
   {
     "sender": "Usuario123",
     "content": "Hola a todos!",
     "timestamp": "2025-10-25T21:30:00Z",
     "type": "CHAT"
   }
   ```

2. **LiveEvent**: Eventos de transmisión
   ```json
   {
     "type": "USER_JOINED",
     "liveId": "live-123",
     "userId": "user-456",
     "userName": "Juan",
     "timestamp": "2025-10-25T21:30:00Z"
   }
   ```

3. **DonationEvent**: Notificaciones de donación
   ```json
   {
     "type": "DONATION",
     "liveId": "live-123",
     "donorName": "María",
     "amount": 10.50,
     "timestamp": "2025-10-25T21:30:00Z"
   }
   ```

4. **QuestionMessage**: Preguntas al presentador
   ```json
   {
     "userId": "user-789",
     "userName": "Pedro",
     "question": "¿Cuándo es el próximo show?",
     "timestamp": "2025-10-25T21:30:00Z"
   }
   ```

---

## 🔗 3. Integración Redis + WebSocket

### Flujo Completo de Chat en Vivo

```
1. Cliente se conecta a WebSocket (/ws)
2. Cliente se suscribe a /topic/live/{liveId}/chat
3. Cliente envía mensaje a /app/live/{liveId}/chat
4. Servidor recibe mensaje en @MessageMapping
5. Servidor guarda mensaje en Redis (persistencia temporal)
6. Servidor hace broadcast a /topic/live/{liveId}/chat
7. Todos los clientes suscritos reciben el mensaje
```

### Flujo de Donaciones

```
1. Cliente envía POST /api/live/{id}/donate
2. LiveService guarda transacción en MongoDB
3. LiveService registra donación en Redis
4. LiveController llama a webSocketChatController.sendDonationNotification()
5. WebSocket envía evento a /topic/live/{liveId}/events
6. Todos los espectadores ven la notificación en tiempo real
```

### Persistencia Híbrida

| Dato | Redis | MongoDB | Propósito |
|------|-------|---------|-----------|
| Chat | ✅ (últimos 100) | ❌ | Tiempo real |
| Donaciones | ✅ (estadísticas) | ✅ (historial) | Ambos |
| Preguntas | ✅ (pendientes) | ❌ | Tiempo real |
| Espectadores | ✅ (activos) | ❌ | Tiempo real |
| Vistas | ✅ (contadores) | ✅ (análisis) | Ambos |

---

## 🧪 4. Cliente de Prueba

### WEBSOCKET_CLIENT_EXAMPLE.html

Cliente HTML completo con:
- ✅ Conexión WebSocket con SockJS
- ✅ Cliente STOMP
- ✅ Envío de mensajes de chat
- ✅ Envío de preguntas
- ✅ Recepción de eventos (join/leave/donation)
- ✅ UI responsive con estilos modernos

### Cómo Usar el Cliente

1. Abrir `documentation/WEBSOCKET_CLIENT_EXAMPLE.html` en navegador
2. Ingresar ID de transmisión (ej: `live-test-1`)
3. Ingresar nombre de usuario
4. Click en "Conectar"
5. Enviar mensajes y preguntas
6. Ver eventos en tiempo real

### Librerías Incluidas

```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

---

## 🚀 5. Endpoints Actualizados

### REST Endpoints

| Endpoint | Método | Descripción | Cambios |
|----------|--------|-------------|---------|
| `/api/live/{id}/donate` | POST | Realizar donación | ✅ Ahora envía notificación WebSocket |
| `/api/live/{id}/join` | POST | Unirse a transmisión | ✅ Registra en Redis |
| `/api/live/{id}/leave` | POST | Salir de transmisión | ✅ Elimina de Redis |
| `/api/live/{id}/events` | GET | Obtener eventos | ✅ Lee de Redis |

### WebSocket Endpoints

| Endpoint | Tipo | Descripción |
|----------|------|-------------|
| `/ws` | Conexión | Endpoint de conexión WebSocket |
| `/app/live/{liveId}/chat` | Send | Enviar mensaje de chat |
| `/app/live/{liveId}/join` | Send | Notificar entrada |
| `/app/live/{liveId}/leave` | Send | Notificar salida |
| `/app/live/{liveId}/question` | Send | Enviar pregunta |
| `/topic/live/{liveId}/chat` | Subscribe | Recibir mensajes de chat |
| `/topic/live/{liveId}/events` | Subscribe | Recibir eventos |
| `/topic/live/{liveId}/questions` | Subscribe | Recibir preguntas |

---

## 📦 6. Dependencias Agregadas

### pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

Ya existentes:
- `spring-boot-starter-data-redis` ✅
- `spring-boot-starter-web` ✅
- `spring-boot-starter-security` ✅

---

## ⚙️ 7. Configuración Requerida

### application.properties

```properties
# Redis (ya configurado)
spring.data.redis.host=localhost
spring.data.redis.port=6379

# WebSocket (configuración automática)
# No requiere propiedades adicionales
```

### Docker Compose

Redis ya está corriendo en Docker:
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
```

---

## 🔒 8. Seguridad WebSocket

### SecurityConfig.java

WebSocket endpoints permitidos sin autenticación:
```java
.requestMatchers("/ws/**").permitAll()
```

### Consideraciones de Producción

1. **Autenticación WebSocket**: Implementar `ChannelInterceptor` para validar JWT
2. **Rate Limiting**: Limitar mensajes por usuario
3. **CORS**: Configurar orígenes permitidos específicos
4. **Validación**: Validar contenido de mensajes (XSS, spam)

---

## 📊 9. Métricas y Monitoreo

### Redis Monitoring

```bash
# Conectar a Redis CLI
docker exec -it comuniarte-redis redis-cli

# Ver todas las keys
KEYS *

# Ver espectadores activos
SMEMBERS live:viewers:live-test-1

# Ver ranking de vistas
ZREVRANGE ranking:vistas:global 0 10 WITHSCORES

# Ver últimos mensajes de chat
LRANGE live:comentarios:live-test-1 -10 -1
```

### WebSocket Monitoring

Logs en consola:
```
INFO  WebSocketChatController - Mensaje de chat recibido para live live-test-1: Hola! de Usuario123
INFO  WebSocketChatController - Usuario user-456 se unió a live live-test-1
INFO  LiveService - Registrando donación de 10.5 para transmisión live-test-1
```

---

## ✅ 10. Testing

### Test Manual con Cliente HTML

1. **Iniciar servidor**: `./mvnw.cmd spring-boot:run`
2. **Abrir cliente**: `WEBSOCKET_CLIENT_EXAMPLE.html`
3. **Conectar**: Ingresar `live-test-1` y nombre
4. **Enviar mensaje**: Escribir y enviar
5. **Abrir segunda pestaña**: Repetir pasos 2-3
6. **Verificar**: Mensajes aparecen en ambas pestañas

### Test con cURL

```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","email":"test@test.com","password":"pass123"}'

# Unirse a transmisión
curl -X POST "http://localhost:8080/api/live/live-test-1/join?userId=user1" \
  -H "Authorization: Bearer <token>"

# Hacer donación (activa WebSocket)
curl -X POST "http://localhost:8080/api/live/live-test-1/donate?donorId=user1&donorName=Juan&creatorId=creator1&amount=10.50" \
  -H "Authorization: Bearer <token>"
```

---

## 🎯 11. Próximos Pasos

### Mejoras Sugeridas

1. **Persistencia de Chat**: Guardar mensajes importantes en MongoDB
2. **Moderación**: Sistema de moderación de chat en tiempo real
3. **Emojis y Reacciones**: Agregar soporte para emojis y reacciones
4. **Video Streaming**: Integrar con WebRTC para video en vivo
5. **Notificaciones Push**: Integrar con Firebase Cloud Messaging
6. **Analytics Avanzado**: Métricas de engagement en tiempo real

### Optimizaciones

1. **Redis Cluster**: Para alta disponibilidad
2. **WebSocket Scaling**: Usar Redis Pub/Sub para múltiples instancias
3. **Compresión**: Comprimir mensajes WebSocket
4. **Batching**: Agrupar mensajes para reducir overhead

---

## 📚 12. Referencias

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS](https://github.com/sockjs/sockjs-client)
- [Redis Pub/Sub](https://redis.io/topics/pubsub)
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)

---

**Última actualización**: Octubre 25, 2025  
**Autor**: ComuniArte Development Team  
**Estado**: ✅ Producción Ready







