# Order Processing Service

A demonstration Spring Boot service implementing modern architectural patterns for order management with tenant-specific validation and asynchronous processing.

## Features

- **CQRS (Command & Query Separation)** - Clean separation of read and write operations
- **Tenant-specific Validation** - Configurable validation rules per tenant
- **Saga-style Asynchronous Processing** - Reliable order processing workflow
- **Transactional Outbox Pattern** - Guaranteed message delivery with crash recovery
- **Background Worker** - Automatic processing every 5 seconds
- **H2 In-Memory Database** - Zero configuration development database
- **Swagger UI** - Interactive API documentation and testing

## Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Running the Application

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`

2. **Access Swagger UI**

   Open your browser and navigate to:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```
   From here you can:
    - Test all API endpoints
    - View request/response schemas
    - Execute sample requests

3. **Access H2 Database Console**

   Navigate to:
   ```
   http://localhost:8080/h2-console
   ```

   **Connection Details:**
   | Field | Value |
   |-------|-------|
   | JDBC URL | `jdbc:h2:mem:ordersdb` |
   | Username | `sa` |
   | Password | *(leave empty)* |

   **Key Tables:**
    - `ORDERS` - Order records
    - `OUTBOX_EVENTS` - Event processing queue
    - `TENANTS` - Tenant configurations
    - `TENANT_ORDER_CONFIGS` - Validation rules per tenant

## Architecture Overview

```
┌────────────────────────────┐
│      CLIENT (API)          │
│  (Swagger/Postman/UI)      │
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│     COMMAND HANDLER        │
│  (CreateOrderCommand)      │
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│   TRANSACTIONAL WRITE      │
│  (Order + Outbox Event)    │
└──────────┬─────────────────┘
           │
     [Async Worker]
           │
           ▼
┌────────────────────────────┐
│     OUTBOX PROCESSOR       │
│  (Background Processing)   │
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│      QUERY HANDLER         │
│    (GetOrderQuery)         │
└────────────────────────────┘
```

## CQRS Implementation

### Command Side (Write Operations)
- **Location:** `command/handler`
- **Responsibilities:**
    - Input validation
    - Order creation (PENDING status)
    - Outbox event creation (PENDING status)
    - Atomic transaction management

### Query Side (Read Operations)
- **Location:** `query/handler`
- **Responsibilities:**
    - Fetch orders by ID
    - Entity to DTO mapping
    - Read-only operations
    - No business logic execution

## Tenant Validation System

### Pre-configured Tenants

The application automatically creates two tenants on startup:

| Tenant | Code | Min Amount | Min Quantity |
|--------|------|------------|--------------|
| Tenant A | `TENANT_A` | 100 | - |
| Tenant B | `TENANT_B` | 100 | 10 |

### Validation Rules

- **Tenant A:** Orders must have `amount ≥ 100`
- **Tenant B:** Orders must have `amount ≥ 100` AND `quantity ≥ 10`

### Validation Process

1. Worker loads tenant ID from order
2. Retrieves validation rules from database
3. Applies rules via `TenantValidatorRegistry`
4. Updates order status:
    - ✅ Rules pass → `PROCESSED`
    - ❌ Rules fail → `FAILED`

## Saga & Transactional Outbox Pattern

### Processing Flow

#### Step 1: Initial Creation
Order and outbox event are created atomically:
```
Order        → status: PENDING
Outbox Event → status: PENDING
```

#### Step 2: Background Processing
Worker runs every 5 seconds:
```
PENDING → IN_PROGRESS → PROCESSED/FAILED
```

#### Step 3: Crash Recovery
On application restart:
- `OutboxStartupConfig` resets `IN_PROGRESS` → `PENDING`
- Worker safely retries unfinished events

## API Usage Examples

### Create Order

**Request:**
```http
POST /api/v1/orders
Content-Type: application/json

{
  "tenantId": "TENANT_A",
  "amount": 150,
  "quantity": 5
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "tenantId": "TENANT_A",
    "status": "PENDING"
  }
}
```

### Get Order by ID

**Request:**
```http
GET /api/v1/orders/1
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "tenantId": "TENANT_A",
    "status": "PROCESSED"
  }
}
```

### Example: Failed Validation

**Request (Tenant B with insufficient quantity):**
```json
{
  "tenantId": "TENANT_B",
  "amount": 120,
  "quantity": 5
}
```

**Result:**
- Order Status: `FAILED`
- Error Message: `"Quantity must be >= 10"`

## Database Queries for Debugging

Use these queries in the H2 console for troubleshooting:

```sql
-- View all orders
SELECT * FROM ORDERS;

-- Check outbox event status
SELECT ID, AGGREGATE_ID, STATUS, ERROR_MESSAGE 
FROM OUTBOX_EVENTS;

-- View tenant configurations
SELECT * FROM TENANT_ORDER_CONFIGS;

-- Check processing history
SELECT o.ID, o.TENANT_ID, o.STATUS as ORDER_STATUS, 
       e.STATUS as EVENT_STATUS, e.ERROR_MESSAGE
FROM ORDERS o
JOIN OUTBOX_EVENTS e ON o.ID = e.AGGREGATE_ID
ORDER BY o.CREATED_AT DESC;
```

## Project Structure

```
src/main/java/com/kris/orderservice/
│
├── command/
│   └── handler/
│       └── CreateOrderCommandHandler.java
│
├── query/
│   └── handler/
│       └── GetOrderQueryHandler.java
│
├── outbox/
│   ├── domain/
│   │   └── OutboxEvent.java
│   ├── repository/
│   │   └── OutboxEventRepository.java
│   └── saga/
│       ├── OutboxWorker.java
│       ├── OutboxProcessor.java
│       └── OutboxStartupConfig.java
│
├── tenant/
│   ├── domain/
│   │   ├── Tenant.java
│   │   └── TenantOrderConfig.java
│   └── repository/
│       └── TenantRepository.java
│
├── validation/
│   ├── DatabaseTenantOrderValidator.java
│   └── TenantValidatorRegistry.java
│
└── config/
    ├── SwaggerConfig.java
    └── DatabaseConfig.java
```

## Key Components

### Outbox Pattern Components
- **OutboxWorker:** Scheduled task that processes pending events
- **OutboxProcessor:** Business logic for event processing
- **OutboxStartupConfig:** Handles crash recovery on startup

### Validation Components
- **TenantValidatorRegistry:** Central registry for validation strategies
- **DatabaseTenantOrderValidator:** Database-driven validation implementation

## Development Tips

1. **Monitor Processing:** Watch the application logs to see the background worker processing orders every 5 seconds

2. **Test Different Scenarios:**
    - Valid orders for each tenant
    - Orders that fail validation
    - System behavior during processing

3. **Database Inspection:** Use the H2 console to inspect data changes in real-time

4. **Swagger Testing:** Use Swagger UI for quick API testing without external tools

## Technologies Used

- **Spring Boot** - Application framework
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **Swagger/OpenAPI** - API documentation
- **Maven** - Build tool
- **SLF4J/Logback** - Logging

## License

This project is a demonstration application for educational purposes.

## Author

Krishnan

---

*This service demonstrates enterprise patterns in a simplified, runnable format suitable for learning and experimentation.*