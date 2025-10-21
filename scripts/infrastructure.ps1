# ComuniArte Infrastructure Management Script (PowerShell)
# Scripts para gestionar la infraestructura de ComuniArte en Windows

param(
    [Parameter(Position=0)]
    [string]$Action = "menu"
)

# Colors for output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"

# Function to print colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Red
}

# Check if Docker and Docker Compose are installed
function Test-Dependencies {
    Write-Status "Checking dependencies..."
    
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Error "Docker is not installed. Please install Docker Desktop first."
        exit 1
    }
    
    if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        Write-Error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    }
    
    Write-Success "Dependencies check passed"
}

# Start all services
function Start-Infrastructure {
    Write-Status "Starting ComuniArte infrastructure..."
    docker-compose up -d
    
    Write-Status "Waiting for services to be ready..."
    Start-Sleep -Seconds 10
    
    # Check service health
    Test-ServicesHealth
    
    Write-Success "Infrastructure started successfully!"
    Show-ConnectionInfo
}

# Stop all services
function Stop-Infrastructure {
    Write-Status "Stopping ComuniArte infrastructure..."
    docker-compose down
    Write-Success "Infrastructure stopped"
}

# Restart all services
function Restart-Infrastructure {
    Write-Status "Restarting ComuniArte infrastructure..."
    docker-compose restart
    Write-Success "Infrastructure restarted"
}

# Check services health
function Test-ServicesHealth {
    Write-Status "Checking services health..."
    
    # MongoDB
    try {
        docker exec comuniarte-mongodb mongosh --eval "db.runCommand('ping')" 2>$null
        Write-Success "MongoDB is running"
    } catch {
        Write-Warning "MongoDB is not responding"
    }
    
    # Neo4j
    try {
        Invoke-WebRequest -Uri "http://localhost:7474" -UseBasicParsing -TimeoutSec 5 | Out-Null
        Write-Success "Neo4j is running"
    } catch {
        Write-Warning "Neo4j is not responding"
    }
    
    # Redis
    try {
        docker exec comuniarte-redis redis-cli ping 2>$null
        Write-Success "Redis is running"
    } catch {
        Write-Warning "Redis is not responding"
    }
    
    # MinIO
    try {
        Invoke-WebRequest -Uri "http://localhost:9000/minio/health/live" -UseBasicParsing -TimeoutSec 5 | Out-Null
        Write-Success "MinIO is running"
    } catch {
        Write-Warning "MinIO is not responding"
    }
}

# Print connection information
function Show-ConnectionInfo {
    Write-Host ""
    Write-Status "=== ComuniArte Infrastructure Connection Info ==="
    Write-Host ""
    Write-Host "🗄️  MongoDB:" -ForegroundColor $Blue
    Write-Host "   - Host: localhost:27017"
    Write-Host "   - Database: comuniarte_db"
    Write-Host "   - Username: admin"
    Write-Host "   - Password: admin1234"
    Write-Host ""
    Write-Host "🕸️  Neo4j:" -ForegroundColor $Blue
    Write-Host "   - Browser: http://localhost:7474"
    Write-Host "   - Bolt: bolt://localhost:7687"
    Write-Host "   - Username: neo4j"
    Write-Host "   - Password: admin1234"
    Write-Host ""
    Write-Host "⚡ Redis:" -ForegroundColor $Blue
    Write-Host "   - Host: localhost:6379"
    Write-Host "   - Commander: http://localhost:8081"
    Write-Host ""
    Write-Host "📦 MinIO:" -ForegroundColor $Blue
    Write-Host "   - API: http://localhost:9000"
    Write-Host "   - Console: http://localhost:9001"
    Write-Host "   - Username: minioadmin"
    Write-Host "   - Password: minioadmin1234"
    Write-Host ""
}

# Show logs
function Show-Logs {
    param([string]$Service = "")
    
    if ($Service -eq "") {
        docker-compose logs -f
    } else {
        docker-compose logs -f $Service
    }
}

# Clean up (remove containers and volumes)
function Remove-Infrastructure {
    Write-Warning "This will remove all containers and volumes. Are you sure? (y/N)"
    $response = Read-Host
    
    if ($response -match "^[Yy]$") {
        Write-Status "Cleaning up infrastructure..."
        docker-compose down -v --remove-orphans
        docker system prune -f
        Write-Success "Cleanup completed"
    } else {
        Write-Status "Cleanup cancelled"
    }
}

# Main menu
function Show-Menu {
    Write-Host ""
    Write-Host "🎭 ComuniArte Infrastructure Manager" -ForegroundColor $Blue
    Write-Host "=================================="
    Write-Host "1) Start infrastructure"
    Write-Host "2) Stop infrastructure"
    Write-Host "3) Restart infrastructure"
    Write-Host "4) Check services health"
    Write-Host "5) Show logs"
    Write-Host "6) Show connection info"
    Write-Host "7) Cleanup (remove all data)"
    Write-Host "8) Exit"
    Write-Host ""
}

# Main script logic
function Main {
    Test-Dependencies
    
    if ($Action -eq "menu") {
        # Interactive mode
        do {
            Show-Menu
            $choice = Read-Host "Select an option (1-8)"
            
            switch ($choice) {
                "1" { Start-Infrastructure }
                "2" { Stop-Infrastructure }
                "3" { Restart-Infrastructure }
                "4" { Test-ServicesHealth }
                "5" { 
                    $service = Read-Host "Enter service name (or press Enter for all)"
                    Show-Logs $service
                }
                "6" { Show-ConnectionInfo }
                "7" { Remove-Infrastructure }
                "8" { 
                    Write-Status "Goodbye!"
                    exit 0
                }
                default { Write-Error "Invalid option. Please try again." }
            }
        } while ($true)
    } else {
        # Command line mode
        switch ($Action) {
            "start" { Start-Infrastructure }
            "stop" { Stop-Infrastructure }
            "restart" { Restart-Infrastructure }
            "health" { Test-ServicesHealth }
            "logs" { Show-Logs $args[0] }
            "info" { Show-ConnectionInfo }
            "cleanup" { Remove-Infrastructure }
            default { 
                Write-Host "Usage: .\infrastructure.ps1 {start|stop|restart|health|logs|info|cleanup}"
                exit 1
            }
        }
    }
}

# Run main function
Main
