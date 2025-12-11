#!/bin/bash

set -e

echo "=========================================="
echo "Building Intelligent Financial Assistant"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Change to project root
cd "$(dirname "$0")"

# Check prerequisites
print_info "Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed"
    exit 1
fi
print_success "Docker is installed"

if ! command -v java &> /dev/null; then
    print_error "Java is not installed (Java 17 required)"
    exit 1
fi
print_success "Java is installed"

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' | head -1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 or higher is required (found Java $JAVA_VERSION)"
    exit 1
fi
print_success "Java version is compatible (Java $JAVA_VERSION)"

echo ""
print_info "Building backend services..."
echo ""

# Build Discovery Service
print_info "Building discovery-service..."
cd intelligent_financial_assistant_backend/discovery-service
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    print_success "discovery-service built successfully"
else
    print_error "Failed to build discovery-service"
    exit 1
fi
cd ../..

# Build Auth Service
print_info "Building auth-service..."
cd intelligent_financial_assistant_backend/auth-service
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    print_success "auth-service built successfully"
else
    print_error "Failed to build auth-service"
    exit 1
fi
cd ../..

# Build Transactions Service
print_info "Building transactions-service..."
cd intelligent_financial_assistant_backend/transactions-service
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    print_success "transactions-service built successfully"
else
    print_error "Failed to build transactions-service"
    exit 1
fi
cd ../..

# Build API Gateway
print_info "Building api-gateway..."
cd intelligent_financial_assistant_backend/api-gateway
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    print_success "api-gateway built successfully"
else
    print_error "Failed to build api-gateway"
    exit 1
fi
cd ../..

echo ""
print_success "All services built successfully!"
echo ""

# Ask user if they want to start services with Docker Compose
read -p "Do you want to start all services with Docker Compose? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_info "Starting services with Docker Compose..."
    docker compose -f docker-compose.simple.yml up -d
    
    echo ""
    print_success "Services are starting!"
    echo ""
    print_info "You can check the status with: docker compose ps"
    print_info "View logs with: docker compose logs -f"
    print_info "Eureka Dashboard: http://localhost:8761"
    print_info "API Gateway: http://localhost:8080"
    echo ""
    print_info "Waiting for services to be ready (this may take 1-2 minutes)..."
    sleep 10
    
    # Check if services are running
    docker compose ps
fi

echo ""
print_info "Next steps:"
echo "  1. Wait for all services to be healthy (check with: docker compose ps)"
echo "  2. Create a user with: ./create-user.sh"
echo "  3. Start the frontend: cd intelligent_financial_assistant_frontend_web && npm install && npm start"
echo "  4. Access the application at: http://localhost:4200"
echo ""
