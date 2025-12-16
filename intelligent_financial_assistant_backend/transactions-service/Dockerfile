FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY common-security/pom.xml common-security/pom.xml
COPY common-security/src common-security/src
RUN mvn -f common-security/pom.xml clean install -DskipTests

COPY transactions-service/pom.xml transactions-service/pom.xml
COPY transactions-service/src transactions-service/src
RUN mvn -f transactions-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/transactions-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]

