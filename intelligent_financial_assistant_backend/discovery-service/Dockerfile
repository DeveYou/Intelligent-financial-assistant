FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY discovery-service/pom.xml discovery-service/pom.xml
COPY discovery-service/src discovery-service/src
RUN mvn -f discovery-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/discovery-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]
