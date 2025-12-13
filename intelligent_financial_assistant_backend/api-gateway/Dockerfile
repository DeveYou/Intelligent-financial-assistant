FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY api-gateway/src api-gateway/src
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/api-gateway/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
