# Stage 1: Build the application
FROM eclipse-temurin:24-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Install Maven
RUN apk update && \
    apk add maven && \
    rm -rf /var/cache/apk/*

# Copy the Maven project file to leverage Docker's caching for dependencies
COPY pom.xml .

# Download project dependencies. This step will try to run Flyway and jOOQ code generation
# connecting to jdbc:postgresql://89.117.58.248:5432/chess_twin_db as configured in pom.xml.
# If this external database is not reachable from your build environment, this step will fail.
# Consider running 'mvn clean package -DskipTests -Dflyway.skip=true -Djooq.codegen.skip=true' locally
# and then building the Docker image if you face issues or prefer local code generation.
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the Spring Boot application into an executable JAR
# This will again attempt to run Flyway and jOOQ code generation.
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:24-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the builder stage
ARG JAR_FILE=target/chesstwin-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# --- NEW: Copy the wait-for-it.sh script and make it executable ---
COPY wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh
# -----------------------------------------------------------------

# Expose the application's port
EXPOSE 8080

# --- MODIFIED: Define the entry point to run the application using the wait script ---
# The 'wait-for-it.sh db:5432' part ensures the 'db' service is reachable on port 5432
# before executing the rest of the command (java -jar app.jar).
ENTRYPOINT ["wait-for-it.sh", "89.117.58.248", "--", "java", "-jar", "app.jar"]
# -----------------------------------------------------------------------------------

# Set environment variables for database connection (will be overridden by docker-compose)
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://89.117.58.248:5432/chess_twin_db" \
    SPRING_DATASOURCE_USERNAME="chess_twin_user" \
    SPRING_DATASOURCE_PASSWORD="chess_twin_pwd"
