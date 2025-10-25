# 📊 Sistema de Monitoreo ComuniArte

## 🎯 **Visión General**

ComuniArte implementa un sistema de monitoreo completo basado en **Prometheus + Grafana** que proporciona visibilidad total de la infraestructura, métricas de rendimiento y alertas proactivas.

## 🏗️ **Arquitectura del Sistema**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Exporters     │    │   Prometheus    │    │     Grafana     │
│                 │    │                 │    │                 │
│ • Node          │───▶│ • Scraping      │───▶│ • Dashboards    │
│ • MongoDB       │    │ • Storage       │    │ • Visualization │
│ • Redis         │    │ • Alerting      │    │ • Alerts        │
│ • Neo4j         │    │ • Rules         │    │                 │
│ • MinIO         │    │                 │    │                 │
│ • Nginx         │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Alertmanager   │
                       │                 │
                       │ • Notifications │
                       │ • Routing       │
                       │ • Grouping      │
                       └─────────────────┘
```

## 🔧 **Componentes Implementados**

### **1. Prometheus (Puerto 9090)**
- **Función**: Recolección y almacenamiento de métricas
- **Configuración**: `config/prometheus.yml`
- **Retención**: 200 horas de datos
- **Scraping**: Cada 15 segundos

### **2. Grafana (Puerto 3000)**
- **Función**: Visualización y dashboards
- **Credenciales**: `admin` / `admin1234`
- **Dashboards**: Pre-configurados para ComuniArte
- **Datasource**: Prometheus automático

### **3. Alertmanager (Puerto 9093)**
- **Función**: Gestión de alertas
- **Configuración**: `config/alertmanager.yml`
- **Notificaciones**: Email y webhook

### **4. Exporters**

#### **Node Exporter (Puerto 9100)**
- Métricas del sistema operativo
- CPU, memoria, disco, red
- Procesos y archivos

#### **MongoDB Exporter (Puerto 9216)**
- Conexiones activas
- Operaciones por segundo
- Tamaño de base de datos
- Índices y colecciones

#### **Redis Exporter (Puerto 9121)**
- Memoria utilizada
- Conexiones activas
- Comandos por segundo
- Claves expiradas

#### **MinIO Exporter (Puerto 9290)**
- Almacenamiento utilizado
- Requests por segundo
- Errores de API
- Métricas de buckets

#### **Neo4j Exporter (Puerto 2004)**
- Métricas JMX
- Transacciones por segundo
- Memoria heap
- Conexiones de base de datos

#### **Nginx Exporter (Puerto 9113)**
- Requests por segundo
- Tiempo de respuesta
- Códigos de estado HTTP
- Conexiones activas

## 📈 **Dashboards Disponibles**

### **ComuniArte Infrastructure Dashboard**
- **CPU Usage**: Utilización de procesador
- **Memory Usage**: Uso de memoria RAM
- **Service Status**: Estado de todos los servicios
- **Real-time Metrics**: Métricas en tiempo real

### **Métricas Clave Monitoreadas**

#### **Sistema**
- CPU: >80% = Warning
- Memoria: >85% = Warning  
- Disco: <10% = Critical

#### **Servicios**
- MongoDB: Down = Critical
- Redis: Down = Critical
- Neo4j: Down = Critical
- MinIO: Down = Critical
- Nginx: Down = Critical

#### **Aplicación**
- Response Time: >2s = Warning
- Error Rate: >5% = Warning

## 🚨 **Sistema de Alertas**

### **Niveles de Severidad**
- **Critical**: Servicios caídos, disco lleno
- **Warning**: Alto uso de recursos, tiempo de respuesta lento

### **Canales de Notificación**
- **Email**: `admin@comuniarte.com`
- **Webhook**: `http://localhost:5001/`

### **Reglas de Alertas**
- **Infraestructura**: CPU, memoria, disco
- **Servicios**: Estado de conexión
- **Aplicación**: Rendimiento y errores

## 🚀 **Acceso a los Servicios**

### **Grafana Dashboard**
```
URL: http://localhost:3000
```

### **Prometheus**
```
URL: http://localhost:9090
Consulta: PromQL queries
```

### **Alertmanager**
```
URL: http://localhost:9093
Gestión: Alertas activas
```

## 📊 **Métricas Específicas de ComuniArte**

### **MongoDB**
- `mongodb_connections_current`: Conexiones activas
- `mongodb_opcounters_*`: Operaciones por tipo
- `mongodb_db_size_bytes`: Tamaño de base de datos

### **Redis**
- `redis_connected_clients`: Clientes conectados
- `redis_commands_processed_total`: Comandos procesados
- `redis_memory_used_bytes`: Memoria utilizada

### **Neo4j**
- `neo4j_transaction_active`: Transacciones activas
- `neo4j_heap_used_bytes`: Memoria heap utilizada
- `neo4j_db_size_bytes`: Tamaño de base de datos

### **MinIO**
- `minio_disk_usage_percent`: Uso de disco
- `minio_requests_total`: Requests totales
- `minio_errors_total`: Errores por tipo

## 🔧 **Configuración y Mantenimiento**

### **Archivos de Configuración**
- `config/prometheus.yml`: Configuración principal
- `config/alert_rules.yml`: Reglas de alertas
- `config/alertmanager.yml`: Configuración de alertas
- `config/grafana/`: Configuración de Grafana

### **Volúmenes de Datos**
- `prometheus_data`: Métricas históricas
- `grafana_data`: Dashboards y configuración
- `alertmanager_data`: Estado de alertas

### **Comandos Útiles**

#### **Reiniciar servicios de monitoreo**
```bash
docker-compose restart prometheus grafana alertmanager
```

#### **Ver logs de Prometheus**
```bash
docker-compose logs prometheus
```

#### **Verificar métricas**
```bash
curl http://localhost:9090/api/v1/targets
```

## 📋 **Próximos Pasos**

### **Mejoras Planificadas**
1. **Dashboards adicionales**: Métricas específicas de aplicación
2. **Alertas avanzadas**: Basadas en patrones de uso
3. **Integración con Slack**: Notificaciones en tiempo real
4. **Métricas de negocio**: Usuarios activos, contenido subido
5. **Capacity planning**: Predicción de recursos necesarios

### **Monitoreo de Aplicación**
- Métricas de Spring Boot Actuator
- Métricas personalizadas de ComuniArte
- Trazabilidad de requests
- Métricas de negocio

## 🎯 **Beneficios del Sistema**

### **Operacionales**
- ✅ Visibilidad completa de la infraestructura
- ✅ Detección proactiva de problemas
- ✅ Alertas automáticas por email/webhook
- ✅ Dashboards en tiempo real

### **Técnicos**
- ✅ Métricas históricas para análisis
- ✅ Capacidad de escalar basada en datos
- ✅ Troubleshooting más rápido
- ✅ Optimización basada en métricas

### **Negocio**
- ✅ Disponibilidad garantizada
- ✅ Performance optimizada
- ✅ Experiencia de usuario mejorada
- ✅ Decisiones basadas en datos

---

**El sistema de monitoreo está completamente operativo y proporciona visibilidad total de la infraestructura ComuniArte.** 🚀
