FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY libs libs
COPY services/notification-service services/notification-service
COPY mvnw mvnw
COPY .mvn .mvn

RUN chmod +x mvnw && ./mvnw -pl services/notification-service -am package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/services/notification-service/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
