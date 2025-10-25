# 🔐 Credenciales ComuniArte - Equipo de Desarrollo

## 🚀 **Acceso a Servicios Principales**

### **Sistema de Monitoreo**
```
Grafana Dashboard:
- URL: http://localhost:3000
- Usuario: admin
- Contraseña: admin1234

Prometheus:
- URL: http://localhost:9090
- Consulta: PromQL queries

Alertmanager:
- URL: http://localhost:9093
- Gestión: Alertas activas
```

### **Bases de Datos**
```
MongoDB:
- Host: localhost:27017
- Database: comuniarte_db
- Usuario: admin
- Contraseña: admin1234

Neo4j:
- Browser: http://localhost:7474
- Bolt: bolt://localhost:7687
- Usuario: neo4j
- Contraseña: admin1234

Redis:
- Host: localhost:6379
- Commander: http://localhost:8081
- Sin autenticación requerida

MinIO:
- API: http://localhost:9000
- Console: http://localhost:9001
- Usuario: minioadmin
- Contraseña: minioadmin1234
```

### **Sistema de Analytics**
```
Kafka:
- Broker: localhost:9092
- Zookeeper: localhost:2181

Analytics API:
- URL: http://localhost:3001
- Base URL: http://localhost:3001/api/analytics

Analytics Dashboard:
- URL: http://localhost:3002
- Usuario: admin
- Contraseña: analytics1234
```

## 📊 **Puertos de Servicios**

### **Infraestructura Principal**
- MongoDB: `localhost:27017`
- Redis: `localhost:6379`
- Neo4j Browser: `localhost:7474`
- Neo4j Bolt: `localhost:7687`
- MinIO API: `localhost:9000`
- MinIO Console: `localhost:9001`
- Nginx: `localhost:80` / `localhost:443`

### **Monitoreo**
- Prometheus: `localhost:9090`
- Grafana: `localhost:3000`
- Alertmanager: `localhost:9093`
- Node Exporter: `localhost:9100`

### **Analytics**
- Kafka: `localhost:9092`
- Kafka UI: `localhost:8080`
- Zookeeper: `localhost:2181`
- Spark Master: `localhost:8081`
- Spark Worker: `localhost:8082`
- Analytics API: `localhost:3001`
- Analytics Dashboard: `localhost:3002`

### **Desarrollo**
- Redis Commander: `localhost:8081`

## 🛠️ **Comandos de Gestión**

### **Iniciar Sistemas**
```bash
# Infraestructura principal
docker-compose up -d

# Sistema de monitoreo
docker-compose -f docker-compose-monitoring.yml up -d

# Sistema de analytics
docker-compose -f docker-compose-analytics.yml up -d
```

### **Verificar Estado**
```bash
# Ver todos los servicios
docker ps

# Ver logs de un servicio
docker-compose logs [servicio]

# Verificar salud de servicios
.\scripts\infrastructure.ps1 health
```

### **Gestión de Kafka**
```bash
# Listar topics
docker exec comuniarte-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Crear topic
docker exec comuniarte-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic

# Consumir mensajes
docker exec comuniarte-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events
```

## 🔌 **API Endpoints**

### **Aplicación Principal**
```
Base URL: http://localhost:8080/api/v1

Authentication:
- POST /auth/register
- POST /auth/login
- GET /auth/profile

Content Management:
- GET /content
- POST /content
- GET /content/{id}
- POST /content/{id}/view
- POST /content/{id}/reaction
- POST /content/{id}/comment

Social Network:
- GET /network/followers
- GET /network/following
- POST /network/follow/{userId}
```

### **Analytics API**
```
Base URL: http://localhost:3001/api/analytics

Real-time Metrics:
- GET /realtime
- GET /dashboard

User Analytics:
- GET /users?timeRange=1h

Content Analytics:
- GET /content?timeRange=1h

Interaction Analytics:
- GET /interactions?timeRange=1h

System Analytics:
- GET /system?timeRange=1h

Health Check:
- GET /health
```

## 🚨 **Alertas Configuradas**

### **Niveles de Severidad**
- **Critical**: Servicios caídos, disco lleno
- **Warning**: Alto uso de recursos, tiempo de respuesta lento

### **Umbrales de Alertas**
- CPU: >80% = Warning
- Memoria: >85% = Warning  
- Disco: <10% = Critical
- Response Time: >2s = Warning
- Error Rate: >5% = Warning

## 📈 **Métricas Clave**

### **Sistema**
- `node_cpu_seconds_total`: Uso de CPU
- `node_memory_MemTotal_bytes`: Memoria total
- `node_filesystem_avail_bytes`: Espacio en disco

### **Servicios**
- `up{job="mongodb"}`: Estado MongoDB
- `up{job="redis"}`: Estado Redis
- `up{job="neo4j"}`: Estado Neo4j
- `up{job="minio"}`: Estado MinIO
- `up{job="nginx"}`: Estado Nginx

### **Aplicación**
- `http_requests_total`: Requests totales
- `http_request_duration_seconds`: Tiempo de respuesta
- `mongodb_operations_total`: Operaciones MongoDB
- `redis_operations_total`: Operaciones Redis

## 🔧 **Troubleshooting**

### **Problemas Comunes**
```
Service not running: Check docker-compose status
Port conflicts: Check netstat -tulpn
Permission denied: chmod +x scripts
Node modules missing: npm install
Kafka not starting: Check Zookeeper connection
Spark jobs failing: Check Kafka connectivity
```

### **Comandos de Diagnóstico**
```bash
# Check services
docker-compose ps

# Check ports
netstat -tulpn | grep -E "(27017|6379|7687|9000|8080)"

# Check logs
docker-compose logs [service]

# Check API health
curl http://localhost:8080/health
curl http://localhost:3001/health
```

## 📁 **Archivos de Configuración**

### **Docker Compose**
- `docker-compose.yml`: Infraestructura principal
- `docker-compose-monitoring.yml`: Sistema de monitoreo
- `docker-compose-analytics.yml`: Sistema de analytics

### **Configuraciones**
- `config/prometheus.yml`: Configuración Prometheus
- `config/alertmanager.yml`: Configuración Alertmanager
- `config/nginx.conf`: Configuración Nginx
- `config/kafka-server.properties`: Configuración Kafka

### **Scripts**
- `scripts/infrastructure.ps1`: Gestión de infraestructura
- `scripts/generate-secrets.ps1`: Generación de secrets
- `scripts/mongo-init.js`: Inicialización MongoDB
- `scripts/neo4j-init.cypher`: Inicialización Neo4j

---

**⚠️ IMPORTANTE: Este archivo contiene credenciales sensibles. Solo compartir con el equipo autorizado.**
