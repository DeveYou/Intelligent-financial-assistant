# Quick Reference Guide

## Common Commands

### Build & Start

```bash
# Automated build and start (recommended)
./build-and-run.sh

# Manual: Build services
cd intelligent_financial_assistant_backend/discovery-service && ./mvnw clean package -DskipTests && cd ../..
cd intelligent_financial_assistant_backend/auth-service && ./mvnw clean package -DskipTests && cd ../..
cd intelligent_financial_assistant_backend/transactions-service && ./mvnw clean package -DskipTests && cd ../..
cd intelligent_financial_assistant_backend/api-gateway && ./mvnw clean package -DskipTests && cd ../..

# Start with Docker Compose (after building)
docker compose -f docker-compose.simple.yml up -d

# Or full build in Docker (slow)
docker compose up -d
```

### Service Management

```bash
# Check status
docker compose -f docker-compose.simple.yml ps

# View logs (all services)
docker compose -f docker-compose.simple.yml logs -f

# View logs (specific service)
docker compose -f docker-compose.simple.yml logs -f auth-service

# Restart a service
docker compose -f docker-compose.simple.yml restart auth-service

# Stop all services
docker compose -f docker-compose.simple.yml down

# Stop and remove volumes (deletes data)
docker compose -f docker-compose.simple.yml down -v

# Rebuild a service
docker compose -f docker-compose.simple.yml build auth-service

# Rebuild and restart
docker compose -f docker-compose.simple.yml up -d --build auth-service
```

### User Management

```bash
# Create user (interactive)
./create-user.sh

# Create user (command line)
./create-user.sh -f John -l Doe -e john@example.com -p password123

# Create user (via API)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Database Access

```bash
# Connect to PostgreSQL
docker compose -f docker-compose.simple.yml exec postgres psql -U finance_user -d finance_db

# List users
docker compose -f docker-compose.simple.yml exec postgres \
  psql -U finance_user -d finance_db -c "SELECT id, first_name, last_name, email, role, enabled FROM users;"

# Enable a user
docker compose -f docker-compose.simple.yml exec postgres \
  psql -U finance_user -d finance_db -c "UPDATE users SET enabled = true WHERE email = 'john@example.com';"

# Make user admin
docker compose -f docker-compose.simple.yml exec postgres \
  psql -U finance_user -d finance_db -c "UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'john@example.com';"
```

### Frontend

```bash
# Install dependencies
cd intelligent_financial_assistant_frontend_web
npm install

# Start development server
npm start

# Build for production
npm run build

# The app will be available at http://localhost:4200
```

### Monitoring

```bash
# Check service health
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # Transactions Service
curl http://localhost:8761/actuator/health  # Discovery Service

# Eureka Dashboard
open http://localhost:8761

# Check if services are registered
curl http://localhost:8761/eureka/apps
```

### Testing API

```bash
# Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"test123"}'

# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}' | jq -r '.token')

echo "Token: $TOKEN"

# Use token to access protected endpoint
curl -X GET http://localhost:8080/users/1/profile \
  -H "Authorization: Bearer $TOKEN"

# Get all users (admin only)
curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer $TOKEN"

# Create transaction
curl -X POST http://localhost:8080/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "type": "EXPENSE",
    "category": "Food",
    "description": "Grocery shopping"
  }'

# Get transactions
curl -X GET http://localhost:8080/transactions \
  -H "Authorization: Bearer $TOKEN"
```

### Troubleshooting

```bash
# Check Docker resources
docker system df

# Check running containers
docker ps -a

# Check networks
docker network ls

# Inspect a service
docker compose -f docker-compose.simple.yml logs auth-service | tail -100

# Check if ports are in use
netstat -tuln | grep -E ':(5432|8080|8081|8082|8761|4200)'

# Kill process on port (Linux/Mac)
lsof -ti:8080 | xargs kill -9

# Restart Docker (if needed)
# Linux: sudo systemctl restart docker
# Mac: Restart Docker Desktop
# Windows: Restart Docker Desktop

# Clean up Docker
docker system prune -a --volumes  # WARNING: Removes everything!
```

### Development

```bash
# Run service locally (without Docker)
cd intelligent_financial_assistant_backend/auth-service
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test

# Skip tests during build
./mvnw clean package -DskipTests

# Clean build artifacts
./mvnw clean
```

## Service URLs

| Service | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API Gateway | http://localhost:8080 |
| Auth Service | http://localhost:8081 |
| Transactions Service | http://localhost:8082 |
| Discovery (Eureka) | http://localhost:8761 |
| PostgreSQL | localhost:5432 |

## Environment Variables

Can be set in `docker-compose.simple.yml`:

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/finance_db
  - SPRING_DATASOURCE_USERNAME=finance_user
  - SPRING_DATASOURCE_PASSWORD=1234
  - EUREKA_SERVER=http://discovery-service:8761/eureka
  - SERVER_PORT=8080
  - JWT_SECRET=your-secret-key
  - JWT_EXPIRATION=86400000
```

## Default Credentials

### Database
- Host: localhost:5432
- Database: finance_db
- User: finance_user
- Password: 1234

### Admin User (create manually)
```sql
-- Connect to database and run:
INSERT INTO users (first_name, last_name, email, password, role, enabled, created_at) 
VALUES ('Admin', 'User', 'admin@example.com', 
        '$2a$10$encrypted_password_here', 'ROLE_ADMIN', true, NOW());
```

Note: Password needs to be bcrypt encrypted. Use auth service to create user, then update role to ADMIN.

## Common Issues

### Port Already in Use
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Service Won't Start
```bash
# Check logs
docker compose -f docker-compose.simple.yml logs [service-name]

# Restart service
docker compose -f docker-compose.simple.yml restart [service-name]
```

### Database Connection Failed
```bash
# Check if postgres is healthy
docker compose -f docker-compose.simple.yml ps postgres

# Check postgres logs
docker compose -f docker-compose.simple.yml logs postgres
```

### Services Not Registered with Eureka
```bash
# Wait 30-60 seconds, services need time to register
# Check Eureka dashboard: http://localhost:8761

# Check service logs for Eureka connection errors
docker compose -f docker-compose.simple.yml logs [service-name] | grep -i eureka
```

## Tips

- Always use the simple docker-compose for faster builds: `docker-compose.simple.yml`
- Wait 1-2 minutes after starting for all services to be healthy
- Check Eureka dashboard to verify service registration
- Use the create-user.sh script instead of manual curl commands
- Keep logs open in a separate terminal: `docker compose -f docker-compose.simple.yml logs -f`
- Use `jq` for pretty JSON output: `curl ... | jq`
