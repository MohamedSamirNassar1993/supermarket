FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY libs libs
COPY services/inventory-service services/inventory-service
COPY mvnw mvnw
COPY .mvn .mvn

RUN chmod +x mvnw && ./mvnw -pl services/inventory-service -am package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/services/inventory-service/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
