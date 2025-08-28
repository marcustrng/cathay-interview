FROM openjdk:17.0.1-jdk-slim

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup -s /bin/bash appuser

# Install curl for health checks
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Copy the JAR file
COPY target/tutqq-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/api/exchange-rates/health || exit 1

# JVM options optimized for containers (Java 11+)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]