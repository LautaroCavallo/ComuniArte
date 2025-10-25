# 📊 Sistema de Real-time Analytics ComuniArte

## 🎯 **Visión General**

ComuniArte implementa un sistema completo de **Real-time Analytics** que proporciona métricas en tiempo real de usuarios, contenido, interacciones y comportamiento de la plataforma, permitiendo tomar decisiones basadas en datos actualizados.

## 🏗️ **Arquitectura del Sistema**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Event Sources │    │   Processing    │    │   Analytics     │
│                 │    │   Layer         │    │   Layer         │
│                 │    │                 │    │                 │
│ • User Events   │───▶│ • Apache Kafka  │───▶│ • Analytics API │
│ • Content Events│    │ • Apache Spark │    │ • Grafana       │
│ • Interactions  │    │ • Redis Streams│    │ • Dashboards    │
│ • System Events │    │                 │    │ • Alerts        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔧 **Componentes Implementados**

### **1. Apache Kafka (Puerto 9092)**
- **Función**: Streaming de eventos en tiempo real
- **Configuración**: `config/kafka-server.properties`
- **Topics**: user-events, content-events, interaction-events, system-events
- **UI**: Kafka UI en puerto 8080

### **2. Apache Spark (Puertos 8081, 8082)**
- **Función**: Procesamiento distribuido en tiempo real
- **Master UI**: Puerto 8081
- **Worker UI**: Puerto 8082
- **Aplicaciones**: `analytics/spark-apps/realtime_analytics.py`

### **3. Redis Streams (Puerto 6380)**
- **Función**: Cache de eventos y métricas
- **Configuración**: `config/redis-streams.conf`
- **Streams**: user-analytics, content-analytics, interaction-analytics

### **4. Analytics API (Puerto 3001)**
- **Función**: API REST para métricas de analytics
- **Endpoints**: /api/analytics/realtime, /api/analytics/users, etc.
- **Tecnología**: Node.js + Express

### **5. Analytics Dashboard (Puerto 3002)**
- **Función**: Visualización de métricas en tiempo real
- **Credenciales**: `admin` / `analytics1234`
- **Dashboards**: Real-time Analytics Dashboard

## 📊 **Métricas de Negocio**

### **Usuarios**
- **Usuarios Activos**: Número de usuarios conectados
- **Total Usuarios**: Usuarios registrados en la plataforma
- **Crecimiento de Usuarios**: Tasa de crecimiento diario
- **Retención**: Usuarios que regresan

### **Contenido**
- **Total Contenido**: Contenido creado en la plataforma
- **Visualizaciones**: Número total de visualizaciones
- **Contenido Popular**: Top 10 contenido más visto
- **Categorías Populares**: Contenido por categoría

### **Interacciones**
- **Total Interacciones**: Likes, comentarios, shares
- **Tasa de Engagement**: Interacciones por visualización
- **Usuarios Más Activos**: Top usuarios por interacciones
- **Tipos de Interacción**: Distribución por tipo

### **Sistema**
- **Tiempo de Respuesta**: Promedio de respuesta de APIs
- **Tasa de Error**: Porcentaje de errores
- **Salud del Sistema**: Estado general de la plataforma
- **Throughput**: Requests por segundo

## 🚀 **Eventos en Tiempo Real**

### **User Events**
```json
{
  "userId": "user123",
  "eventType": "user_login|user_logout|user_registration",
  "timestamp": "2025-10-21T17:30:00Z",
  "data": {
    "ip": "192.168.1.1",
    "userAgent": "Mozilla/5.0...",
    "location": "Buenos Aires, Argentina"
  }
}
```

### **Content Events**
```json
{
  "contentId": "content456",
  "eventType": "content_view|content_created|content_updated",
  "timestamp": "2025-10-21T17:30:00Z",
  "data": {
    "category": "MUSICA",
    "duration": 180,
    "creatorId": "user123"
  }
}
```

### **Interaction Events**
```json
{
  "userId": "user123",
  "contentId": "content456",
  "eventType": "like|comment|share|view",
  "timestamp": "2025-10-21T17:30:00Z",
  "data": {
    "interactionValue": 1,
    "commentText": "Great content!"
  }
}
```

### **System Events**
```json
{
  "eventType": "response_time|error_rate|system_health",
  "timestamp": "2025-10-21T17:30:00Z",
  "data": {
    "responseTime": 150,
    "errorRate": 0.001,
    "cpuUsage": 45.2
  }
}
```

## 📈 **Dashboards Disponibles**

### **Real-time Analytics Dashboard**
- **Usuarios Activos**: Métricas en tiempo real
- **Total Usuarios**: Contador de usuarios registrados
- **Total Contenido**: Contenido creado
- **Total Visualizaciones**: Views acumuladas
- **Usuarios en Tiempo Real**: Gráfico de tendencia
- **Engagement en Tiempo Real**: Interacciones vs views
- **Tiempo de Respuesta**: Performance de APIs
- **Tasa de Error**: Errores del sistema
- **Distribución de Interacciones**: Pie chart de tipos

## 🔌 **API Endpoints**

### **Real-time Metrics**
```
GET /api/analytics/realtime
Response: {
  "activeUsers": 150,
  "totalUsers": 1250,
  "totalContent": 340,
  "totalViews": 12500,
  "totalInteractions": 2800,
  "avgResponseTime": 150,
  "errorRate": 0.001
}
```

### **User Analytics**
```
GET /api/analytics/users?timeRange=1h
Response: {
  "totalUsers": 1250,
  "activeUsers": 150,
  "newUsersToday": 25,
  "userGrowthRate": 0.02,
  "topUsers": [...],
  "userEvents": [...]
}
```

