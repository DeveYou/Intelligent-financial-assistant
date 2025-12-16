FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY common-security/pom.xml common-security/pom.xml
COPY common-security/src common-security/src
RUN mvn -f common-security/pom.xml clean install -DskipTests

COPY account-service/pom.xml account-service/pom.xml
COPY account-service/src account-service/src
RUN mvn -f account-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/account-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]
