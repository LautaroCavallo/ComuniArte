# 🚀 Sistema de Load Testing ComuniArte

## 🎯 **Visión General**

ComuniArte implementa un sistema completo de **Load Testing** que permite evaluar el rendimiento de la plataforma bajo diferentes cargas de trabajo, identificar cuellos de botella y optimizar el rendimiento para alta concurrencia.

## 🏗️ **Arquitectura del Sistema**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Testing  │    │   Target Apps   │    │   Monitoring   │
│   Tools          │    │                 │    │                 │
│                 │    │                 │    │                 │
│ • Artillery     │───▶│ • Spring Boot   │───▶│ • Prometheus    │
│ • K6            │    │ • MongoDB       │    │ • Grafana       │
│ • Database Tests│    │ • Neo4j         │    │ • Alertmanager  │
│ • Stress Tests  │    │ • Redis         │    │                 │
│                 │    │ • MinIO         │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔧 **Herramientas Implementadas**

### **1. Artillery (HTTP Load Testing)**
- **Función**: Pruebas de carga HTTP/WebSocket
- **Configuración**: `load-testing/artillery-config.yml`
- **Escenarios**: Registro, gestión de contenido, descubrimiento, interacciones
- **Métricas**: Response time, throughput, error rate

### **2. K6 (Performance Testing)**
- **Función**: Pruebas de rendimiento avanzadas
- **Script**: `load-testing/k6-load-test.js`
- **Características**: Custom metrics, thresholds, stages
- **Métricas**: Custom error rate, response time trends

### **3. Database Load Testing**
- **Función**: Pruebas específicas de bases de datos
- **Scripts**: `database-load-test.sh` / `database-load-test.ps1`
- **Bases de datos**: MongoDB, Neo4j, Redis, MinIO
- **Métricas**: Operations/sec, latency, throughput

### **4. Stress Testing**
- **Función**: Pruebas de carga extrema
- **Configuración**: `artillery-stress-config.yml`
- **Objetivo**: Identificar puntos de ruptura
- **Métricas**: Peak load capacity, failure points

## 📊 **Escenarios de Prueba**

### **1. User Registration Flow (20%)**
- Registro de nuevos usuarios
- Autenticación y obtención de tokens
- Verificación de perfiles

### **2. Content Management Flow (30%)**
- Login de creadores
- Listado de contenido
- Creación de nuevo contenido
- Gestión de metadatos

### **3. Content Discovery Flow (25%)**
- Navegación sin autenticación
- Búsqueda por categorías
- Paginación de resultados
- Filtrado por tags

### **4. User Interaction Flow (15%)**
- Login de espectadores
- Visualización de contenido
- Reacciones (likes, dislikes)
- Comentarios

### **5. Social Network Flow (10%)**
- Gestión de seguidores
- Redes sociales
- Recomendaciones

## 🎯 **Métricas de Rendimiento**

### **Response Time Targets**
- **95th percentile**: < 2000ms
- **Average response time**: < 1000ms
- **Maximum response time**: < 5000ms

### **Throughput Targets**
- **Concurrent users**: 50-100
- **Requests per second**: 100-200
- **Peak load capacity**: 300 RPS

### **Error Rate Targets**
- **HTTP errors**: < 1%
- **Database errors**: < 0.1%
- **Timeout errors**: < 0.5%

### **Resource Utilization**
- **CPU usage**: < 80%
- **Memory usage**: < 85%
- **Disk I/O**: < 70%

## 🚀 **Ejecución de Pruebas**

### **Script Maestro**
```bash
# Ejecutar todas las pruebas
./load-testing/run-load-tests.sh
```

### **Pruebas Individuales**

#### **Artillery Tests**
```bash
artillery run load-testing/artillery-config.yml
artillery run load-testing/artillery-config.yml --output results.json
artillery report results.json --output report.html
```

#### **K6 Tests**
```bash
k6 run load-testing/k6-load-test.js
k6 run --out json=results.json load-testing/k6-load-test.js
```

#### **Database Tests**
```bash
# Linux/macOS
./load-testing/database-load-test.sh

# Windows PowerShell
.\load-testing\database-load-test.ps1
```

#### **Stress Tests**
```bash
artillery run load-testing/artillery-stress-config.yml
```

## 📈 **Fases de Carga**

### **1. Warm-up Phase (1 min)**
- **Arrival Rate**: 5 usuarios/segundo
- **Objetivo**: Preparar servicios
- **Duración**: 60 segundos

