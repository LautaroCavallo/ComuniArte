# ⚡ Demo Rápida de ComuniArte (10 min)

## ✅ Checklist Pre-Presentación

```bash
# 1. Docker corriendo
docker ps
# Esperas ver: mongodb, neo4j, redis, minio, nginx, redis-commander

# 2. Backend corriendo
http://localhost:8080/api/health
# Esperas ver: {"message":"ComuniArte Backend is running!","status":"UP"}

# 3. Postman listo
# - Colección importada: ComuniArte-Complete.postman_collection.json
# - Environment seleccionado: ComuniArte - Local
```

---

## 🎯 Flujo de Demo en 10 Minutos

### **1. Intro (1 min)**

**"ComuniArte es una plataforma de streaming cultural que usa polyglot persistence con MongoDB, Neo4j y Redis."**

Mostrar:
- Diagrama en `README.md`
- Docker containers corriendo

---

### **2. Autenticación (2 min)**

#### Paso 1: Registrar Usuario
Postman → `1.1 Registrar Usuario (Creador)` → **Send**

```json
{
  "nombre": "Demo Profesor",
  "email": "profesor@uade.edu.ar",
  "password": "demo123",
  "region": "Argentina",
  "tipoUsuario": "creador"
}
```

✅ **Resaltar:** Token JWT generado automáticamente

#### Paso 2: Ver en Neo4j
Neo4j Browser → `http://localhost:7474`

```cypher
MATCH (u:Usuario {email: "profesor@uade.edu.ar"})
RETURN u
```

✅ **Resaltar:** Usuario guardado en MongoDB Y Neo4j

---

### **3. Contenidos + Likes (3 min)**

#### Crear Contenido
Postman → `2.1 Crear Contenido` → **Send**

✅ **Resaltar:** Guardado en MongoDB

#### Dar Like
Postman → `2.7 Dar Like` → **Send**

#### Ver Like en Redis
Redis Commander → `http://localhost/redis`

Buscar key: `content:*:likes`

✅ **Resaltar:** Contador en tiempo real en Redis

#### Ver Like en Neo4j
Neo4j Browser:
```cypher
MATCH (u:Usuario)-[g:GUSTA]->(c:Contenido)
RETURN u, g, c
```

✅ **Resaltar:** Relación de grafo creada

---

### **4. Live Streaming (3 min)**

#### Iniciar Transmisión
Postman → `5.1 Iniciar Transmisión` → **Send**

#### WebSocket Chat
Abrir → `documentation/WEBSOCKET_CLIENT_EXAMPLE.html`

1. Conectar a `ws://localhost:8080/ws`
2. Subscribirse a `/topic/live/{liveId}/chat`
3. Enviar mensaje

✅ **Resaltar:** Mensajes en tiempo real via WebSocket

#### Ver Chat en Redis
Redis Commander → Buscar: `live:*:chat`

✅ **Resaltar:** Mensajes persistidos en Redis

---

### **5. Analytics + Logs (1 min)**

#### Métricas
Postman → `6.2 Métricas de Contenido` → **Send**

#### Ver Logs
Postman → `7.4 Ver Logs` → **Send**

✅ **Resaltar:** Sistema de logging centralizado en MongoDB

---

## 🔥 Demo Avanzada (20 min) - Opcional

Si tenés más tiempo, agregar:

### 6. Red Social (3 min)
- Follow/Unfollow
- Recomendaciones basadas en grafos
- Visualización en Neo4j

### 7. Rankings Dinámicos (2 min)
- Rankings en Redis Sorted Sets
- Actualización en tiempo real

### 8. Métricas de Sistema (2 min)
- Estado de MongoDB/Neo4j/Redis
- Logs especializados por servicio
- Estadísticas agregadas

---

## 📊 URLs Clave

```
Backend:         http://localhost:8080/api/health
Neo4j Browser:   http://localhost:7474
Redis Commander: http://localhost/redis
MinIO Console:   http://localhost:9001
```

---

## 💬 Frases Clave para la Presentación

1. **"Usamos polyglot persistence para aprovechar las fortalezas de cada base de datos"**
   - MongoDB → Documentos flexibles
   - Neo4j → Consultas de grafos eficientes
   - Redis → Velocidad en tiempo real

2. **"La consistencia eventual se logra con el patrón Outbox y sincronización asíncrona"**

3. **"Redis actúa como cache y como sistema de mensajería Pub/Sub para WebSocket"**

4. **"Los logs centralizados permiten debugging distribuido entre servicios"**

5. **"La arquitectura está preparada para escalar horizontalmente"**

---

## 🆘 Solución Rápida de Problemas

| Problema | Solución |
|----------|----------|
| Docker no levanta | `docker-compose down && docker-compose up -d` |
| Backend no arranca | Verificar logs: `mvn spring-boot:run` |
| 403 Forbidden | Hacer login de nuevo, copiar nuevo JWT |
| WebSocket no conecta | Verificar que backend esté en puerto 8080 |

---

## 🎓 Conclusión

**"Implementamos 40+ endpoints REST, WebSocket para real-time, y polyglot persistence con MongoDB, Neo4j y Redis. El sistema está 100% funcional y listo para producción."**

Cierre con:
- Documentación completa en `/documentation`
- Código en GitHub branch `Liveserver`
- Colección Postman para testing

**¡Gracias!** 🚀

