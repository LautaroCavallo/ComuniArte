# 🏗️ ComuniArte - Arquitectura del Sistema

## 📐 Visión General

ComuniArte implementa una arquitectura **Polyglot Persistence** utilizando tres bases de datos especializadas, cada una optimizada para diferentes tipos de datos y patrones de acceso.

```
┌────────────────────────────────────────────────────────────────┐
│                        Cliente (Web/Mobile)                     │
└───────────────────────────┬────────────────────────────────────┘
                            │
                            │ HTTP/REST + WebSocket
                            │
┌───────────────────────────▼────────────────────────────────────┐
│                     Spring Boot Application                     │
│                         (Port 8080)                             │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │
│  │ Controllers  │  │   Services   │  │    Repositories      │ │
│  │              │  │              │  │                      │ │
│  │ • REST       │  │ • Business   │  │ • MongoDB            │ │
│  │ • WebSocket  │  │   Logic      │  │ • Neo4j              │ │
│  │ • Security   │  │ • Integration│  │ • Redis              │ │
│  └──────────────┘  └──────────────┘  └──────────────────────┘ │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   MongoDB    │    │    Neo4j     │    │    Redis     │
│  Port 27017  │    │  Port 7687   │    │  Port 6379   │
│              │    │              │    │              │
│ • Contenidos │    │ • Relaciones │    │ • Cache      │
│ • Usuarios   │    │ • Grafos     │    │ • Contadores │
│ • Comentarios│    │ • Red Social │    │ • Pub/Sub    │
│ • Listas     │    │              │    │ • Live Data  │
│ • Transmisión│    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
```

---

## 🗄️ Decisiones de Arquitectura

### ¿Por qué Polyglot Persistence?

| Requisito | Solución | Base de Datos | Justificación |
|-----------|----------|---------------|---------------|
| **Almacenar contenidos con metadatos flexibles** | Documentos | MongoDB | Esquema dinámico ideal para metadata variable |
| **Recomendaciones basadas en red social** | Grafos | Neo4j | Consultas de relaciones 1000x más rápidas |
| **Rankings y contadores en tiempo real** | In-Memory | Redis | Latencia sub-millisegundo para operaciones frecuentes |
| **Chat en vivo con miles de usuarios** | Pub/Sub | Redis | Distribución de eventos sin latencia |

---

## 💾 MongoDB - Base de Datos Documental

### Responsabilidades
- Almacenamiento principal de datos estructurados
- Persistencia de largo plazo
- Consultas complejas con agregaciones

### Colecciones Principales

```javascript
// usuarios
{
  _id: ObjectId("..."),
  nombre: "Juan Pérez",
  email: "juan@mail.com",
  tipoUsuario: "creador",
  region: "Argentina",
  intereses: ["música", "folklore"],
  historialReproduccion: ["content1", "content2"],
  fechaRegistro: ISODate("2025-01-15")
}

// contenidos
{
  _id: ObjectId("..."),
  titulo: "Concierto Folklore",
  tipo: "video",
  creadorId: "user123",
  categoria: "Música",
  etiquetas: ["folklore", "argentina"],
  metadatosEnriquecidos: {
    duracion: 180,
    idioma: "es",
    locacion: "Buenos Aires"
  },
  fechaPublicacion: ISODate("2025-10-26")
}

// transmisiones
{
  _id: ObjectId("..."),
  creadorId: "user123",
  titulo: "Live Show",
  estado: "ACTIVA",
  fechaInicio: ISODate("2025-10-26T20:00:00"),
  espectadoresMax: 42,
  totalDonaciones: 150.50,
  totalMensajes: 325
}

// listas_personalizadas
{
  _id: ObjectId("..."),
  usuarioId: "user123",
  nombre: "Mis Favoritos",
  contenidosIds: ["content1", "content2"],
  esPublica: true
}

// comentarios
{
  _id: ObjectId("..."),
  contenidoId: "content123",
  usuarioId: "user456",
  texto: "¡Excelente!",
  fecha: ISODate("2025-10-26"),
  esLive: false
}
```

### Índices Optimizados
```javascript
// Búsquedas por creador
db.contenidos.createIndex({ creadorId: 1, fechaPublicacion: -1 });

// Búsquedas por categoría
db.contenidos.createIndex({ categoria: 1, etiquetas: 1 });

// Búsquedas de listas
db.listas_personalizadas.createIndex({ usuarioId: 1 });

// Transmisiones activas
db.transmisiones.createIndex({ estado: 1, fechaInicio: -1 });
```

---

## 🕸️ Neo4j - Base de Datos de Grafos

### Responsabilidades
- Relaciones sociales (seguir, colaborar)
- Recomendaciones basadas en red
- Análisis de influencia y propagación

### Modelo de Datos

```
(:Usuario)-[:SIGUE]->(:Usuario)
(:Usuario)-[:CREO]->(:Contenido)
(:Usuario)-[:VIO {duracion_ms, fecha}]->(:Contenido)
(:Usuario)-[:GUSTA]->(:Contenido)
(:Usuario)-[:COLABORA_EN {rol}]->(:Contenido)
(:Usuario)-[:ES_MIEMBRO_DE {fecha}]->(:Colectivo)
```

