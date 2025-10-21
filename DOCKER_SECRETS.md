# 🔐 Docker Secrets - ComuniArte

## ¿Qué son Docker Secrets?

Docker Secrets es una funcionalidad que permite manejar información sensible (como contraseñas, claves API, certificados) de manera segura en contenedores Docker. Los secrets se almacenan de forma encriptada y solo son accesibles por los servicios que los necesitan.

## 🏗️ Implementación en ComuniArte

### Estructura de Secrets

```
secrets/
├── mongo_root_password.txt    # Contraseña root de MongoDB
├── neo4j_password.txt        # Contraseña de Neo4j
└── minio_password.txt        # Contraseña de MinIO
```

### Configuración en docker-compose.yml

```yaml
version: '3.8'

secrets:
  mongo_root_password:
    file: ./secrets/mongo_root_password.txt
  neo4j_password:
    file: ./secrets/neo4j_password.txt
  minio_password:
    file: ./secrets/minio_password.txt

services:
  mongodb:
    environment:
      MONGO_INITDB_ROOT_PASSWORD_FILE: /run/secrets/mongo_root_password
    secrets:
      - mongo_root_password
```

## 🔒 Ventajas de Seguridad

### ✅ Beneficios

1. **Encriptación**: Los secrets se almacenan encriptados en el sistema de archivos
2. **Acceso Controlado**: Solo los servicios autorizados pueden acceder a cada secret
3. **No en Logs**: Los secrets no aparecen en logs de Docker
4. **Rotación**: Fácil rotación de credenciales sin cambiar código
5. **Auditoría**: Docker registra el acceso a secrets

### ❌ Sin Secrets (Riesgos)

```yaml
# ❌ MALO - Contraseña en texto plano
environment:
  MONGO_INITDB_ROOT_PASSWORD: admin1234  # Visible en logs y código
```

### ✅ Con Secrets (Seguro)

```yaml
# ✅ BUENO - Contraseña en secret
environment:
  MONGO_INITDB_ROOT_PASSWORD_FILE: /run/secrets/mongo_root_password
secrets:
  - mongo_root_password
```

## 🛠️ Comandos de Gestión

### Crear Secrets Manualmente

```bash
# Crear secret desde archivo
echo "nueva_password_segura" | docker secret create mongo_password -

# Crear secret desde stdin
echo "mi_password" | docker secret create neo4j_password -

# Listar secrets
docker secret ls

# Inspeccionar secret
docker secret inspect mongo_password
```

### Rotar Secrets

```bash
# 1. Crear nuevo secret
echo "nueva_password_2025" | docker secret create mongo_password_v2 -

# 2. Actualizar docker-compose.yml
# 3. Reiniciar servicios
docker-compose up -d mongodb
```

## 🔧 Configuración por Entorno

### Desarrollo
```bash
# Usar archivos locales
secrets:
  mongo_password:
    file: ./secrets/dev/mongo_password.txt
```

### Producción
```bash
# Usar secrets de Docker Swarm
secrets:
  mongo_password:
    external: true
```

## 📋 Checklist de Seguridad

- [x] Secrets creados en archivos separados
- [x] Archivos de secrets en .gitignore
- [x] Variables de entorno usando *_FILE
- [x] Secrets montados en /run/secrets/
- [x] Documentación actualizada
- [x] Credenciales por defecto cambiadas

## 🚨 Mejores Prácticas

### 1. **Nunca Committear Secrets**
```bash
# ✅ Correcto
echo "secrets/" >> .gitignore

# ❌ Incorrecto
git add secrets/mongo_password.txt  # NUNCA hacer esto
```

### 2. **Usar Contraseñas Fuertes**
```bash
# Generar contraseña segura
openssl rand -base64 32
```

### 3. **Rotar Regularmente**
```bash
# Script de rotación mensual
#!/bin/bash
echo "Rotando secrets..."
# Crear nuevos secrets
# Actualizar servicios
# Eliminar secrets antiguos
```

### 4. **Monitorear Acceso**
```bash
# Ver logs de acceso a secrets
docker service logs comuniarte-mongodb | grep secret
```

## 🔄 Migración desde Variables de Entorno

### Antes (Inseguro)
```yaml
environment:
  MONGO_INITDB_ROOT_PASSWORD: admin1234
  NEO4J_AUTH: neo4j/admin1234
  MINIO_ROOT_PASSWORD: minioadmin1234
```

### Después (Seguro)
```yaml
environment:
  MONGO_INITDB_ROOT_PASSWORD_FILE: /run/secrets/mongo_root_password
  NEO4J_PASSWORD_FILE: /run/secrets/neo4j_password
  MINIO_ROOT_PASSWORD_FILE: /run/secrets/minio_password
secrets:
  - mongo_root_password
  - neo4j_password
  - minio_password
```

## 📚 Referencias

- [Docker Secrets Documentation](https://docs.docker.com/engine/swarm/secrets/)
- [Docker Compose Secrets](https://docs.docker.com/compose/compose-file/compose-file-v3/#secrets)
- [Security Best Practices](https://docs.docker.com/engine/security/)

---

**Implementado por**: Equipo de Infraestructura ComuniArte  
**Fecha**: Octubre 2025  
**Versión**: 1.0
