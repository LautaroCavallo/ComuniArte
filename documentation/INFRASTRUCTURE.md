# 🎭 ComuniArte - Infraestructura

Este directorio contiene toda la configuración de infraestructura para la plataforma ComuniArte, desarrollada como TPO para Ingeniería de Datos II.

## 🏗️ Arquitectura de Infraestructura

### Servicios Incluidos

| Servicio | Puerto | Propósito | Credenciales |
|----------|--------|-----------|--------------|
| **MongoDB** | 27017 | Perfiles de usuarios, metadatos de contenidos, interacciones | admin/admin1234 |
| **Neo4j** | 7474/7687 | Grafo de creadores/seguidores y recomendaciones | neo4j/admin1234 |
| **Redis** | 6379 | Chat en vivo, contadores de viewers, eventos en tiempo real | - |
| **MinIO** | 9000/9001 | Almacenamiento S3-compatible para videos y audios | minioadmin/minioadmin1234 |
| **Redis Commander** | 8081 | Interfaz web para Redis (desarrollo) | - |

### Estructura de Datos

#### MongoDB Collections
- `usuarios` - Perfiles de usuarios (creadores, espectadores, admins)
- `contents` - Metadatos de videos, audios y textos
- `interactions` - Likes, comentarios, visualizaciones
- `analytics` - Métricas históricas y rankings

#### Neo4j Graph
- Nodos: `User`, `Content`, `Category`
- Relaciones: `FOLLOWS`, `UPLOADED`, `LIKED`, `VIEWED`

#### Redis Data Structures
- Streams: Chat en vivo por transmisión
- Sorted Sets: Rankings de popularidad
- Hashes: Sesiones de usuarios activos
- Lists: Notificaciones pendientes

## 🚀 Inicio Rápido

### Prerrequisitos
- Docker Desktop instalado y ejecutándose
- Docker Compose disponible

### Windows (PowerShell)
```powershell
# Ejecutar script interactivo
.\scripts\infrastructure.ps1

# O comandos directos
.\scripts\infrastructure.ps1 start
.\scripts\infrastructure.ps1 health
.\scripts\infrastructure.ps1 stop
```

### Linux/macOS (Bash)
```bash
# Ejecutar script interactivo
./scripts/infrastructure.sh

# O comandos directos
./scripts/infrastructure.sh start
./scripts/infrastructure.sh health
./scripts/infrastructure.sh stop
```

### Docker Compose Directo
```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar servicios
docker-compose down

# Limpiar todo (¡CUIDADO!)
docker-compose down -v
```

## 📊 Monitoreo y Acceso

### Interfaces Web
- **Neo4j Browser**: http://localhost:7474
- **MinIO Console**: http://localhost:9001
- **Redis Commander**: http://localhost:8081

### Conexiones de Aplicación
```yaml
# MongoDB
mongodb://admin:admin1234@localhost:27017/comuniarte_db

# Neo4j
bolt://localhost:7687
Username: neo4j
Password: admin1234

# Redis
redis://localhost:6379

# MinIO
Endpoint: http://localhost:9000
Access Key: minioadmin
Secret Key: minioadmin1234
```

## 🔧 Configuración Avanzada

### Volúmenes Persistentes
Los datos se almacenan en volúmenes Docker nombrados:
- `mongodb_data` - Datos de MongoDB
- `neo4j_data` - Datos de Neo4j
- `redis_data` - Datos de Redis
- `minio_data` - Archivos de MinIO

### Redes
Todos los servicios están en la red `comuniarte-network` (172.20.0.0/16) para comunicación interna.

### Configuración Redis
El archivo `config/redis.conf` optimiza Redis para:
- Memoria: 512MB con política LRU
- Persistencia: Snapshots automáticos
- Estructuras de datos para features en tiempo real

## 🧪 Datos de Prueba

### Usuarios de Ejemplo
- **María González** (maria@comuniarte.com) - Creador
- **Carlos Ruiz** (carlos@comuniarte.com) - Espectador

### Contenido de Ejemplo
- "Poesía Urbana - Barrio Sur" (video)
- "Música Folklórica Andina" (audio)

## 🛠️ Troubleshooting

### Problemas Comunes

1. **Puerto ya en uso**
   ```bash
   # Verificar qué proceso usa el puerto
   netstat -ano | findstr :27017
   
   # Cambiar puertos en docker-compose.yml si es necesario
   ```

2. **Servicios no responden**
   ```bash
   # Verificar logs
   docker-compose logs mongodb
   docker-compose logs neo4j
   
   # Reiniciar servicios
   docker-compose restart
   ```

3. **Problemas de memoria**
   ```bash
   # Limpiar recursos Docker
   docker system prune -f
   
   # Ajustar límites de memoria en docker-compose.yml
   ```

### Comandos Útiles

```bash
# Ver estado de contenedores
docker-compose ps

# Ejecutar comandos en contenedores
docker exec -it comuniarte-mongodb mongosh
docker exec -it comuniarte-neo4j cypher-shell
docker exec -it comuniarte-redis redis-cli

# Backup de datos
docker run --rm -v mongodb_data:/data -v $(pwd):/backup alpine tar czf /backup/mongodb-backup.tar.gz /data
```

## 📈 Escalabilidad

### Para Producción
- Usar Docker Swarm o Kubernetes
- Configurar replicación en MongoDB
- Implementar clustering en Redis
- Usar MinIO distribuido
- Configurar load balancers

### Monitoreo
- Prometheus + Grafana para métricas
- ELK Stack para logs
- Health checks automáticos

## 🔒 Seguridad

### Credenciales por Defecto
⚠️ **IMPORTANTE**: Cambiar todas las credenciales por defecto antes de usar en producción.

### Recomendaciones
- Usar secrets de Docker
- Configurar TLS/SSL
- Implementar autenticación fuerte
- Restringir acceso por IP
- Rotar credenciales regularmente

## 📝 Notas de Desarrollo

- Los datos de prueba se crean automáticamente al iniciar MongoDB
- Los índices se crean automáticamente para optimizar consultas
- Redis Commander es solo para desarrollo (remover en producción)
- Los volúmenes persisten entre reinicios del sistema

---

**Desarrollado por**: Lautaro, Nicolas, Laura  
**Materia**: Ingeniería de Datos II - UADE  
**Profesor**: Lic. Joaquín Salas
