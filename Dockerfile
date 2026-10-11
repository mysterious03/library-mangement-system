# ==============================================================================
# SMART LIBRARY MANAGEMENT SYSTEM (SLMS) - DOCKERFILE
# Pure Java SE 17 + JDBC + Embedded ACID H2 Database + HTML5/CSS3
# Suitable for Render, Railway, Fly.io, AWS, DigitalOcean, and Local Docker
# ==============================================================================

# --- Stage 1: Compiler Build Stage ---
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY lib/ lib/
COPY src/ src/

RUN mkdir -p bin && \
    javac -encoding UTF-8 -cp "lib/h2.jar:lib/mysql-connector-j.jar:src" -d bin $(find src -name "*.java")

# --- Stage 2: Ultra-lightweight Runtime Stage ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy compiled bytecode and required static/library dependencies
COPY --from=builder /app/bin bin/
COPY lib/ lib/
COPY web/ web/
COPY sql/ sql/

# Dynamic Cloud Port Binding (Render, Railway, Fly.io inject $PORT)
ENV PORT=8080
EXPOSE 8080

# Launch application
CMD ["java", "-cp", "bin:lib/h2.jar:lib/mysql-connector-j.jar", "Main"]
