#!/bin/bash

# Script de Monitoreo de Seguridad ComuniArte
# Detecta vulnerabilidades y ataques en tiempo real

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuración
LOG_DIR="/var/log/comuniarte-security"
ALERT_EMAIL="admin@comuniarte.local"
THRESHOLD_FAILED_LOGINS=5
THRESHOLD_RATE_LIMIT=100

echo -e "${BLUE}🛡️ Monitoreo de Seguridad ComuniArte${NC}"
echo "======================================"

# Crear directorio de logs si no existe
mkdir -p $LOG_DIR

# Función para enviar alertas
send_alert() {
    local severity=$1
    local message=$2
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    echo "[$timestamp] [$severity] $message" >> $LOG_DIR/security-alerts.log
    
    if [ "$severity" = "CRITICAL" ]; then
        echo -e "${RED}🚨 ALERTA CRÍTICA: $message${NC}"
        # Enviar email (requiere configuración SMTP)
        # echo "$message" | mail -s "ComuniArte Security Alert" $ALERT_EMAIL
    elif [ "$severity" = "WARNING" ]; then
        echo -e "${YELLOW}⚠️ ADVERTENCIA: $message${NC}"
    else
        echo -e "${GREEN}ℹ️ INFO: $message${NC}"
    fi
}

# Función para verificar vulnerabilidades conocidas
check_vulnerabilities() {
    echo -e "${BLUE}🔍 Verificando vulnerabilidades conocidas...${NC}"
    
    # CVE-2021-44228 (Log4Shell)
    if docker exec comuniarte-backend grep -r "log4j" /app 2>/dev/null | grep -q "2\.[0-9]\|1\.[0-9]"; then
        send_alert "CRITICAL" "Posible vulnerabilidad Log4Shell detectada en backend"
    fi
    
    # CVE-2021-45046 (Log4Shell follow-up)
    if docker exec comuniarte-backend find /app -name "*.jar" -exec grep -l "log4j" {} \; 2>/dev/null | grep -q "log4j"; then
        send_alert "WARNING" "Archivos JAR con Log4j encontrados"
    fi
    
    # Verificar versiones de Docker
    docker_version=$(docker version --format '{{.Server.Version}}')
    if [[ "$docker_version" < "20.10.0" ]]; then
        send_alert "WARNING" "Docker versión antigua detectada: $docker_version"
    fi
    
    # Verificar imágenes con vulnerabilidades
    if docker images | grep -q "nginx:1\.[0-9]\|nginx:1\.1[0-9]\|nginx:1\.20"; then
        send_alert "WARNING" "Nginx versión antigua detectada"
    fi
}

# Función para monitorear intentos de login fallidos
monitor_failed_logins() {
    echo -e "${BLUE}🔐 Monitoreando intentos de login fallidos...${NC}"
    
    # MongoDB
    failed_mongo=$(docker logs comuniarte-mongodb 2>&1 | grep -c "Authentication failed" | tail -1)
    if [ "$failed_mongo" -gt $THRESHOLD_FAILED_LOGINS ]; then
        send_alert "WARNING" "Múltiples intentos de login fallidos en MongoDB: $failed_mongo"
    fi
    
    # Neo4j
    failed_neo4j=$(docker logs comuniarte-neo4j 2>&1 | grep -c "Invalid credentials" | tail -1)
    if [ "$failed_neo4j" -gt $THRESHOLD_FAILED_LOGINS ]; then
        send_alert "WARNING" "Múltiples intentos de login fallidos en Neo4j: $failed_neo4j"
    fi
    
    # Redis
    failed_redis=$(docker logs comuniarte-redis 2>&1 | grep -c "NOAUTH" | tail -1)
    if [ "$failed_redis" -gt $THRESHOLD_FAILED_LOGINS ]; then
        send_alert "WARNING" "Múltiples intentos de acceso no autorizado en Redis: $failed_redis"
    fi
}

