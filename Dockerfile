# Stage 1: Build the Spring Boot application using Maven & Eclipse Temurin 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for layer caching
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source and package JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal JRE 21 Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Dynamic port binding
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=", "-jar", "app.jar"]
