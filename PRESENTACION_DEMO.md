# 🎯 Guía de Presentación - ComuniArte Backend

## 📋 Información General
- **Proyecto:** ComuniArte - Plataforma de Streaming Cultural
- **Backend:** Spring Boot 3.2.3 + Java 21
- **Bases de Datos:** MongoDB + Neo4j + Redis (Polyglot Persistence)
- **Real-time:** WebSocket + Redis Pub/Sub
- **Autenticación:** JWT + Spring Security

---

## 🚀 Preparación Previa (15 min antes)

### 1️⃣ Levantar Infraestructura
```bash
# Terminal 1 - Levantar Docker
cd ComuniArte
docker-compose up -d

# Verificar que todo esté corriendo
docker ps

# Deberías ver 6 contenedores:
# ✅ MongoDB (27017)
# ✅ Neo4j (7474, 7687)
# ✅ Redis (6379)
# ✅ MinIO (9000, 9001)
# ✅ Redis Commander (8081)
# ✅ Nginx (80)
```

### 2️⃣ Levantar Backend Spring Boot
```bash
# Terminal 2 - Levantar Backend
mvn spring-boot:run

# O desde tu IDE (IntelliJ/Eclipse):
# Run → ComuniArteApplication.java

# Esperar a ver:
# "Started ComuniArteApplication in X seconds"
```

### 3️⃣ Verificar que Todo Funcione
```bash
# Terminal 3 - Verificación rápida
curl http://localhost:8080/api/health

# Respuesta esperada:
# {"message":"ComuniArte Backend is running!","status":"UP"}
```

### 4️⃣ Importar Colección Postman
1. Abrir Postman
2. Import → Archivo `ComuniArte-Complete.postman_collection.json`
3. Environments → Import `ComuniArte.postman_environment.json`
4. Seleccionar environment "ComuniArte - Local"

---

## 🎬 Flujo de Demostración (20-30 min)

### **PARTE 1: Arquitectura y Tecnologías (5 min)**

#### 📊 Mostrar Diagrama
```
┌─────────────────────────────────────────────┐
│         Spring Boot Application             │
├─────────────────────────────────────────────┤
│  MongoDB  │   Neo4j   │     Redis           │
│  (JSON)   │  (Grafos) │  (Cache/RT)         │
├─────────────────────────────────────────────┤
│           WebSocket (STOMP)                 │
└─────────────────────────────────────────────┘
```

#### 💬 Puntos Clave
- **MongoDB:** Contenidos, usuarios, comentarios, transmisiones
- **Neo4j:** Relaciones sociales (SIGUE, GUSTA, VIO), recomendaciones
- **Redis:** Cache, contadores en tiempo real, chat en vivo
- **WebSocket:** Comunicación bidireccional para live streams

#### 🔍 Mostrar Código Relevante
- **Polyglot Persistence:** `application.properties` (configuraciones)
- **Entities:** `src/main/java/com/uade/tpo/marketplace/entity/`
- **Services:** `src/main/java/com/uade/tpo/marketplace/service/`

---

### **PARTE 2: Autenticación y Seguridad (3 min)**

#### 1. Registrar Usuario (Postman)
```http
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "nombre": "Demo Profesor",
  "email": "profesor@uade.edu.ar",
  "password": "demo123",
  "region": "Argentina",
  "tipoUsuario": "creador"
}
```

**✅ Mostrar:**
- Se crea usuario en **MongoDB**
- Se crea nodo en **Neo4j** automáticamente
- Se retorna **JWT Token**

#### 2. Login (Postman)
```http
POST http://localhost:8080/api/users/login

{
  "email": "profesor@uade.edu.ar",
  "password": "demo123"
}
```

**✅ Copiar el token JWT** → Usar en próximos requests

---

### **PARTE 3: Gestión de Contenidos (5 min)**

