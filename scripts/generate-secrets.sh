#!/bin/bash

# Script para generar secrets seguros para ComuniArte
# Genera contraseñas aleatorias y crea los archivos de secrets

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🔐 Generador de Secrets Seguros - ComuniArte${NC}"
echo "=============================================="

# Crear directorio secrets si no existe
mkdir -p secrets

# Función para generar contraseña segura
generate_password() {
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-25
}

echo -e "${YELLOW}Generando contraseñas seguras...${NC}"

# Generar contraseñas
MONGO_PASSWORD=$(generate_password)
NEO4J_PASSWORD=$(generate_password)
MINIO_PASSWORD=$(generate_password)

# Crear archivos de secrets
echo "$MONGO_PASSWORD" > secrets/mongo_root_password.txt
echo "$NEO4J_PASSWORD" > secrets/neo4j_password.txt
echo "$MINIO_PASSWORD" > secrets/minio_password.txt

# Establecer permisos seguros
chmod 600 secrets/*.txt

echo -e "${GREEN}✅ Secrets generados exitosamente${NC}"
echo ""
echo "📋 Contraseñas generadas:"
echo "MongoDB: $MONGO_PASSWORD"
echo "Neo4j:   $NEO4J_PASSWORD"
echo "MinIO:   $MINIO_PASSWORD"
echo ""
echo "🔒 Archivos creados:"
echo "- secrets/mongo_root_password.txt"
echo "- secrets/neo4j_password.txt"
echo "- secrets/minio_password.txt"
echo ""
echo "⚠️  IMPORTANTE:"
echo "1. Guarda estas contraseñas en un lugar seguro"
echo "2. Los archivos están protegidos (chmod 600)"
echo "3. Los archivos están en .gitignore"
echo ""
echo "🚀 Para usar los secrets:"
echo "docker-compose up -d"
