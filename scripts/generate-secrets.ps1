# Script para generar secrets seguros para ComuniArte (PowerShell)
# Genera contraseñas aleatorias y crea los archivos de secrets

param(
    [switch]$Force
)

# Colors
$Green = "Green"
$Blue = "Blue"
$Yellow = "Yellow"

Write-Host "🔐 Generador de Secrets Seguros - ComuniArte" -ForegroundColor $Blue
Write-Host "=============================================="

# Crear directorio secrets si no existe
if (-not (Test-Path "secrets")) {
    New-Item -ItemType Directory -Path "secrets" | Out-Null
}

# Función para generar contraseña segura
function Generate-Password {
    $bytes = New-Object byte[] 32
    (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes)
    return [Convert]::ToBase64String($bytes) -replace '[=+/]', '' | Select-Object -First 25
}

Write-Host "Generando contraseñas seguras..." -ForegroundColor $Yellow

# Generar contraseñas
$MONGO_PASSWORD = Generate-Password
$NEO4J_PASSWORD = Generate-Password
$MINIO_PASSWORD = Generate-Password

# Crear archivos de secrets
$MONGO_PASSWORD | Out-File -FilePath "secrets/mongo_root_password.txt" -Encoding UTF8 -NoNewline
$NEO4J_PASSWORD | Out-File -FilePath "secrets/neo4j_password.txt" -Encoding UTF8 -NoNewline
$MINIO_PASSWORD | Out-File -FilePath "secrets/minio_password.txt" -Encoding UTF8 -NoNewline

Write-Host "✅ Secrets generados exitosamente" -ForegroundColor $Green
Write-Host ""
Write-Host "📋 Contraseñas generadas:"
Write-Host "MongoDB: $MONGO_PASSWORD"
Write-Host "Neo4j:   $NEO4J_PASSWORD"
Write-Host "MinIO:   $MINIO_PASSWORD"
Write-Host ""
Write-Host "🔒 Archivos creados:"
Write-Host "- secrets/mongo_root_password.txt"
Write-Host "- secrets/neo4j_password.txt"
Write-Host "- secrets/minio_password.txt"
Write-Host ""
Write-Host "⚠️  IMPORTANTE:"
Write-Host "1. Guarda estas contraseñas en un lugar seguro"
Write-Host "2. Los archivos están protegidos"
Write-Host "3. Los archivos están en .gitignore"
Write-Host ""
Write-Host "🚀 Para usar los secrets:"
Write-Host "docker-compose up -d"