#### 1. Crear Contenido (Postman)
```http
POST http://localhost:8080/api/contents
Authorization: Bearer {{jwt_token}}

{
  "title": "Festival de Música Andina",
  "description": "Transmisión en vivo desde Tilcara",
  "category": "Música",
  "creatorId": "{{userId}}",
  "mediaType": "video",
  "mediaUrl": "https://stream.comuniarte.org/andina.mp4",
  "tags": ["andina", "folklore", "tilcara"]
}
```

**✅ Mostrar:**
- Contenido guardado en **MongoDB**
- Nodo creado en **Neo4j**

#### 2. Buscar Contenidos (Postman)
```http
GET http://localhost:8080/api/contents?category=Música
```

#### 3. Comentar en Contenido (Postman)
```http
POST http://localhost:8080/api/contents/{{contentId}}/comments

{
  "usuarioId": "{{userId}}",
  "texto": "¡Excelente contenido!"
}
```

#### 4. Dar Like (Postman)
```http
POST http://localhost:8080/api/contents/{{contentId}}/like?usuarioId={{userId}}
```

**✅ Mostrar:**
- Comentario en **MongoDB**
- Like contador en **Redis** (incremento instantáneo)
- Relación **GUSTA** en **Neo4j**

---

### **PARTE 4: Red Social y Recomendaciones (4 min)**

#### 1. Seguir a un Creador (Postman)
```http
POST http://localhost:8080/api/network/follow?followerId={{userId}}&creatorId={{creatorId}}
```

**✅ Mostrar en Neo4j Browser:**
```cypher
MATCH (u:Usuario)-[s:SIGUE]->(c:Usuario)
RETURN u, s, c
LIMIT 50
```

#### 2. Ver Seguidores (Postman)
```http
GET http://localhost:8080/api/network/followers/{{userId}}
```

#### 3. Recomendaciones (Postman)
```http
GET http://localhost:8080/api/network/recommendations/{{userId}}
```

**💡 Explicar:** El algoritmo usa grafos para encontrar usuarios similares

---

### **PARTE 5: Transmisiones en Vivo (5 min)**

#### 1. Iniciar Transmisión (Postman)
```http
POST http://localhost:8080/api/live/start

{
  "creadorId": "{{userId}}",
  "titulo": "En vivo desde el festival",
  "descripcion": "Música en directo",
  "categoria": "Música"
}
```

**✅ Mostrar:**
- Transmisión guardada en **MongoDB**
- Estado activo en **Redis**

#### 2. WebSocket - Chat en Vivo

**Abrir:** `documentation/WEBSOCKET_CLIENT_EXAMPLE.html`

1. Conectar al WebSocket: `ws://localhost:8080/ws`
2. Subscribirse: `/topic/live/{{liveId}}/chat`
3. Enviar mensaje: `/app/live/{{liveId}}/chat`

**✅ Mostrar:**
- Mensajes en tiempo real
- Guardados en **Redis**
- Broadcasting a todos los conectados

#### 3. Ver Espectadores Activos (Postman)
```http
GET http://localhost:8080/api/live/{{liveId}}/viewers/count
```

#### 4. Finalizar Transmisión (Postman)
```http
POST http://localhost:8080/api/live/{{liveId}}/end
```

---

### **PARTE 6: Analytics y Métricas (3 min)**

#### 1. Métricas de Contenido (Postman)
```http
GET http://localhost:8080/api/analytics/content/{{contentId}}
```

**✅ Mostrar:**
- Total de vistas (MongoDB)
- Total de likes (Redis)
- Engagement rate

#### 2. Ranking de Popularidad (Postman)
```http
GET http://localhost:8080/api/analytics/ranking?category=Música
```

**✅ Mostrar:**
- Rankings dinámicos desde **Redis Sorted Sets**

#### 3. Métricas de Creador (Postman)
```http
GET http://localhost:8080/api/analytics/creator/{{creatorId}}
```

---

### **PARTE 7: Monitoreo y Logs (3 min)**

#### 1. Estado del Sistema (Postman)
```http
GET http://localhost:8080/api/system/status
```