# Función para monitorear rate limiting
monitor_rate_limiting() {
    echo -e "${BLUE}📊 Monitoreando rate limiting...${NC}"
    
    # Nginx rate limiting
    rate_limit_hits=$(docker logs comuniarte-nginx 2>&1 | grep -c "limiting requests" | tail -1)
    if [ "$rate_limit_hits" -gt $THRESHOLD_RATE_LIMIT ]; then
        send_alert "WARNING" "Alto número de requests bloqueados por rate limiting: $rate_limit_hits"
    fi
    
    # Fail2ban bans
    fail2ban_bans=$(docker logs comuniarte-fail2ban 2>&1 | grep -c "Ban" | tail -1)
    if [ "$fail2ban_bans" -gt 10 ]; then
        send_alert "WARNING" "Múltiples IPs bloqueadas por Fail2ban: $fail2ban_bans"
    fi
}

# Función para verificar integridad de archivos
check_file_integrity() {
    echo -e "${BLUE}🔍 Verificando integridad de archivos...${NC}"
    
    # Verificar certificados SSL
    if [ -f "ssl/certs/comuniarte.crt" ]; then
        cert_expiry=$(openssl x509 -in ssl/certs/comuniarte.crt -noout -dates | grep notAfter | cut -d= -f2)
        cert_expiry_epoch=$(date -d "$cert_expiry" +%s)
        current_epoch=$(date +%s)
        days_until_expiry=$(( (cert_expiry_epoch - current_epoch) / 86400 ))
        
        if [ "$days_until_expiry" -lt 30 ]; then
            send_alert "WARNING" "Certificado SSL expira en $days_until_expiry días"
        fi
    fi
    
    # Verificar permisos de archivos sensibles
    if [ -f "secrets/mongo_root_password.txt" ]; then
        perms=$(stat -c %a secrets/mongo_root_password.txt)
        if [ "$perms" != "600" ]; then
            send_alert "WARNING" "Permisos inseguros en secrets: $perms (debería ser 600)"
        fi
    fi
}

# Función para verificar recursos del sistema
check_system_resources() {
    echo -e "${BLUE}💻 Verificando recursos del sistema...${NC}"
    
    # Memoria
    memory_usage=$(free | grep Mem | awk '{printf "%.0f", $3/$2 * 100.0}')
    if [ "$memory_usage" -gt 90 ]; then
        send_alert "WARNING" "Uso de memoria alto: $memory_usage%"
    fi
    
    # CPU
    cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
    if [ "$cpu_usage" -gt 90 ]; then
        send_alert "WARNING" "Uso de CPU alto: $cpu_usage%"
    fi
    
    # Espacio en disco
    disk_usage=$(df / | tail -1 | awk '{print $5}' | cut -d'%' -f1)
    if [ "$disk_usage" -gt 90 ]; then
        send_alert "CRITICAL" "Espacio en disco bajo: $disk_usage%"
    fi
}

# Función para generar reporte de seguridad
generate_security_report() {
    local report_file="$LOG_DIR/security-report-$(date +%Y%m%d-%H%M%S).txt"
    
    echo "=== REPORTE DE SEGURIDAD COMIARTE ===" > $report_file
    echo "Fecha: $(date)" >> $report_file
    echo "=====================================" >> $report_file
    echo "" >> $report_file
    
    echo "=== ESTADO DE CONTENEDORES ===" >> $report_file
    docker-compose ps >> $report_file
    echo "" >> $report_file
    
    echo "=== LOGS DE SEGURIDAD ===" >> $report_file
    docker logs comuniarte-nginx 2>&1 | grep -i "error\|warn\|fail" | tail -20 >> $report_file
    echo "" >> $report_file
    
    echo "=== INTENTOS DE ACCESO FALLIDOS ===" >> $report_file
    docker logs comuniarte-mongodb 2>&1 | grep -i "auth" | tail -10 >> $report_file
    echo "" >> $report_file
    
    echo "=== IPs BLOQUEADAS ===" >> $report_file
    docker logs comuniarte-fail2ban 2>&1 | grep -i "ban" | tail -10 >> $report_file
    echo "" >> $report_file
    
    echo "Reporte generado: $report_file"
}

# Función principal
main() {
    echo -e "${GREEN}Iniciando monitoreo de seguridad...${NC}"
    
    check_vulnerabilities
    monitor_failed_logins
    monitor_rate_limiting
    check_file_integrity
    check_system_resources
    
    if [ "$1" = "--report" ]; then
        generate_security_report
    fi
    
    echo -e "${GREEN}Monitoreo completado${NC}"
}

# Ejecutar monitoreo
main "$@"