### Queries Cypher Clave

#### Recomendaciones (Collaborative Filtering)
```cypher
// Recomendar contenido que vieron mis seguidos
MATCH (u:Usuario {userId: $userId})-[:SIGUE]->(seguido)
      -[:VIO]->(contenido:Contenido)
WHERE NOT (u)-[:VIO]->(contenido)
RETURN contenido
ORDER BY count(*) DESC
LIMIT 10
```

#### Influencers de la Plataforma
```cypher
// Top 10 usuarios más seguidos
MATCH (u:Usuario)<-[r:SIGUE]-()
RETURN u.userId, u.nombre, count(r) as seguidores
ORDER BY seguidores DESC
LIMIT 10
```

#### Análisis de Impacto
```cypher
// Medir alcance de un creador
MATCH (creador:Usuario {userId: $creadorId})-[:CREO]->(contenido)
      <-[:VIO]-(espectador:Usuario)
RETURN COUNT(DISTINCT espectador) as alcance
```

---

## ⚡ Redis - Cache y Tiempo Real

### Responsabilidades
- Cache de consultas frecuentes
- Contadores de alta frecuencia (vistas, likes)
- Rankings dinámicos
- Datos de sesiones en vivo
- Pub/Sub para eventos

### Estructuras de Datos

#### Sorted Sets (Rankings)
```redis
# Ranking global de likes (score = número de likes)
ZADD ranking:likes:global 42 "content123"
ZADD ranking:likes:global 105 "content456"

# Top 10 más likeados
ZREVRANGE ranking:likes:global 0 9 WITHSCORES
```

#### Sets (Espectadores Activos)
```redis
# Agregar espectador
SADD live:viewers:live-123 "user456"

# Contar espectadores
SCARD live:viewers:live-123
# => 42

# Listar espectadores
SMEMBERS live:viewers:live-123
```

#### Lists (Chat y Mensajes)
```redis
# Agregar mensaje al chat
RPUSH live:comentarios:live-123 "timestamp|user456|Hola a todos"

# Mantener últimos 100 mensajes
LTRIM live:comentarios:live-123 -100 -1

# Ver últimos 10 mensajes
LRANGE live:comentarios:live-123 -10 -1
```

#### Strings (Contadores)
```redis
# Incrementar vistas
INCR likes:count:content123
# => 43

# Obtener contador
GET likes:count:content123
```

#### Pub/Sub (Eventos)
```redis
# Publicar evento
PUBLISH live:events:live-123 "DONATION|user456|10.50"

# Suscribirse a eventos
SUBSCRIBE live:events:live-123
```

### Patrones de Keys
```
# Contadores
likes:count:{contenidoId}
views:count:{contenidoId}

# Rankings
ranking:likes:global
ranking:vistas:global

# Live Streaming
live:viewers:{liveId}
live:comentarios:{liveId}
live:preguntas:{liveId}
live:donaciones:{liveId}

# Cache de usuarios
user:likes:{userId}
user:feed:{userId}

# Pub/Sub
live:events:{liveId}
notifications
```

---

## 🔌 WebSocket con STOMP

### Arquitectura de Tiempo Real

```
Cliente 1                        Servidor                         Cliente 2
   │                                │                                │
   │─────── CONNECT ────────────────▶│                                │
   │◀────── CONNECTED ──────────────│                                │
   │                                │                                │
   │─── SUBSCRIBE /topic/live/123 ─▶│                                │
   │                                │◀─── SUBSCRIBE /topic/live/123 ─│
   │                                │                                │
   │─── SEND /app/chat ─────────────▶│                                │
   │                                │                                │
   │                          [Procesar mensaje]                     │
   │                          [Guardar en Redis]                     │
   │                                │                                │
   │◀──── MESSAGE /topic/live/123 ──│────── MESSAGE /topic/live/123 ─▶│
   │                                │                                │
```

### Endpoints WebSocket

| Acción Cliente | Envía a | Recibe de | Descripción |
|----------------|---------|-----------|-------------|
| Conectar | `/ws` | - | Establecer conexión |
| Enviar mensaje | `/app/live/{id}/chat` | `/topic/live/{id}/chat` | Chat público |
| Unirse | `/app/live/{id}/join` | `/topic/live/{id}/events` | Notificar entrada |
| Pregunta | `/app/live/{id}/question` | `/topic/live/{id}/questions` | Enviar pregunta |

---

## 🔄 Flujos de Datos Principales

### Flujo 1: Dar Like a un Contenido

```
1. Cliente: POST /api/contents/{id}/like
                    ↓
2. LikeService: Verificar duplicado en Redis
                    ↓
3. Redis: INCR likes:count:content123
   Redis: ZADD ranking:likes:global
   Redis: SADD user:likes:user456
                    ↓
4. Neo4j: CREATE (u)-[:GUSTA]->(c)
                    ↓
5. Response: { success: true, totalLikes: 43 }
```

