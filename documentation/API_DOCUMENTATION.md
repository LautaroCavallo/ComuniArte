# 📚 ComuniArte - Documentación Completa de API

**Versión:** 0.0.1-SNAPSHOT  
**Base URL:** `http://localhost:8080`  
**Autenticación:** JWT Bearer Token (excepto `/register` y `/login`)

---

## 📑 Índice

1. [Autenticación](#1-autenticación)
2. [Gestión de Contenidos](#2-gestión-de-contenidos)
3. [Gestión de Usuarios](#3-gestión-de-usuarios)
4. [Red Social (Neo4j)](#4-red-social-neo4j)
5. [Transmisiones en Vivo](#5-transmisiones-en-vivo)
6. [WebSocket](#6-websocket)
7. [Analytics](#7-analytics)
8. [Sistema](#8-sistema)

---

## 1. Autenticación

### 1.1 Registrar Usuario

```http
POST /api/users/register
```

**Body:**
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "region": "Argentina",
  "tipoUsuario": "creador"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 1.2 Iniciar Sesión

```http
POST /api/users/login
```

**Body:**
```json
{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

---

## 2. Gestión de Contenidos

### 2.1 Subir Contenido

```http
POST /api/contents
Authorization: Bearer {token}
```

**Body:**
```json
{
  "titulo": "Mi primer video",
  "tipo": "video",
  "urlArchivo": "https://cdn.example.com/video.mp4",
  "creadorId": "user123",
  "categoria": "Música",
  "etiquetas": ["folklore", "argentina", "en-vivo"],
  "metadatosEnriquecidos": {
    "duracion": 180,
    "idioma": "es",
    "region": "Buenos Aires"
  }
}
```

**Response:** `200 OK`
```json
{
  "id": "content123",
  "titulo": "Mi primer video",
  "tipo": "video",
  "creadorId": "user123",
  "fechaPublicacion": "2025-10-26T19:00:00"
}
```

### 2.2 Obtener Contenido

```http
GET /api/contents/{id}
```

**Response:** `200 OK`
```json
{
  "id": "content123",
  "titulo": "Mi primer video",
  "tipo": "video",
  "urlArchivo": "https://cdn.example.com/video.mp4",
  "creadorId": "user123",
  "categoria": "Música",
  "etiquetas": ["folklore", "argentina"],
  "fechaPublicacion": "2025-10-26T19:00:00"
}
```

### 2.3 Actualizar Contenido

```http
PUT /api/contents/{id}
Authorization: Bearer {token}
```

**Body:** (solo campos a actualizar)
```json
{
  "titulo": "Título actualizado",
  "etiquetas": ["nueva-etiqueta"]
}
```

### 2.4 Eliminar Contenido

```http
DELETE /api/contents/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

### 2.5 Listar Contenidos con Filtros

```http
GET /api/contents/list?category={cat}&tag={tag}&creatorId={id}&type={type}&page=0&size=20
```

**Query Parameters:**
- `category` (optional): Filtrar por categoría
- `tag` (optional): Filtrar por etiqueta
- `creatorId` (optional): Filtrar por creador
- `type` (optional): Filtrar por tipo (video/audio/texto)
- `page` (default: 0): Página
- `size` (default: 20): Tamaño de página

### 2.6 Ver Comentarios

```http
GET /api/contents/{id}/comments
```

**Response:** `200 OK`
```json
[
  {
    "id": "comment1",
    "contenidoId": "content123",
    "usuarioId": "user456",
    "texto": "¡Excelente contenido!",
    "fecha": "2025-10-26T19:05:00",
    "esLive": false
  }
]
```

### 2.7 Agregar Comentario

```http
POST /api/contents/{id}/comments
Authorization: Bearer {token}
```

**Body:**
```json
{
  "usuarioId": "user456",
  "texto": "¡Excelente contenido!"
}
```

### 2.8 Dar Like

```http
POST /api/contents/{id}/like
Authorization: Bearer {token}
```

**Body:**
```json
{
  "usuarioId": "user456"
}
```

**Response:** `200 OK`
```json
{
  "contentId": "content123",
  "userId": "user456",
  "success": true,
  "message": "Like registrado exitosamente",
  "totalLikes": 42
}
```

### 2.9 Quitar Like

```http
DELETE /api/contents/{id}/like?usuarioId={userId}
Authorization: Bearer {token}
```

### 2.10 Ver Estadísticas de Likes

```http
GET /api/contents/{id}/likes
```

**Response:** `200 OK`
```json
{
  "totalLikes": 42,
  "rankingPosition": 15,
  "rankingScore": 42
}
```

### 2.11 Verificar Like de Usuario

```http
GET /api/contents/{id}/liked?usuarioId={userId}
```

**Response:** `200 OK`
```json
{
  "liked": true
}
```

---

## 3. Gestión de Usuarios

### 3.1 Obtener Perfil

```http
GET /api/users/{id}
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "id": "user123",
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "tipoUsuario": "creador",
  "region": "Argentina",
  "intereses": ["música", "folklore"],
  "fechaRegistro": "2025-01-15T10:00:00",
  "historialReproduccion": ["content1", "content2"],
  "listasPersonalizadas": ["lista1", "lista2"]
}
```

### 3.2 Actualizar Perfil

```http
PUT /api/users/{id}
Authorization: Bearer {token}
```

**Body:** (solo campos a actualizar)
```json
{
  "nombre": "Juan Carlos Pérez",
  "region": "Buenos Aires",
  "intereses": ["música", "folklore", "tango"]
}
```

### 3.3 Ver Historial de Reproducción

```http
GET /api/users/{id}/history
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
["content1", "content2", "content3"]
```

### 3.4 Agregar al Historial

```http
POST /api/users/{id}/history
Authorization: Bearer {token}
```

**Body:**
```json
{
  "contenidoId": "content123"
}
```

### 3.5 Limpiar Historial

```http
DELETE /api/users/{id}/history
Authorization: Bearer {token}
```

### 3.6 Ver Listas Personalizadas

```http
GET /api/users/{id}/lists
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
[
  {
    "id": "lista1",
    "usuarioId": "user123",
    "nombre": "Mis Favoritos",
    "descripcion": "Contenidos que me encantan",
    "contenidosIds": ["content1", "content2"],
    "esPublica": true,
    "fechaCreacion": "2025-10-20T10:00:00"
  }
]
```

### 3.7 Crear Lista Personalizada

```http
POST /api/users/{id}/lists
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nombre": "Ver más tarde",
  "descripcion": "Contenidos para ver después",
  "esPublica": false
}
```

### 3.8 Actualizar Lista (Agregar/Quitar Contenido)

```http
PUT /api/users/{id}/lists/{listId}
Authorization: Bearer {token}
```

**Agregar contenido:**
```json
{
  "accion": "add",
  "contenidoId": "content123"
}
```

**Quitar contenido:**
```json
{
  "accion": "remove",
  "contenidoId": "content123"
}
```

**Actualizar metadatos:**
```json
{
  "nombre": "Nuevo nombre",
  "descripcion": "Nueva descripción",
  "esPublica": true
}
```

### 3.9 Eliminar Lista

```http
DELETE /api/users/{id}/lists/{listId}
Authorization: Bearer {token}
```

### 3.10 Segmentar Usuarios

```http
GET /api/users/segment?tipoUsuario={tipo}&region={region}&interes={interes}
Authorization: Bearer {token}
```

**Query Parameters:**
- `tipoUsuario` (optional): "creador" o "espectador"
- `region` (optional): Filtrar por región
- `interes` (optional): Filtrar por interés específico

---

## 4. Red Social (Neo4j)

### 4.1 Seguir Usuario

```http
POST /api/network/follow?followerId={userId1}&creatorId={userId2}
Authorization: Bearer {token}
```

### 4.2 Dejar de Seguir

```http
POST /api/network/unfollow?followerId={userId1}&creatorId={userId2}
Authorization: Bearer {token}
```

### 4.3 Ver Seguidores

```http
GET /api/network/followers/{id}
```

**Response:** `200 OK`
```json
[
  {
    "userId": "user456",
    "nombre": "María García"
  }
]
```

### 4.4 Ver Seguidos

```http
GET /api/network/following/{id}
```

### 4.5 Obtener Recomendaciones

```http
GET /api/network/recommendations/{id}
```

**Response:** Contenidos recomendados basados en la red social

### 4.6 Ver Grafo de Relaciones

```http
GET /api/network/graph
```

**Response:** Estructura simplificada del grafo social

---

## 5. Transmisiones en Vivo

### 5.1 Iniciar Transmisión

```http
POST /api/live/start
Authorization: Bearer {token}
```

**Body:**
```json
{
  "creadorId": "user123",
  "titulo": "Concierto en vivo",
  "descripcion": "Música folclórica argentina",
  "categoria": "Música",
  "etiquetas": ["folklore", "en-vivo"]
}
```

**Response:** `200 OK`
```json
{
  "id": "live-abc123",
  "creadorId": "user123",
  "titulo": "Concierto en vivo",
  "estado": "ACTIVA",
  "fechaInicio": "2025-10-26T20:00:00",
  "espectadoresMax": 0
}
```

### 5.2 Finalizar Transmisión

```http
POST /api/live/{id}/end?guardarComoContenido=true
Authorization: Bearer {token}
```

**Query Parameters:**
- `guardarComoContenido` (default: false): Si debe guardarse como contenido permanente

**Response:** Transmisión con estadísticas finales

### 5.3 Ver Transmisión

```http
GET /api/live/{id}
```

### 5.4 Ver Transmisiones Activas

```http
GET /api/live/active
```

**Response:** `200 OK`
```json
[
  {
    "id": "live-abc123",
    "creadorId": "user123",
    "titulo": "Concierto en vivo",
    "estado": "ACTIVA",
    "fechaInicio": "2025-10-26T20:00:00"
  }
]
```

### 5.5 Unirse a Transmisión

```http
POST /api/live/{id}/join?userId={userId}
Authorization: Bearer {token}
```

### 5.6 Salir de Transmisión

```http
POST /api/live/{id}/leave?userId={userId}
Authorization: Bearer {token}
```

### 5.7 Ver Chat

```http
GET /api/live/{id}/chat
```

### 5.8 Enviar Mensaje (REST)

```http
POST /api/live/{id}/chat?userId={userId}
Authorization: Bearer {token}
```

**Body:**
```json
{
  "mensaje": "¡Hola a todos!"
}
```

### 5.9 Hacer Donación

```http
POST /api/live/{id}/donate?donorId={userId}&donorName={name}&creatorId={creatorId}&amount={amount}
Authorization: Bearer {token}
```

### 5.10 Enviar Pregunta

```http
POST /api/live/{id}/questions?userId={userId}
Authorization: Bearer {token}
```

**Body:**
```json
{
  "question": "¿Cuándo es el próximo show?"
}
```

### 5.11 Ver Preguntas

```http
GET /api/live/{id}/questions
```

### 5.12 Ver Eventos

```http
GET /api/live/{id}/events
```

**Response:** `200 OK`
```json
{
  "viewersCount": 42,
  "recentDonations": 5,
  "totalDonations": 150.50,
  "pendingQuestions": 3
}
```

### 5.13 Ver Espectadores

```http
GET /api/live/{id}/viewers
```

**Response:** `200 OK`
```json
{
  "count": 42,
  "viewers": ["user1", "user2", "user3"]
}
```

### 5.14 Contar Espectadores

```http
GET /api/live/{id}/viewers/count
```

**Response:** `200 OK`
```json
{
  "count": 42
}
```

---

## 6. WebSocket

### 6.1 Conectar a WebSocket

**Endpoint:** `ws://localhost:8080/ws`

**Librerías requeridas:**
```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

### 6.2 Ejemplo de Conexión

```javascript
// Conectar
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Conectado: ' + frame);
    
    // Suscribirse a chat
    stompClient.subscribe('/topic/live/live-123/chat', function(message) {
        const msg = JSON.parse(message.body);
        console.log('Mensaje:', msg);
    });
    
    // Suscribirse a eventos
    stompClient.subscribe('/topic/live/live-123/events', function(event) {
        const evt = JSON.parse(event.body);
        console.log('Evento:', evt);
    });
});
```

### 6.3 Enviar Mensaje de Chat

```javascript
stompClient.send('/app/live/live-123/chat', {}, JSON.stringify({
    sender: 'usuario123',
    content: '¡Hola a todos!',
    type: 'CHAT'
}));
```

### 6.4 Notificar Entrada

```javascript
stompClient.send('/app/live/live-123/join', {}, JSON.stringify({
    userId: 'user123',
    userName: 'Juan Pérez'
}));
```

### 6.5 Enviar Pregunta

```javascript
stompClient.send('/app/live/live-123/question', {}, JSON.stringify({
    userId: 'user123',
    userName: 'Juan Pérez',
    question: '¿Cuándo es el próximo show?'
}));
```

### 6.6 Topics Disponibles

| Topic | Descripción |
|-------|-------------|
| `/topic/live/{liveId}/chat` | Mensajes de chat |
| `/topic/live/{liveId}/events` | Eventos (join/leave/donation) |
| `/topic/live/{liveId}/questions` | Preguntas al presentador |

Ver cliente de ejemplo completo: [WEBSOCKET_CLIENT_EXAMPLE.html](WEBSOCKET_CLIENT_EXAMPLE.html)

---

## 7. Analytics

### 7.1 Registrar Vista

```http
POST /api/analytics/views?contentId={contentId}
Authorization: Bearer {token}
```

### 7.2 Métricas de Contenido

```http
GET /api/analytics/content/{id}
```

**Response:** `200 OK`
```json
{
  "contenidoId": "content123",
  "vistas": 1500,
  "likes": 42,
  "comentarios": 15,
  "duracionMedia": 120
}
```

### 7.3 Métricas de Creador

```http
GET /api/analytics/creator/{id}
```

**Response:** `200 OK`
```json
{
  "creadorId": "user123",
  "seguidores": 250,
  "contenidosPublicados": 15,
  "vistasToales": 5000,
  "impactoRegional": {
    "Argentina": 60,
    "Chile": 25,
    "Uruguay": 15
  }
}
```

### 7.4 Rankings de Popularidad

```http
GET /api/analytics/ranking?category={cat}&region={region}&type={type}
```

**Response:** Top 50 contenidos más populares

### 7.5 Impacto por Región

```http
GET /api/analytics/impact?region={region}
```

---

## 8. Sistema

### 8.1 Estado del Sistema

```http
GET /api/system/status
```

**Response:** `200 OK`
```json
{
  "status": "UP",
  "mongodb": "CONNECTED",
  "neo4j": "CONNECTED",
  "redis": "CONNECTED",
  "uptime": "2h 30m"
}
```

### 8.2 Configuración

```http
GET /api/system/config
```

### 8.3 Sesiones Activas

```http
GET /api/system/sessions
Authorization: Bearer {token}
```

### 8.4 Logs del Sistema

```http
GET /api/system/logs
Authorization: Bearer {token}
```

### 8.5 Métricas Técnicas

```http
GET /api/system/metrics
Authorization: Bearer {token}
```

---

## 📝 Códigos de Estado HTTP

| Código | Significado |
|--------|-------------|
| `200 OK` | Solicitud exitosa |
| `201 Created` | Recurso creado exitosamente |
| `204 No Content` | Eliminación exitosa |
| `400 Bad Request` | Solicitud inválida |
| `401 Unauthorized` | No autenticado |
| `403 Forbidden` | No autorizado |
| `404 Not Found` | Recurso no encontrado |
| `500 Internal Server Error` | Error del servidor |

---

## 🔐 Autenticación

Todos los endpoints (excepto `/register` y `/login`) requieren un token JWT en el header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

**Última actualización:** Octubre 26, 2025  
**Versión de API:** 1.0  
**Contacto:** ComuniArte Development Team

