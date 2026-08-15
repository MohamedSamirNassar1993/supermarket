ARG SERVICE_NAME=api-gateway
FROM eclipse-temurin:17-jdk-alpine AS build
ARG SERVICE_NAME
WORKDIR /build
RUN apk add --no-cache maven
COPY pom.xml .
COPY libs libs
COPY services services
RUN mvn -pl services/${SERVICE_NAME} -am package -DskipTests -B -q

FROM eclipse-temurin:17-jre-alpine
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /build/services/${SERVICE_NAME}/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
