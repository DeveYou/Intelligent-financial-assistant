# syntax=docker/dockerfile:1

# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY recipient-service/pom.xml recipient-service/pom.xml
COPY recipient-service/src recipient-service/src
RUN mvn -f recipient-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/recipient-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]