**✅ Mostrar:**
- Estado de MongoDB, Neo4j, Redis
- Métricas de conexión
- Contadores activos

#### 2. Ver Logs (Postman)
```http
GET http://localhost:8080/api/system/logs?level=INFO&limit=20
```

**✅ Mostrar:**
- Logs centralizados en **MongoDB**
- Filtros por nivel, servicio, fecha

#### 3. Logs de Redis (Postman)
```http
GET http://localhost:8080/api/system/logs/redis?limit=10
```

#### 4. Estadísticas de Logs (Postman)
```http
GET http://localhost:8080/api/system/logs/statistics
```

---

### **PARTE 8: Interfaces de Administración (2 min)**

#### 🔵 Neo4j Browser
```
http://localhost:7474
Usuario: neo4j
Password: admin1234
```

**Queries para mostrar:**
```cypher
// Ver todos los nodos
MATCH (n) RETURN n LIMIT 100

// Ver red de seguidores
MATCH (u:Usuario)-[s:SIGUE]->(c:Usuario)
RETURN u, s, c

// Ver contenidos con likes
MATCH (u:Usuario)-[g:GUSTA]->(c:Contenido)
RETURN u, g, c
```

#### 🔴 Redis Commander
```
http://localhost/redis
```

**Keys para mostrar:**
- `live:*:chat` → Mensajes de chat
- `live:*:viewers` → Espectadores activos
- `content:*:likes` → Contadores de likes
- `ranking:*` → Rankings dinámicos

#### 🗄️ MongoDB (opcional)
```bash
# Desde terminal
docker exec -it comuniarte-mongodb mongosh -u admin -p admin1234

use comuniarte_db

// Ver colecciones
show collections

// Ver contenidos
db.contenidos.find().pretty()

// Ver usuarios
db.usuarios.find().pretty()

// Ver logs
db.log_entries.find().limit(5).pretty()
```

---

## 📊 Métricas para Resaltar

### ✅ Endpoints Implementados
- **40+ endpoints REST**
- **4 topics WebSocket**
- **100% de los requerimientos cumplidos**

### ✅ Persistencia Poliglota
- **MongoDB:** 7 colecciones (usuarios, contenidos, comentarios, transmisiones, listas, logs, history)
- **Neo4j:** 3 tipos de nodos (Usuario, Contenido, Categoria), 4 tipos de relaciones (SIGUE, GUSTA, VIO, COLABORA)
- **Redis:** 10+ estructuras de datos (strings, sets, sorted sets, lists, pub/sub)

### ✅ Features Destacadas
- Autenticación JWT con Spring Security
- Real-time con WebSocket + STOMP
- Logging centralizado asíncrono
- Recomendaciones basadas en grafos
- Cache inteligente multi-nivel

---

## 🎯 Posibles Preguntas del Profesor

### 1. **"¿Por qué usar 3 bases de datos?"**
**Respuesta:**
- **MongoDB:** Flexibilidad para documentos complejos (contenidos con metadatos variables)
- **Neo4j:** Eficiencia en consultas de grafos (recomendaciones, red social)
- **Redis:** Velocidad en operaciones en tiempo real (chat, contadores, rankings)

Cada DB se usa para lo que mejor hace → **Polyglot Persistence**

### 2. **"¿Cómo garantizan consistencia entre DBs?"**
**Respuesta:**
- **Patrón Outbox** para operaciones críticas (usuario en MongoDB + Neo4j)
- **Eventual Consistency** para operaciones no críticas
- **Transacciones locales** en cada DB
- **Logs** para auditoría y debugging

### 3. **"¿Qué pasa si Redis falla?"**
**Respuesta:**
- Datos **persistentes** en MongoDB/Neo4j
- Redis solo tiene **cache y datos temporales**
- Sistema sigue funcionando (degradado) sin Redis
- Cache se reconstruye desde MongoDB cuando Redis vuelve

