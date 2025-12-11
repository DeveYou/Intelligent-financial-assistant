# Intelligent Financial Assistant

A microservices-based financial assistant application built with Spring Boot backend and Angular frontend.

## Architecture

The application consists of the following microservices:

- **Discovery Service** (Eureka Server) - Port 8761: Service registry for microservices
- **API Gateway** - Port 8080: Entry point for all client requests
- **Auth Service** - Port 8081: Handles authentication, authorization, and user management
- **Transactions Service** - Port 8082: Manages financial transactions
- **PostgreSQL Database** - Port 5432: Persistent data storage
- **Frontend Web** - Port 4200: Angular web application

## Prerequisites

- Docker and Docker Compose installed
- (Optional) Node.js and npm for running frontend in development mode
- (Optional) Java 17 and Maven for local development

## Quick Start with Docker Compose

### Option 1: Automated Build and Run (Recommended)

Use the provided script that handles building and starting all services:

```bash
./build-and-run.sh
```

This will:
- Check prerequisites (Java 17+, Docker)
- Build all microservices locally
- Optionally start all services with Docker Compose
- Provide next steps

### Option 2: Manual Docker Compose

From the root directory, run:

```bash
docker compose up -d
```

**Note**: This requires the services to be built first, or the Docker images will be built which may take time and requires internet connectivity for Maven dependencies.

### Services Started

Both methods will:
- Start PostgreSQL database
- Start Discovery Service (Eureka)
- Start Auth Service
- Start Transactions Service  
- Start API Gateway

### 2. Check Services Status

Wait for all services to be healthy (this may take 1-2 minutes):

```bash
docker-compose ps
```

You can also check:
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080/actuator/health
- Auth Service: http://localhost:8081/actuator/health
- Transactions Service: http://localhost:8082/actuator/health

### 3. View Logs

To view logs from all services:

```bash
docker-compose logs -f
```

To view logs from a specific service:

```bash
docker-compose logs -f auth-service
```

### 4. Create a New User

You can create a new user using the provided script:

```bash
./create-user.sh
```

Or with command-line arguments:

```bash
./create-user.sh -f John -l Doe -e john@example.com -p password123
```

Or using curl directly:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890",
    "address": "123 Main St"
  }'
```

### 5. Run the Frontend

#### Option A: Using Docker (recommended for production)

From the `intelligent_financial_assistant_frontend_web` directory:

```bash
cd intelligent_financial_assistant_frontend_web
npm install
npm start
```

The frontend will be available at: http://localhost:4200

#### Option B: Development mode

```bash
cd intelligent_financial_assistant_frontend_web
npm install
ng serve
```

### 6. Access the Application

Open your browser and navigate to: http://localhost:4200

Login with the credentials of the user you created.

## Stopping the Application

To stop all services:

```bash
docker-compose down
```

To stop and remove volumes (will delete database data):

```bash
docker-compose down -v
```

## API Documentation

Once the services are running, you can access the API documentation:

- Auth Service: http://localhost:8081/swagger-ui.html
- Transactions Service: http://localhost:8082/swagger-ui.html

## Available API Endpoints

### Authentication (via API Gateway at :8080)

- `POST /auth/register` - Register a new user
- `POST /auth/login` - Login and get JWT token
- `POST /auth/logout` - Logout (invalidate token)
- `GET /auth/validate-token` - Validate a JWT token

### User Management (via API Gateway at :8080)

- `GET /admin/users` - Get all users (admin only)
- `POST /admin/users` - Create a user (admin only)
- `GET /admin/users/{userId}` - Get user by ID (admin only)
- `PATCH /admin/users/{userId}` - Update user (admin only)
- `DELETE /admin/users/{userId}` - Delete user (admin only)
- `PATCH /users/{userId}/profile` - Update own profile

### Transactions (via API Gateway at :8080)

- `GET /transactions` - Get all transactions
- `POST /transactions` - Create a new transaction
- `GET /transactions/{id}` - Get transaction by ID
- `PUT /transactions/{id}` - Update transaction
- `DELETE /transactions/{id}` - Delete transaction

## Configuration

### Environment Variables

You can customize the services by setting environment variables in the `docker-compose.yml` file:

- `POSTGRES_DB` - Database name
- `POSTGRES_USER` - Database user
- `POSTGRES_PASSWORD` - Database password
- `EUREKA_SERVER` - Eureka server URL
- `SERVER_PORT` - Service port

### Database Configuration

The default database credentials are:
- Database: `finance_db`
- User: `finance_user`
- Password: `1234`

⚠️ **Important**: Change these credentials in production!

## Development

### Building Individual Services

To build a specific service:

```bash
cd intelligent_financial_assistant_backend/auth-service
./mvnw clean package
```

### Running Services Locally (without Docker)

1. Start PostgreSQL locally or use Docker:
   ```bash
   docker run -d -p 5432:5432 -e POSTGRES_DB=finance_db -e POSTGRES_USER=finance_user -e POSTGRES_PASSWORD=1234 postgres:14
   ```

2. Start services in order:
   ```bash
   # Discovery Service
   cd intelligent_financial_assistant_backend/discovery-service
   ./mvnw spring-boot:run
   
   # Auth Service
   cd intelligent_financial_assistant_backend/auth-service
   ./mvnw spring-boot:run
   
   # Transactions Service
   cd intelligent_financial_assistant_backend/transactions-service
   ./mvnw spring-boot:run
   
   # API Gateway
   cd intelligent_financial_assistant_backend/api-gateway
   ./mvnw spring-boot:run
   ```

## Troubleshooting

### Services not starting

Check the logs:
```bash
docker compose logs -f [service-name]
```

### Docker build fails with network issues

If you encounter Maven download errors during Docker build, you have two options:

1. **Build services locally first** (recommended for development):
   ```bash
   # Build all services
   cd intelligent_financial_assistant_backend/discovery-service && ./mvnw clean package -DskipTests
   cd ../auth-service && ./mvnw clean package -DskipTests
   cd ../transactions-service && ./mvnw clean package -DskipTests
   cd ../api-gateway && ./mvnw clean package -DskipTests
   
   # Then copy JARs and use simplified Dockerfiles
   ```

2. **Use pre-built JARs**: If JARs exist in `target/` directories, create simplified Dockerfiles that just copy the JAR

3. **Run services locally without Docker**: See the "Running Services Locally" section below

### Port conflicts

If ports are already in use, you can change them in `docker-compose.yml`

### Database connection issues

Make sure PostgreSQL is healthy:
```bash
docker compose ps postgres
```

### Eureka registration issues

Wait for all services to register (may take up to 2 minutes). Check the Eureka dashboard at http://localhost:8761

### Cannot create user

Make sure:
1. All services are running and healthy
2. PostgreSQL is accessible
3. Auth service is registered with Eureka
4. API Gateway can reach the auth service

## Project Structure

```
.
├── docker-compose.yml                          # Main orchestration file
├── create-user.sh                              # User creation script
├── intelligent_financial_assistant_backend/
│   ├── discovery-service/                     # Eureka server
│   ├── api-gateway/                           # API Gateway
│   ├── auth-service/                          # Authentication service
│   └── transactions-service/                  # Transactions service
└── intelligent_financial_assistant_frontend_web/ # Angular frontend
```

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on the GitHub repository.
