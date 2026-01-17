# --- Stage 1: Build the Application ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Clear the local Maven repository to ensure a clean build
RUN rm -rf /root/.m2/repository

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the JAR file (skipping tests to speed up build)
RUN mvn clean package -DskipTests --no-transfer-progress

# --- Stage 2: Create the Runtime Image ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the JAR from the build stage
# Note: We use a wildcard *.jar so we don't need to know the exact version name
COPY --from=build /app/target/*.jar app.jar

# Expose the port (This is just documentation, docker-compose handles the mapping)
EXPOSE 8087

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]