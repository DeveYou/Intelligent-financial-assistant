FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/recipient-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8084

CMD ["java", "-jar", "recipient-service-0.0.1-SNAPSHOT.jar"]
