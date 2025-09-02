# Spring Boot Currency Management System

## Overview

A robust microservice for currency management and foreign exchange operations, implementing CRUD operations, external API integration, and real-time data synchronization. Built with enterprise-grade patterns and comprehensive testing coverage.

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (OpenJDK LTS recommended)
- **Maven 3.9+** or use included wrapper
- **Docker 24.0+** & Docker Compose v2 (optional)

### Local Development
```bash
# Clone and navigate
git clone https://github.com/marcustrng/cathay-interview.git
cd cathay-interview

# Run with Maven wrapper
./mvnw spring-boot:run

# Or with Docker Compose
docker-compose up -d
```

### Access Points
| Service | URL | Description |
|---------|-----|-------------|
| API Base | http://localhost:8080 | Main application |
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive API documentation |
| API Docs | http://localhost:8080/v3/api-docs | OpenAPI 3.0 specification |
| H2 Console | http://localhost:8080/h2-console | Database management interface |

## 🏗️ Architecture & Design

### Tech Stack
- **Framework**: Spring Boot 3.x, Spring Data JPA
- **Runtime**: Java 17 with Maven build system
- **Database**: H2 in-memory (development), PostgreSQL-ready
- **External API**: OANDA FX API integration
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: OpenAPI 3.0 (Swagger)

### Design Patterns Implemented
- **Strategy Pattern**: Multiple exchange rate providers
- **Factory Pattern**: DTO transformation and response builders
- **Repository Pattern**: Data access layer abstraction
- **Observer Pattern**: Event-driven synchronization notifications

## 📋 Core Features

### 1. Currency Management
- **CRUD Operations**: Complete currency lifecycle management
- **Data Persistence**: H2 embedded database with JPA entities
- **Validation**: Comprehensive input validation and error handling
- **Sorting**: Results ordered by currency code (ISO 4217)

### 2. Exchange Rate Integration
- **OANDA FX API**: Real-time exchange rate fetching
- **Multi-Currency Support**: 3+ base currencies (USD, EUR, GBP, JPY)
- **Error Handling**: Circuit breaker pattern for API resilience
- **Data Transformation**: External API response mapping to internal DTOs

### 3. Synchronization Service
- **Scheduled Updates**: Configurable interval-based refresh
- **Manual Refresh**: On-demand synchronization endpoint
- **Timestamp Tracking**: Last update tracking (yyyy/MM/dd HH:mm:ss)
- **Failure Recovery**: Retry mechanism with exponential backoff

### 4. Enterprise Features
- **Centralized Exception Handling**: Consistent error response structure
- **Request/Response Logging**: Comprehensive audit trail
- **i18n Support**: Multi-language error messages
- **Security**: AES/RSA encryption examples
- **Monitoring**: Health checks and metrics endpoints

## 🔧 API Endpoints

### Currency Management
```http
GET    /client-api/v1/currencies           # List all currencies (sorted)
POST   /client-api/v1/currencies           # Create new currency
GET    /client-api/v1/currencies/{code}    # Get currency by code
PUT    /client-api/v1/currencies/{code}    # Update currency
DELETE /client-api/v1/currencies/{code}    # Delete currency
```

### Exchange Rates
```http
GET    /client-api/v1/exchange-rates                    # Current rates info
POST   /client-api/v1/exchange-rates/sync               # Manual synchronization
GET    /client-api/v1/exchange-rates/{from}/{to}        # Get specific rate
```

## 🧪 Testing Strategy

### Test Coverage
- **Unit Tests**: 95%+ coverage with Mockito
- **API Tests**: MockMvc for controller layer
- **External API**: WireMock for OANDA API simulation

### Running Tests
```bash
# All tests
./mvnw test

# Coverage report
./mvnw jacoco:report

```

## 🐳 Docker Support

### Development Environment
```yaml
# docker-compose.yml
version: '3.8'

services:
  # H2 Database Console (for development)
  cathay-interview-tutqq:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: cathay-interview
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: development
      JAVA_OPTS: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    ports:
      - "8080:8080"
      - "5005:5005"  # Debug port
    volumes:
      - .:/workspace
      - app_logs_dev:/app/logs
    networks:
      - dev-net

volumes:
  app_logs_dev:
    driver: local

networks:
  dev-net:
    driver: bridge
```

### Build & Deploy
```bash
# Build image
docker build -t cathay-inteview:latest .

# Run container
docker run -p 8080:8080 \
  currency-service:latest
```

## ⚙️ Configuration

### Application Properties
```yaml
spring:
  application:
    name:
      cathay::tutqq
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  datasource:
    url: jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  flyway:
    enabled: true
    locations: classpath:db/migration
  # Quartz Scheduler Configuration
  quartz:
    job-store-type: memory
    properties:
      org:
        quartz:
          scheduler:
            instanceName: CurrencyExchangeScheduler
            instanceId: AUTO
          jobStore:
            class: org.quartz.simpl.RAMJobStore
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 10
            threadPriority: 5
# Logging Configuration
logging:
  level:
    com.cathay.interview.tutqq: INFO
    org.quartz: INFO
    org.springframework.web.client: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Custom Application Properties
app:
  exchange-rate:
    oanda-api:
      base-url: "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies"
      timeout: 30000 # 30 seconds
      max-retries: 3
    sync:
      enabled: true
      cron: "0 0 2 * * ?" # Every day at 2 AM
      default-days-back: 1
      batch-size: 100
```

## 📚 Database Schema

### DDL Script Location
```
src/main/resources/db/migration/V1_1_0__init.sql
```
## 📞 Contact & Support

**Developer**: Marcus Tự Trương  
**Email**: tutqq96@gmail.com  
**Assignment**: Cathay United Bank Technical Interview  

---
