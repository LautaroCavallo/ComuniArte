#!/bin/bash

# Script para generar certificados SSL/TLS para ComuniArte
# Genera certificados autofirmados para desarrollo

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🔐 Generador de Certificados SSL/TLS - ComuniArte${NC}"
echo "=================================================="

# Crear directorio para certificados
mkdir -p ssl/certs ssl/private

# Configuración del certificado
COUNTRY="AR"
STATE="Buenos Aires"
CITY="Buenos Aires"
ORG="ComuniArte"
ORG_UNIT="Infrastructure"
COMMON_NAME="comuniarte.local"
EMAIL="admin@comuniarte.local"

echo -e "${YELLOW}Generando certificado SSL autofirmado...${NC}"

# Generar clave privada
openssl genrsa -out ssl/private/comuniarte.key 2048

# Generar certificado
openssl req -new -x509 -key ssl/private/comuniarte.key -out ssl/certs/comuniarte.crt -days 365 -subj "/C=$COUNTRY/ST=$STATE/L=$CITY/O=$ORG/OU=$ORG_UNIT/CN=$COMMON_NAME/emailAddress=$EMAIL"

# Establecer permisos seguros
chmod 600 ssl/private/comuniarte.key
chmod 644 ssl/certs/comuniarte.crt

echo -e "${GREEN}✅ Certificados SSL generados exitosamente${NC}"
echo ""
echo "📋 Archivos creados:"
echo "- ssl/certs/comuniarte.crt (Certificado público)"
echo "- ssl/private/comuniarte.key (Clave privada)"
echo ""
echo "⚠️  IMPORTANTE:"
echo "1. Estos son certificados autofirmados para DESARROLLO"
echo "2. Para producción usar Let's Encrypt o certificados comerciales"
echo "3. Agregar '127.0.0.1 comuniarte.local' a /etc/hosts"
echo ""
echo "🔒 Para producción usar Let's Encrypt:"
echo "certbot --nginx -d comuniarte.com -d www.comuniarte.com"
