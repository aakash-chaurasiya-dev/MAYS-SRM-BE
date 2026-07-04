# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy the pom.xml file to download dependencies first
# This ensures Docker caches the dependencies layer, significantly speeding up future builds
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the actual source code
COPY src ./src

# Package the application (skip tests to speed up the build image process)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the builder stage
# (We use *.jar because the exact version number might change in pom.xml)
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080
 
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
