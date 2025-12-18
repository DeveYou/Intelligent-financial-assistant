# Stage 1: Build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy and install common-security
COPY common-security/pom.xml common-security/
COPY common-security/src common-security/src
WORKDIR /app/common-security
RUN mvn clean install -DskipTests

# Copy and build notification-service
WORKDIR /app
COPY notification-service/pom.xml notification-service/
COPY notification-service/src notification-service/src
WORKDIR /app/notification-service
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /app/notification-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
