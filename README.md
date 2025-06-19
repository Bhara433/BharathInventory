# E-commerce Inventory Management System

A robust and scalable e-commerce inventory management system built with Spring Boot, featuring Redis caching, PostgreSQL database, and comprehensive API coverage for inventory and reservation management.

## 🚀 Features

- **Inventory Management**: Create, update, and manage product inventory
- **Reservation System**: Reserve items with expiration and concurrency handling
- **Redis Caching**: High-performance caching for improved response times
- **Concurrency Control**: Pessimistic locking for simultaneous reservations
- **RESTful APIs**: Comprehensive API endpoints for all operations
- **Database Support**: PostgreSQL and H2 (in-memory) database support
- **Validation**: Input validation and error handling
- **Testing**: JUnit tests with TestContainers

## 🛠 Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.0
- **Database**: PostgreSQL / H2 (In-memory)
- **Caching**: Redis
- **Build Tool**: Maven
- **Testing**: JUnit 5, TestContainers
- **Documentation**: Spring Boot Actuator

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Redis (for caching)
- PostgreSQL (optional, H2 is used by default)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd inventory
```

### 2. Start Redis (Required for Caching)

```bash
# Using Docker
docker run -d -p 6379:6379 redis:latest

# Or install Redis locally
# macOS: brew install redis
# Ubuntu: sudo apt-get install redis-server
```

### 3. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or build and run
mvn clean package
java -jar target/inventory-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Development)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:inventorydb`
- Username: `sa`
- Password: `password`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Inventory Management APIs

#### 1. Create Item
```http
POST /items
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with advanced features",
  "sku": "IPHONE-15-PRO-256",
  "price": 999.99,
  "availableQuantity": 50,
  "category": "Electronics",
  "brand": "Apple"
}
```

#### 2. Get Item by ID
```http
GET /items/{id}
```

#### 3. Get Item by SKU
```http
GET /items/sku/{sku}
```

#### 4. Get All Items
```http
GET /items
```

#### 5. Get Available Items
```http
GET /items/available
```

#### 6. Add Supply
```http
POST /items/{id}/supply
Content-Type: application/json

{
  "quantity": 10
}
```

#### 7. Check Availability
```http
GET /items/{id}/availability?quantity=5
```

### Reservation Management APIs

#### 1. Create Reservation
```http
POST /reservations
Content-Type: application/json

{
  "itemId": 1,
  "customerId": "CUST-001",
  "quantity": 2,
  "expirationMinutes": 30
}
```

#### 2. Cancel Reservation
```http
DELETE /reservations/{id}
```

#### 3. Get Customer Reservations
```http
GET /reservations/customer/{customerId}
```

#### 4. Get Item Reservations
```http
GET /reservations/item/{itemId}
```

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/example/inventory/
│   │   ├── entity/           # JPA entities
│   │   │   ├── Item.java
│   │   │   └── Reservation.java
│   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── ApiResponse.java
│   │   │   ├── CreateItemRequest.java
│   │   │   ├── ItemDto.java
│   │   │   └── ReservationRequest.java
│   │   ├── repository/       # Data access layer
│   │   │   ├── ItemRepository.java
│   │   │   └── ReservationRepository.java
│   │   ├── service/          # Business logic
│   │   │   ├── ItemService.java
│   │   │   ├── ReservationService.java
│   │   │   └── CacheService.java
│   │   ├── controller/       # REST controllers
│   │   │   ├── ItemController.java
│   │   │   └── ReservationController.java
│   │   └── InventoryApplication.java
│   └── resources/
│       ├── application.yml   # Application configuration
│       └── data.sql          # Initial data (optional)
└── test/                     # Test classes
```

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. To use PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventorydb
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Redis Configuration
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
```

## 🧪 Testing

Run tests with:
```bash
mvn test
```

The project includes:
- Unit tests for services
- Integration tests with TestContainers
- API tests for controllers

## 📊 Monitoring

Access Spring Boot Actuator endpoints:
- Health check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

## 🔒 Concurrency Handling

The system handles concurrent reservations using:
- Pessimistic locking on items
- Version control for optimistic locking
- Redis-based caching for performance
- Transaction management

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t inventory-management .

# Run container
docker run -p 8080:8080 inventory-management
```

### Production Configuration
1. Use PostgreSQL instead of H2
2. Configure Redis for production
3. Set appropriate logging levels
4. Configure security settings

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions, please create an issue in the GitHub repository. 