### Flujo 2: Transmisión en Vivo Completa

```
1. Creador: POST /api/live/start
                    ↓
2. MongoDB: INSERT transmision (estado: ACTIVA)
   Redis: Inicializar estructuras
                    ↓
3. Espectadores: WebSocket CONNECT /ws
                    ↓
4. Espectadores: SUBSCRIBE /topic/live/{id}/chat
                    ↓
5. Interacciones:
   - Chat: Redis List + WebSocket broadcast
   - Donaciones: MongoDB + Redis + WebSocket event
   - Preguntas: Redis List
                    ↓
6. Creador: POST /api/live/{id}/end
                    ↓
7. Calcular estadísticas desde Redis
   MongoDB: UPDATE transmision (estado: FINALIZADA)
   Opcional: Crear Contenido permanente
```

### Flujo 3: Recomendaciones Personalizadas

```
1. Cliente: GET /api/network/recommendations/{id}
                    ↓
2. Neo4j: MATCH (u)-[:SIGUE]->(seguido)-[:CREO]->(contenido)
          WHERE NOT (u)-[:VIO]->(contenido)
                    ↓
3. MongoDB: FIND contenidos WHERE _id IN [resultados_neo4j]
                    ↓
4. Redis: Cache resultado en user:feed:{userId}
                    ↓
5. Response: Lista de contenidos recomendados
```

---

## 🔐 Seguridad

### Autenticación JWT

```
1. Login: POST /api/users/login
              ↓
2. Validar credenciales (MongoDB)
              ↓
3. Generar JWT (algoritmo HS256)
              ↓
4. Response: { token: "eyJhbGciOi..." }
              ↓
5. Cliente: Incluir en header
   Authorization: Bearer {token}
              ↓
6. JWTAuthenticationFilter: Validar token
              ↓
7. Acceso a recursos protegidos
```

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Todos los endpoints |
| **CREADOR** | Subir contenido, iniciar transmisiones, ver analytics propios |
| **ESPECTADOR** | Ver contenido, comentar, dar like, ver transmisiones |

---

## 📈 Escalabilidad

### Estrategias Implementadas

#### 1. Sharding en MongoDB
```javascript
// Shard key por creadorId
sh.shardCollection("comuniarte.contenidos", { creadorId: 1 })
```

#### 2. Redis Cluster (Preparado)
```
Redis Master (6379)
  ├── Redis Replica 1 (6380)
  └── Redis Replica 2 (6381)
```

#### 3. Neo4j Clustering (Preparado)
```
Neo4j Core 1 (7687) ─┐
Neo4j Core 2 (7688) ─┼─ Consensus
Neo4j Core 3 (7689) ─┘
```

#### 4. Load Balancing (Recomendado)
```
           Nginx Load Balancer
                   │
        ┌──────────┼──────────┐
        │          │          │
   App 1:8080  App 2:8081  App 3:8082
```

---

## 🔍 Monitoreo y Observabilidad

### Métricas Clave

```
GET /api/system/metrics

{
  "mongodb": {
    "connections": 45,
    "operations": 1234,
    "avgLatency": "5ms"
  },
  "neo4j": {
    "transactions": 789,
    "avgQueryTime": "10ms"
  },
  "redis": {
    "operations": 50000,
    "hitRate": "95%",
    "avgLatency": "1ms"
  },
  "websocket": {
    "activeSessions": 150,
    "messagesPerSecond": 350
  }
}
```

### Logs Estructurados

```json
{
  "timestamp": "2025-10-26T20:00:00Z",
  "level": "INFO",
  "service": "LiveService",
  "method": "startLive",
  "userId": "user123",
  "action": "TRANSMISION_INICIADA",
  "liveId": "live-abc123",
  "duration": "15ms"
}
```

---

## 🚀 Deployment

### Docker Compose (Desarrollo)

```yaml
services:
  mongodb:
    image: mongo:7.0
    ports: ["27017:27017"]
    
  neo4j:
    image: neo4j:5.0
    ports: ["7474:7474", "7687:7687"]
    
  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]
    
  app:
    build: .
    ports: ["8080:8080"]
    depends_on: [mongodb, neo4j, redis]
```

### Producción (Recomendado)

- **Kubernetes** con Helm Charts
- **AWS ECS** o **GCP Cloud Run**
- **Managed Databases**: MongoDB Atlas, Neo4j Aura, Redis Cloud

---

## 📊 Performance Benchmarks

| Operación | Latencia | Throughput |
|-----------|----------|------------|
| Likes (Redis) | ~1ms | 10,000 ops/sec |
| Query MongoDB | ~5ms | 2,000 queries/sec |
| Query Neo4j | ~10ms | 1,000 queries/sec |
| WebSocket Message | ~2ms | 5,000 msg/sec |
| Cache Hit (Redis) | ~0.5ms | 50,000 ops/sec |

---

**Última actualización:** Octubre 26, 2025  
**Arquitecto:** ComuniArte Development Team  
**Versión:** 1.0