### 4. **"¿Cómo escala esto?"**
**Respuesta:**
- **MongoDB:** Sharding por región/categoría
- **Neo4j:** Clustering para grafos grandes
- **Redis:** Redis Cluster o Sentinel
- **Backend:** Múltiples instancias detrás de load balancer
- **WebSocket:** Sticky sessions + Redis Pub/Sub

### 5. **"¿Qué falta implementar?"**
**Respuesta:**
- Tests unitarios y de integración (actualmente 1 test básico)
- Rate limiting para prevenir spam
- 2FA para autenticación
- Métricas avanzadas (Prometheus/Grafana)
- CDN para videos (actualmente URLs simuladas)

---

## 🛠️ Troubleshooting Durante la Presentación

### ❌ Error: "Connection refused" a MongoDB/Neo4j/Redis
```bash
# Verificar que Docker esté corriendo
docker ps

# Si no está, levantar
docker-compose up -d
```

### ❌ Error: "Port 8080 already in use"
```bash
# En Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Volver a levantar backend
mvn spring-boot:run
```

### ❌ Error: "JWT Token expired"
```bash
# Volver a hacer login
POST http://localhost:8080/api/users/login

# Copiar nuevo token
```

### ❌ Error 403 en endpoints protegidos
- Verificar que el header `Authorization: Bearer <token>` esté presente
- Verificar que el token sea válido (no expirado)

---

## 📱 Recursos de Apoyo Durante Presentación

### Documentos a Tener Abiertos
1. ✅ `README.md` → Overview general
2. ✅ `documentation/API_DOCUMENTATION.md` → Referencia de endpoints
3. ✅ `documentation/ARCHITECTURE.md` → Diagramas técnicos
4. ✅ Esta guía (`PRESENTACION_DEMO.md`)

### Navegador - Tabs a Tener Abiertos
1. ✅ Postman con colección cargada
2. ✅ Neo4j Browser: `http://localhost:7474`
3. ✅ Redis Commander: `http://localhost/redis`
4. ✅ WebSocket Client: `documentation/WEBSOCKET_CLIENT_EXAMPLE.html`
5. ✅ Backend logs en terminal

### IDEs
1. ✅ Terminal con backend corriendo
2. ✅ IntelliJ/Eclipse con código fuente abierto
3. ✅ Terminal con `docker ps` visible

---

## ⏱️ Timing Recomendado

| Sección | Tiempo | Contenido |
|---------|--------|-----------|
| Intro + Arquitectura | 5 min | Diagrama, tech stack, justificación |
| Autenticación | 3 min | Register + Login + JWT |
| Contenidos | 5 min | CRUD + Comments + Likes |
| Red Social | 4 min | Follow + Recommendations + Neo4j |
| Live Streaming | 5 min | WebSocket + Redis + Chat en vivo |
| Analytics | 3 min | Métricas + Rankings |
| Logs y Monitoreo | 3 min | Sistema de logs centralizado |
| Demo Interfaces | 2 min | Neo4j + Redis Commander |
| **Total** | **30 min** | + 5-10 min para preguntas |

---

## 🎉 Cierre de Presentación

### Puntos Finales a Resaltar
1. ✅ **Arquitectura moderna** con polyglot persistence
2. ✅ **100% de requerimientos** implementados
3. ✅ **Real-time** con WebSocket y Redis
4. ✅ **Escalabilidad** pensada desde el diseño
5. ✅ **Logging completo** para debugging y auditoría
6. ✅ **Seguridad** con JWT y Spring Security
7. ✅ **Documentación exhaustiva** (README, API Docs, Architecture)

### Repositorio
```
GitHub: [URL del repositorio]
Branch: Liveserver (100% implementado)
Commits: 10+ commits organizados por fase
```

---

## 📞 Contacto Post-Presentación
- **Documentación completa:** Ver carpeta `/documentation`
- **Colección Postman:** `ComuniArte-Complete.postman_collection.json`
- **Código fuente:** GitHub branch `Liveserver`

---

**¡Buena suerte con la presentación! 🚀🎓**