### **2. Ramp-up Phase (2 min)**
- **Arrival Rate**: 10 → 50 usuarios/segundo
- **Objetivo**: Incremento gradual
- **Duración**: 120 segundos

### **3. Sustained Load Phase (5 min)**
- **Arrival Rate**: 50 usuarios/segundo
- **Objetivo**: Carga sostenida
- **Duración**: 300 segundos

### **4. Peak Load Phase (1 min)**
- **Arrival Rate**: 100 usuarios/segundo
- **Objetivo**: Carga máxima
- **Duración**: 60 segundos

### **5. Cool-down Phase (1 min)**
- **Arrival Rate**: 10 usuarios/segundo
- **Objetivo**: Reducción gradual
- **Duración**: 60 segundos

## 📊 **Métricas Específicas por Servicio**

### **MongoDB**
- **Insert operations**: > 1000 ops/sec
- **Query operations**: > 2000 ops/sec
- **Update operations**: > 500 ops/sec
- **Connection pool**: < 80% utilization

### **Neo4j**
- **Node creation**: > 500 ops/sec
- **Relationship creation**: > 300 ops/sec
- **Graph queries**: > 200 ops/sec
- **Transaction throughput**: > 100 tx/sec

### **Redis**
- **SET operations**: > 10000 ops/sec
- **GET operations**: > 15000 ops/sec
- **Hash operations**: > 5000 ops/sec
- **Memory usage**: < 80%

### **MinIO**
- **PUT operations**: > 100 ops/sec
- **GET operations**: > 500 ops/sec
- **STAT operations**: > 1000 ops/sec
- **Storage throughput**: > 100 MB/sec

## 🔍 **Análisis de Resultados**

### **Reportes Generados**
- **Artillery HTML Report**: Visualización interactiva
- **K6 JSON Results**: Métricas detalladas
- **Database Logs**: Performance por servicio
- **Summary Report**: Resumen ejecutivo

### **Métricas Clave a Monitorear**
1. **Response Time Distribution**
2. **Error Rate Trends**
3. **Throughput Over Time**
4. **Resource Utilization**
5. **Database Performance**

### **Indicadores de Problemas**
- **Response time > 2s**: Optimizar queries
- **Error rate > 1%**: Revisar logs
- **CPU > 80%**: Escalar recursos
- **Memory > 85%**: Optimizar memoria
- **Database slow queries**: Indexar tablas

## 🛠️ **Configuración y Personalización**

### **Archivos de Configuración**
- `artillery-config.yml`: Configuración principal
- `k6-load-test.js`: Script K6 personalizado
- `database-load-test.sh`: Pruebas de base de datos
- `artillery-stress-config.yml`: Pruebas de estrés

### **Variables de Entorno**
```bash
BASE_URL=http://localhost:8080
TEST_DURATION=600
CONCURRENT_USERS=50
RESULTS_DIR=load-testing/results
```

### **Personalización de Escenarios**
- Modificar pesos de escenarios
- Ajustar arrival rates
- Cambiar duración de fases
- Personalizar métricas

## 📋 **Próximos Pasos**

### **Mejoras Planificadas**
1. **WebSocket Testing**: Pruebas de chat en vivo
2. **File Upload Testing**: Pruebas de carga de archivos
3. **CDN Testing**: Pruebas de distribución de contenido
4. **Mobile Testing**: Pruebas específicas para móviles
5. **API Rate Limiting**: Pruebas de límites de API

### **Integración con CI/CD**
- Pruebas automáticas en pipeline
- Alertas por degradación de rendimiento
- Comparación de métricas entre builds
- Reportes automáticos

### **Monitoreo Continuo**
- Pruebas de carga programadas
- Alertas proactivas de rendimiento
- Dashboards de métricas en tiempo real
- Análisis de tendencias

## 🎯 **Beneficios del Sistema**

### **Operacionales**
- ✅ Identificación proactiva de cuellos de botella
- ✅ Validación de capacidad antes del lanzamiento
- ✅ Optimización basada en datos reales
- ✅ Reducción de downtime en producción

### **Técnicos**
- ✅ Métricas de rendimiento objetivas
- ✅ Comparación de versiones
- ✅ Optimización de queries y APIs
- ✅ Escalabilidad validada

### **Negocio**
- ✅ Experiencia de usuario garantizada
- ✅ Capacidad para manejar crecimiento
- ✅ Confianza en la plataforma
- ✅ Decisiones basadas en datos

---

**El sistema de Load Testing está completamente operativo y proporciona validación completa del rendimiento de ComuniArte.** 🚀
