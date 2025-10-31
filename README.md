# 🎨 ComuniArte - Plataforma de Streaming Cultural y Comunitario

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
  <img src="https://img.shields.io/badge/MongoDB-7.0-green" alt="MongoDB">
  <img src="https://img.shields.io/badge/Neo4j-5.0-blue" alt="Neo4j">
  <img src="https://img.shields.io/badge/Redis-7.0-red" alt="Redis">
  <img src="https://img.shields.io/badge/WebSocket-STOMP-yellow" alt="WebSocket">
</p>

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Instalación](#-instalación)
- [Endpoints](#-endpoints-principales)
- [Uso](#-uso)
- [Documentación](#-documentación)
- [Equipo](#-equipo)

---

## 📖 Descripción

**ComuniArte** es una plataforma de streaming cultural desarrollada para artistas independientes, comunidades rurales y colectivos culturales de América Latina. Permite compartir contenido audiovisual, crear redes comunitarias, transmisiones en vivo y generar interacciones significativas.

### Características Principales

✅ **Gestión de Contenidos** - Subida y organización de videos, audios y textos
✅ **Red Social** - Sistema de seguidores y recomendaciones basado en grafos
✅ **Transmisiones en Vivo** - Chat en tiempo real, donaciones y preguntas
✅ **Listas Personalizadas** - Colecciones públicas y privadas de contenidos
✅ **Analytics** - Métricas de impacto, rankings y estadísticas
✅ **Likes y Comentarios** - Sistema completo de interacción social
✅ **Segmentación de Usuarios** - Análisis por región, tipo e intereses

---

## 🏗️ Arquitectura

### Stack de Bases de Datos (Polyglot Persistence)

```
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot Application               │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   MongoDB    │  │    Neo4j     │  │    Redis     │ │
│  │              │  │              │  │              │ │
│  │ • Contenidos │  │ • Relaciones │  │ • Cache      │ │
│  │ • Usuarios   │  │ • Seguidores │  │ • Contadores │ │
│  │ • Comentarios│  │ • GUSTA      │  │ • Rankings   │ │
│  │ • Listas     │  │ • VIO        │  │ • Live Chat  │ │
│  │ • Transmisión│  │ • COLABORA   │  │ • Pub/Sub    │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │            WebSocket (STOMP)                      │  │
│  │  • Chat en vivo   • Eventos   • Notificaciones   │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### ¿Por qué 3 bases de datos?

| Base de Datos | Propósito | Ventaja Clave |
|---------------|-----------|---------------|
| **MongoDB** | Documentos flexibles (contenidos, usuarios) | Esquema dinámico, ideal para metadatos enriquecidos |
| **Neo4j** | Relaciones sociales (grafos) | Recomendaciones ultrarrápidas basadas en red |
| **Redis** | Cache y tiempo real | Latencia sub-millisegundo para rankings y live streaming |

---

## 🛠️ Tecnologías

### Backend
- **Spring Boot 3.2.3** - Framework principal
- **Java 21** - Lenguaje de programación
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de boilerplate

### Bases de Datos
- **MongoDB 7.0** - Base de datos NoSQL documental
- **Neo4j 5.0** - Base de datos de grafos
- **Redis 7.0** - Cache y almacenamiento en memoria

### Comunicación en Tiempo Real
- **WebSocket con STOMP** - Protocolo de mensajería
- **Redis Pub/Sub** - Sistema de eventos

### Seguridad
- **Spring Security** - Framework de seguridad
- **JWT** - Autenticación basada en tokens

### DevOps
- **Docker & Docker Compose** - Containerización
- **Git** - Control de versiones

---

## 🚀 Instalación

### Prerrequisitos

- Java 21 o superior
- Maven 3.8+
- Docker y Docker Compose
- Git

### 1. Clonar el Repositorio

```bash
git clone https://github.com/LautaroCavallo/ComuniArte.git
cd ComuniArte
```

### 2. Configurar Variables de Entorno

Crear archivo `BACKEND_CONFIG.env` (ya existe en el proyecto):

```env
# MongoDB
MONGO_URI=mongodb://localhost:27017/comuniarte
MONGO_DATABASE=comuniarte

# Neo4j
NEO4J_URI=bolt://localhost:7687
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=tu-clave-secreta-super-segura-de-al-menos-256-bits
JWT_EXPIRATION=86400000
```

### 3. Levantar Servicios con Docker

```bash
# Iniciar MongoDB, Neo4j y Redis
docker-compose up -d

# Verificar que los servicios estén corriendo
docker-compose ps
```

### 4. Compilar y Ejecutar la Aplicación

#### Windows
```bash
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

#### Linux/Mac
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 5. Verificar que la Aplicación Esté Corriendo

Abrir en el navegador: http://localhost:8080/api/system/status

---

## 🌐 Endpoints Principales

### 📁 Gestión de Contenidos

```http
POST   /api/contents              # Subir contenido
GET    /api/contents/{id}         # Obtener contenido
PUT    /api/contents/{id}         # Actualizar contenido
DELETE /api/contents/{id}         # Eliminar contenido
GET    /api/contents/list         # Listar con filtros
POST   /api/contents/{id}/like    # Dar like
POST   /api/contents/{id}/comments # Comentar
```

### 👥 Gestión de Usuarios

```http
POST   /api/auth/register        # Registrar usuario
POST   /api/auth/login           # Iniciar sesión
GET    /api/users/{id}            # Ver perfil
PUT    /api/users/{id}            # Actualizar perfil
GET    /api/users/{id}/history    # Ver historial
POST   /api/users/{id}/lists      # Crear lista personalizada
GET    /api/users/segment         # Segmentar usuarios
```

### 🕸️ Red Social (Neo4j)

```http
POST   /api/network/follow        # Seguir usuario
POST   /api/network/unfollow      # Dejar de seguir
GET    /api/network/followers/{id} # Ver seguidores
GET    /api/network/following/{id} # Ver seguidos
GET    /api/network/recommendations/{id} # Recomendaciones
GET    /api/network/graph         # Grafo de relaciones
```

### 📡 Transmisiones en Vivo

```http
POST   /api/live/start            # Iniciar transmisión
POST   /api/live/{id}/end         # Finalizar transmisión
GET    /api/live/active           # Ver transmisiones activas
POST   /api/live/{id}/join        # Unirse a transmisión
POST   /api/live/{id}/donate      # Hacer donación
GET    /api/live/{id}/viewers     # Ver espectadores
```

### 📊 Analytics

```http
POST   /api/analytics/views       # Registrar vista
GET    /api/analytics/content/{id} # Métricas de contenido
GET    /api/analytics/creator/{id} # Métricas de creador
GET    /api/analytics/ranking     # Rankings de popularidad
GET    /api/analytics/impact      # Impacto por región
```

### 🔌 WebSocket

```javascript
// Conectar a WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// Suscribirse a chat
stompClient.subscribe('/topic/live/{liveId}/chat', (message) => {
    console.log('Mensaje recibido:', message.body);
});

// Enviar mensaje
stompClient.send('/app/live/{liveId}/chat', {}, JSON.stringify({
    sender: 'usuario123',
    content: 'Hola a todos!'
}));
```

Ver documentación completa en: [API_DOCUMENTATION.md](documentation/API_DOCUMENTATION.md)

---

## 💡 Uso

### Ejemplo: Crear una Transmisión en Vivo

1. **Iniciar transmisión**
```bash
curl -X POST http://localhost:8080/api/live/start \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "creadorId": "user123",
    "titulo": "Concierto en vivo",
    "descripcion": "Música folclórica",
    "categoria": "Música",
    "etiquetas": ["folklore", "en-vivo", "argentina"]
  }'
```

2. **Conectar al chat via WebSocket**
```javascript
// Ver ejemplo completo en documentation/WEBSOCKET_CLIENT_EXAMPLE.html
```

3. **Finalizar transmisión**
```bash
curl -X POST "http://localhost:8080/api/live/live-123/end?guardarComoContenido=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Ejemplo: Sistema de Likes con Redis y Neo4j

```bash
# Dar like (actualiza Redis + crea relación GUSTA en Neo4j)
curl -X POST http://localhost:8080/api/contents/content123/like \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": "user456"}'

# Ver estadísticas
curl http://localhost:8080/api/contents/content123/likes
```

---

## 📚 Documentación

- 📖 [Documentación Completa de API](documentation/API_DOCUMENTATION.md)
- 🏗️ [Arquitectura del Sistema](documentation/ARCHITECTURE.md)
- 🔴 [Implementación de Redis y WebSocket](documentation/REDIS_WEBSOCKET_IMPLEMENTATION.md)
- 🛠️ [Implementación de Servicios](documentation/SERVICES_IMPLEMENTATION.md)
- 📋 [Requerimientos del TPO](documentation/TPO_BDB_II.md)
- 🧪 [Cliente de Prueba WebSocket](documentation/WEBSOCKET_CLIENT_EXAMPLE.html)

---

## 🗂️ Estructura del Proyecto

```
ComuniArte/
├── src/main/java/com/uade/tpo/marketplace/
│   ├── controllers/          # Controladores REST y WebSocket
│   │   ├── ContentController.java
│   │   ├── UserController.java
│   │   ├── LiveController.java
│   │   ├── NetworkController.java
│   │   ├── AnalyticsController.java
│   │   ├── WebSocketChatController.java
│   │   └── config/
│   │       ├── WebSocketConfig.java
│   │       ├── RedisConfig.java
│   │       └── SecurityConfig.java
│   ├── entity/               # Entidades
│   │   ├── mongodb/          # Documentos MongoDB
│   │   │   ├── Usuario.java
│   │   │   ├── Contenido.java
│   │   │   ├── Comentario.java
│   │   │   ├── ListaPersonalizada.java
│   │   │   └── Transmision.java
│   │   └── neo4j/            # Nodos y Relaciones Neo4j
│   │       ├── Usuario.java
│   │       ├── Contenido.java
│   │       ├── SigueRelacion.java
│   │       └── VioRelacion.java
│   ├── repository/           # Repositorios
│   │   ├── mongodb/
│   │   └── neo4j/
│   └── service/              # Lógica de negocio
│       ├── ContentService.java
│       ├── CommentService.java
│       ├── LikeService.java
│       ├── UserProfileService.java
│       ├── UserListService.java
│       ├── LiveService.java
│       ├── NetworkService.java
│       └── AnalyticsService.java
├── documentation/            # Documentación del proyecto
├── docker-compose.yml        # Configuración Docker
└── pom.xml                   # Dependencias Maven
```

---

## 🔒 Seguridad

### Autenticación JWT

Todos los endpoints (excepto `/register` y `/login`) requieren autenticación JWT.

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","email":"juan@mail.com","password":"pass123"}'

# 2. Obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@mail.com","password":"pass123"}'

# 3. Usar token en requests
curl http://localhost:8080/api/users/123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🧪 Testing

### Cliente de Prueba WebSocket

Abrir en navegador: `documentation/WEBSOCKET_CLIENT_EXAMPLE.html`

### Postman Collection

Importar: `collection.json`

### Verificar Servicios

```bash
# MongoDB
docker exec -it comuniarte-mongodb mongosh

# Neo4j Browser
http://localhost:7474

# Redis CLI
docker exec -it comuniarte-redis redis-cli
```

---

## 📊 Métricas y Estadísticas

### Redis - Estructuras de Datos

```bash
# Ver ranking de likes
docker exec -it comuniarte-redis redis-cli
> ZREVRANGE ranking:likes:global 0 10 WITHSCORES

# Ver espectadores activos
> SMEMBERS live:viewers:live-123

# Ver últimos mensajes de chat
> LRANGE live:comentarios:live-123 -10 -1
```

### MongoDB - Consultas de Análisis

```javascript
// Conectar a MongoDB
mongosh mongodb://localhost:27017/comuniarte

// Transmisiones más populares
db.transmisiones.find().sort({espectadoresMax: -1}).limit(10)

// Usuarios por región
db.usuarios.aggregate([
  {$group: {_id: "$region", count: {$sum: 1}}}
])
```

### Neo4j - Análisis de Red

```cypher
// Abrir Neo4j Browser: http://localhost:7474

// Usuarios más seguidos
MATCH (u:Usuario)<-[r:SIGUE]-()
RETURN u.userId, count(r) as seguidores
ORDER BY seguidores DESC LIMIT 10

// Recomendaciones por red
MATCH (u:Usuario {userId: 'user123'})-[:SIGUE]->(seguido)-[:CREO]->(contenido)
WHERE NOT (u)-[:VIO]->(contenido)
RETURN contenido LIMIT 10
```

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama de feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add: Amazing Feature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

---

## 📝 Licencia

Este proyecto fue desarrollado como Trabajo Práctico Obligatorio para la materia **Bases de Datos II** de la Universidad Argentina de la Empresa (UADE).

---

## 👥 Equipo

Desarrollado por estudiantes de Ingeniería en Informática - UADE

**Universidad:** Universidad Argentina de la Empresa (UADE)  
**Materia:** Bases de Datos II  
**Año:** 2025  
**Proyecto:** TPO - ComuniArte

---

## 🙏 Agradecimientos

- **MongoDB** por su flexibilidad en el manejo de documentos
- **Neo4j** por hacer las relaciones sociales ultrarrápidas
- **Redis** por la velocidad en tiempo real
- **Spring Boot** por simplificar el desarrollo
- **Docker** por facilitar el deployment

---

<p align="center">
  Hecho con ❤️ para artistas independientes y comunidades culturales de América Latina
</p>

