#!/bin/bash

# ComuniArte Infrastructure Management Scripts
# Scripts para gestionar la infraestructura de ComuniArte

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker and Docker Compose are installed
check_dependencies() {
    print_status "Checking dependencies..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_success "Dependencies check passed"
}

# Start all services
start_infrastructure() {
    print_status "Starting ComuniArte infrastructure..."
    docker-compose up -d
    
    print_status "Waiting for services to be ready..."
    sleep 10
    
    # Check service health
    check_services_health
    
    print_success "Infrastructure started successfully!"
    print_connection_info
}

# Stop all services
stop_infrastructure() {
    print_status "Stopping ComuniArte infrastructure..."
    docker-compose down
    print_success "Infrastructure stopped"
}

# Restart all services
restart_infrastructure() {
    print_status "Restarting ComuniArte infrastructure..."
    docker-compose restart
    print_success "Infrastructure restarted"
}

# Check services health
check_services_health() {
    print_status "Checking services health..."
    
    # MongoDB
    if docker exec comuniarte-mongodb mongosh --eval "db.runCommand('ping')" &> /dev/null; then
        print_success "MongoDB is running"
    else
        print_warning "MongoDB is not responding"
    fi
    
    # Neo4j
    if curl -s http://localhost:7474 &> /dev/null; then
        print_success "Neo4j is running"
    else
        print_warning "Neo4j is not responding"
    fi
    
    # Redis
    if docker exec comuniarte-redis redis-cli ping &> /dev/null; then
        print_success "Redis is running"
    else
        print_warning "Redis is not responding"
    fi
    
    # MinIO
    if curl -s http://localhost:9000/minio/health/live &> /dev/null; then
        print_success "MinIO is running"
    else
        print_warning "MinIO is not responding"
    fi
}

# Print connection information
print_connection_info() {
    echo ""
    print_status "=== ComuniArte Infrastructure Connection Info ==="
    echo ""
    echo "🗄️  MongoDB:"
    echo "   - Host: localhost:27017"
    echo "   - Database: comuniarte_db"
    echo "   - Username: admin"
    echo "   - Password: admin1234"
    echo ""
    echo "🕸️  Neo4j:"
    echo "   - Browser: http://localhost:7474"
    echo "   - Bolt: bolt://localhost:7687"
    echo "   - Username: neo4j"
    echo "   - Password: admin1234"
    echo ""
    echo "⚡ Redis:"
    echo "   - Host: localhost:6379"
    echo "   - Commander: http://localhost:8081"
    echo ""
    echo "📦 MinIO:"
    echo "   - API: http://localhost:9000"
    echo "   - Console: http://localhost:9001"
    echo "   - Username: minioadmin"
    echo "   - Password: minioadmin1234"
    echo ""
}

# Show logs
show_logs() {
    if [ -z "$1" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f "$1"
    fi
}

# Clean up (remove containers and volumes)
cleanup() {
    print_warning "This will remove all containers and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        print_status "Cleaning up infrastructure..."
        docker-compose down -v --remove-orphans
        docker system prune -f
        print_success "Cleanup completed"
    else
        print_status "Cleanup cancelled"
    fi
}

# Main menu
show_menu() {
    echo ""
    echo "🎭 ComuniArte Infrastructure Manager"
    echo "=================================="
    echo "1) Start infrastructure"
    echo "2) Stop infrastructure"
    echo "3) Restart infrastructure"
    echo "4) Check services health"
    echo "5) Show logs"
    echo "6) Show connection info"
    echo "7) Cleanup (remove all data)"
    echo "8) Exit"
    echo ""
}

# Main script logic
main() {
    check_dependencies
    
    if [ $# -eq 0 ]; then
        # Interactive mode
        while true; do
            show_menu
            read -p "Select an option (1-8): " choice
            
            case $choice in
                1) start_infrastructure ;;
                2) stop_infrastructure ;;
                3) restart_infrastructure ;;
                4) check_services_health ;;
                5) 
                    echo "Enter service name (or press Enter for all): "
                    read -r service
                    show_logs "$service"
                    ;;
                6) print_connection_info ;;
                7) cleanup ;;
                8) 
                    print_status "Goodbye!"
                    exit 0
                    ;;
                *) print_error "Invalid option. Please try again." ;;
            esac
        done
    else
        # Command line mode
        case $1 in
            start) start_infrastructure ;;
            stop) stop_infrastructure ;;
            restart) restart_infrastructure ;;
            health) check_services_health ;;
            logs) show_logs "$2" ;;
            info) print_connection_info ;;
            cleanup) cleanup ;;
            *) 
                echo "Usage: $0 {start|stop|restart|health|logs|info|cleanup}"
                exit 1
                ;;
        esac
    fi
}

# Run main function
main "$@"
