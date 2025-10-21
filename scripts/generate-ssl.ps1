# Script para generar certificados SSL/TLS para ComuniArte (PowerShell)
# Genera certificados autofirmados para desarrollo

param(
    [switch]$Force
)

# Colors
$Green = "Green"
$Blue = "Blue"
$Yellow = "Yellow"

Write-Host "🔐 Generador de Certificados SSL/TLS - ComuniArte" -ForegroundColor $Blue
Write-Host "=================================================="

# Crear directorio para certificados
if (-not (Test-Path "ssl")) {
    New-Item -ItemType Directory -Path "ssl" | Out-Null
}
if (-not (Test-Path "ssl/certs")) {
    New-Item -ItemType Directory -Path "ssl/certs" | Out-Null
}
if (-not (Test-Path "ssl/private")) {
    New-Item -ItemType Directory -Path "ssl/private" | Out-Null
}

# Configuración del certificado
$COUNTRY = "AR"
$STATE = "Buenos Aires"
$CITY = "Buenos Aires"
$ORG = "ComuniArte"
$ORG_UNIT = "Infrastructure"
$COMMON_NAME = "comuniarte.local"
$EMAIL = "admin@comuniarte.local"

Write-Host "Generando certificado SSL autofirmado..." -ForegroundColor $Yellow

# Verificar si OpenSSL está disponible
if (-not (Get-Command openssl -ErrorAction SilentlyContinue)) {
    Write-Host "❌ OpenSSL no está instalado. Instalando..." -ForegroundColor Red
    Write-Host "Por favor instala OpenSSL desde: https://slproweb.com/products/Win32OpenSSL.html"
    exit 1
}

# Generar clave privada
openssl genrsa -out ssl/private/comuniarte.key 2048

# Generar certificado
$subject = "/C=$COUNTRY/ST=$STATE/L=$CITY/O=$ORG/OU=$ORG_UNIT/CN=$COMMON_NAME/emailAddress=$EMAIL"
openssl req -new -x509 -key ssl/private/comuniarte.key -out ssl/certs/comuniarte.crt -days 365 -subj $subject

Write-Host "✅ Certificados SSL generados exitosamente" -ForegroundColor $Green
Write-Host ""
Write-Host "📋 Archivos creados:"
Write-Host "- ssl/certs/comuniarte.crt (Certificado público)"
Write-Host "- ssl/private/comuniarte.key (Clave privada)"
Write-Host ""
Write-Host "⚠️  IMPORTANTE:"
Write-Host "1. Estos son certificados autofirmados para DESARROLLO"
Write-Host "2. Para producción usar Let's Encrypt o certificados comerciales"
Write-Host "3. Agregar '127.0.0.1 comuniarte.local' a C:\Windows\System32\drivers\etc\hosts"
Write-Host ""
Write-Host "🔒 Para producción usar Let's Encrypt:"
Write-Host 'certbot --nginx -d comuniarte.com -d www.comuniarte.com'
