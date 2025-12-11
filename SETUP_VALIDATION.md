# Setup Validation Guide

This document helps you validate that the Intelligent Financial Assistant application is set up correctly.

## Prerequisites Check

Before starting, verify you have:

```bash
# Check Docker
docker --version
# Expected: Docker version 20.x or higher

# Check Java (if building locally)
java -version
# Expected: openjdk version "17" or higher

# Check Maven wrapper (from any service directory)
cd intelligent_financial_assistant_backend/discovery-service
./mvnw --version
# Expected: Apache Maven 3.9.x
```

## Quick Start Validation

### Step 1: Build Services

```bash
# From project root
./build-and-run.sh
```

Expected output:
- ✓ Docker is installed
- ✓ Java is installed
- ✓ Java version is compatible
- ✓ discovery-service built successfully
- ✓ auth-service built successfully
- ✓ transactions-service built successfully
- ✓ api-gateway built successfully

### Step 2: Start Services

If the script asked to start Docker Compose, you should see:

```
[+] Running 5/5
 ✔ Container finance-postgres        Started
 ✔ Container discovery-service       Started
 ✔ Container auth-service            Started
 ✔ Container transactions-service    Started
 ✔ Container api-gateway             Started
```

### Step 3: Verify Services are Running

```bash
docker compose -f docker-compose.simple.yml ps
```

Expected output: All services should be "Up" and healthy after 1-2 minutes:

```
NAME                    STATUS
finance-postgres        Up (healthy)
discovery-service       Up (healthy)
auth-service            Up (healthy)
transactions-service    Up (healthy)
api-gateway             Up
```

### Step 4: Check Service Discovery

Open http://localhost:8761 in your browser.

You should see the Eureka Dashboard with:
- AUTH-SERVICE registered
- TRANSACTIONS-SERVICE registered
- API-GATEWAY registered

### Step 5: Verify API Gateway

Test the API Gateway health:

```bash
curl http://localhost:8080/actuator/health
```

Expected: `{"status":"UP"}`

### Step 6: Create a Test User

```bash
./create-user.sh
```

Enter test data:
- First Name: Test
- Last Name: User
- Email: test@example.com
- Password: test123456

Expected output:
```
✓ User created successfully!

Response:
{
  "message": "User registered successfully",
  "userId": 1
}
```

### Step 7: Verify Login

Try logging in with the created user:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123456"
  }'
```

Expected: A JSON response with:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "test@example.com",
  "role": "ROLE_USER",
  "firstName": "Test",
  "lastName": "User"
}
```

### Step 8: Test Protected Endpoint

Use the token from Step 7:

```bash
TOKEN="<paste-token-here>"

curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer $TOKEN"
```

Expected: 
- If user is ROLE_USER: 403 Forbidden (expected)
- If user is ROLE_ADMIN: List of users

### Step 9: Start Frontend (Optional)

```bash
cd intelligent_financial_assistant_frontend_web
npm install
npm start
```

Expected:
```
** Angular Live Development Server is listening on localhost:4200 **
```

Open http://localhost:4200 in your browser.

### Step 10: Login via Frontend

1. Navigate to http://localhost:4200
2. Enter credentials:
   - Email: test@example.com
   - Password: test123456
3. Click Login

Expected: Successfully logged in and redirected to dashboard.

## Troubleshooting

### Services won't start

1. Check if ports are available:
   ```bash
   netstat -tuln | grep -E ':(5432|8080|8081|8082|8761)'
   ```

2. Check logs:
   ```bash
   docker compose -f docker-compose.simple.yml logs -f [service-name]
   ```

### Services stuck on "starting"

Wait 2-3 minutes. Services need time to:
1. Connect to PostgreSQL
2. Register with Eureka
3. Complete initialization

### Cannot create user

1. Check auth-service logs:
   ```bash
   docker compose -f docker-compose.simple.yml logs auth-service
   ```

2. Verify database connection:
   ```bash
   docker compose -f docker-compose.simple.yml logs postgres
   ```

3. Check if auth-service is registered with Eureka:
   - Open http://localhost:8761
   - Look for AUTH-SERVICE

### Login fails

1. Verify user was created:
   ```bash
   docker compose -f docker-compose.simple.yml exec postgres psql -U finance_user -d finance_db -c "SELECT * FROM users;"
   ```

2. Check if user is enabled:
   - The `enabled` column should be `true`

3. Try resetting password:
   ```bash
   # Create a new user with a simple password
   ./create-user.sh -f Test -l User2 -e test2@example.com -p password
   ```

### Frontend cannot connect to backend

1. Verify API Gateway is accessible:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. Check CORS configuration:
   - API Gateway should allow http://localhost:4200
   - See `intelligent_financial_assistant_backend/api-gateway/src/main/resources/application.yml`

3. Check browser console for errors:
   - Press F12 in browser
   - Look for CORS or network errors

## Service Endpoints

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/finance_db | Database |
| Eureka | 8761 | http://localhost:8761 | Service Discovery |
| Auth Service | 8081 | http://localhost:8081 | Authentication (direct) |
| Transactions Service | 8082 | http://localhost:8082 | Transactions (direct) |
| API Gateway | 8080 | http://localhost:8080 | Main entry point |
| Frontend | 4200 | http://localhost:4200 | Web UI |

## API Endpoints via Gateway

All endpoints should be accessed through the API Gateway (http://localhost:8080):

### Authentication
- POST `/auth/register` - Create new user
- POST `/auth/login` - Login
- POST `/auth/logout` - Logout
- GET `/auth/validate-token` - Validate token

### Users (Authenticated)
- PATCH `/users/{userId}/profile` - Update own profile

### Admin (Admin only)
- GET `/admin/users` - List all users
- POST `/admin/users` - Create user
- GET `/admin/users/{userId}` - Get user details
- PATCH `/admin/users/{userId}` - Update user
- DELETE `/admin/users/{userId}` - Delete user

### Transactions (Authenticated)
- GET `/transactions` - List transactions
- POST `/transactions` - Create transaction
- GET `/transactions/{id}` - Get transaction
- PUT `/transactions/{id}` - Update transaction
- DELETE `/transactions/{id}` - Delete transaction

## Success Criteria

✓ All services are running and healthy
✓ Services are registered with Eureka
✓ Can create a new user via API
✓ Can login and receive JWT token
✓ Can access authenticated endpoints with token
✓ Frontend can communicate with backend
✓ Can login via web interface

## Next Steps

Once validation is complete:

1. **Create an admin user** (requires direct database access or modify the code)
2. **Customize the application** according to your needs
3. **Set up production database** with proper credentials
4. **Configure SSL/TLS** for production
5. **Set up CI/CD pipeline** for automated deployments
6. **Add monitoring and logging** (e.g., Prometheus, Grafana, ELK)

## Support

If you encounter issues not covered here:

1. Check service logs: `docker compose -f docker-compose.simple.yml logs -f`
2. Review the README.md for additional information
3. Check the GitHub issues for similar problems
4. Create a new issue with detailed logs and steps to reproduce

## Clean Up

To stop all services:

```bash
docker compose -f docker-compose.simple.yml down
```

To remove volumes (WARNING: deletes database data):

```bash
docker compose -f docker-compose.simple.yml down -v
```