### **Content Analytics**
```
GET /api/analytics/content?timeRange=1h
Response: {
  "totalContent": 340,
  "totalViews": 12500,
  "popularContent": [...],
  "contentEvents": [...]
}
```

### **Interaction Analytics**
```
GET /api/analytics/interactions?timeRange=1h
Response: {
  "totalInteractions": 2800,
  "interactionTypes": {...},
  "topInteractions": [...],
  "interactionEvents": [...]
}
```

### **System Analytics**
```
GET /api/analytics/system?timeRange=1h
Response: {
  "avgResponseTime": 150,
  "errorRate": 0.001,
  "systemHealth": "healthy",
  "systemEvents": [...]
}
```

### **Dashboard Data**
```
GET /api/analytics/dashboard
Response: {
  "realtime": {...},
  "trends": {...},
  "topContent": [...],
  "topUsers": [...]
}
```

## 🚀 **Acceso a los Servicios**

### **Analytics Dashboard**
```
URL: http://localhost:3002
Usuario: admin
Contraseña: analytics1234
```

### **Kafka UI**
```
URL: http://localhost:8080
Gestión: Topics, consumers, producers
```

### **Spark Master UI**
```
URL: http://localhost:8081
Monitoreo: Jobs, executors, applications
```

### **Spark Worker UI**
```
URL: http://localhost:8082
Monitoreo: Worker status, resources
```

### **Analytics API**
```
URL: http://localhost:3001
Documentación: /api/analytics/*
```

## 🔧 **Configuración y Mantenimiento**

### **Archivos de Configuración**
- `config/kafka-server.properties`: Configuración Kafka
- `config/zookeeper.properties`: Configuración Zookeeper
- `config/redis-streams.conf`: Configuración Redis Streams
- `analytics/api/`: API de Analytics
- `analytics/spark-apps/`: Aplicaciones Spark

### **Volúmenes de Datos**
- `zookeeper_data`: Datos de Zookeeper
- `kafka_data`: Datos de Kafka
- `spark_data`: Datos de Spark
- `redis_streams_data`: Datos de Redis Streams
- `analytics_dashboard_data`: Datos de Grafana

### **Comandos Útiles**

#### **Iniciar sistema de analytics**
```bash
docker-compose -f docker-compose-analytics.yml up -d
```

#### **Ver logs de servicios**
```bash
docker-compose -f docker-compose-analytics.yml logs [servicio]
```

#### **Reiniciar servicios**
```bash
docker-compose -f docker-compose-analytics.yml restart [servicio]
```

#### **Verificar estado**
```bash
docker-compose -f docker-compose-analytics.yml ps
```

## 📊 **Procesamiento de Datos**

### **Flujo de Datos**
1. **Eventos** → Kafka Topics
2. **Kafka** → Spark Streaming
3. **Spark** → Redis Streams
4. **Redis** → Analytics API
5. **API** → Grafana Dashboard

### **Transformaciones Spark**
- **Agregaciones**: Count, sum, average por ventana de tiempo
- **Filtros**: Eventos por tipo, usuario, contenido
- **Joins**: Relaciones entre eventos
- **Window Functions**: Métricas por ventana deslizante

### **Almacenamiento**
- **Redis Streams**: Eventos recientes (últimas 24h)
- **MongoDB**: Métricas históricas
- **Kafka**: Buffer de eventos
- **Memory**: Cache de métricas en tiempo real

## 🚨 **Sistema de Alertas**

### **Alertas de Usuarios**
- **Usuarios activos < 10**: Posible problema de conectividad
- **Crecimiento negativo**: Pérdida de usuarios
- **Pico de usuarios**: Posible ataque o viralización

### **Alertas de Contenido**
- **Contenido sin views**: Contenido de baja calidad
- **Views anómalas**: Posible bot o manipulación
- **Categoría sin contenido**: Falta de diversidad

### **Alertas de Sistema**
- **Tiempo de respuesta > 2s**: Performance degradada
- **Tasa de error > 1%**: Problemas técnicos
- **Throughput bajo**: Posible cuello de botella

## 📋 **Próximos Pasos**

### **Mejoras Planificadas**
1. **Machine Learning**: Predicción de tendencias
2. **A/B Testing**: Experimentos controlados
3. **Cohort Analysis**: Análisis de retención
4. **Funnel Analysis**: Análisis de conversión
5. **Heatmaps**: Análisis de comportamiento

### **Integración con Backend**
- **Event Tracking**: SDK para Spring Boot
- **Custom Events**: Eventos específicos de negocio
- **User Segmentation**: Segmentación automática
- **Personalization**: Recomendaciones basadas en analytics

### **Escalabilidad**
- **Kafka Clusters**: Múltiples brokers
- **Spark Clusters**: Procesamiento distribuido
- **Redis Clusters**: Cache distribuido
- **Load Balancing**: Distribución de carga

## 🎯 **Beneficios del Sistema**

### **Operacionales**
- ✅ Métricas en tiempo real
- ✅ Detección proactiva de problemas
- ✅ Optimización basada en datos
- ✅ Decisiones informadas

### **Técnicos**
- ✅ Arquitectura escalable
- ✅ Procesamiento distribuido
- ✅ Cache de alto rendimiento
- ✅ APIs RESTful

### **Negocio**
- ✅ Entendimiento del usuario
- ✅ Optimización de contenido
- ✅ Mejora de engagement
- ✅ Crecimiento sostenible

---

**El sistema de Real-time Analytics está completamente operativo y proporciona insights en tiempo real para ComuniArte.** 🚀
