# Multi-stage build for Spring Boot + React

# Stage 1: Build React Frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend

# Copy frontend package files
COPY frontend/package*.json ./
RUN npm ci

# Copy frontend source and build
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot Backend
FROM gradle:8.5-jdk21-alpine AS backend-build
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Copy frontend build output to Spring Boot static resources
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

# Build Spring Boot application
RUN ./gradlew build -x test --no-daemon

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

# Copy built jar from backend-build stage
COPY --from=backend-build /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=prod", \
    "-Dspring.datasource.url=${DATABASE_URL}", \
    "-Dspring.datasource.username=${DATABASE_USERNAME}", \
    "-Dspring.datasource.password=${DATABASE_PASSWORD}", \
    "-Dspring.data.redis.host=${REDIS_HOST}", \
    "-Dspring.data.redis.port=${REDIS_PORT}", \
    "-Dspring.data.redis.password=${REDIS_PASSWORD}", \
    "-jar", \
    "app.jar"]
