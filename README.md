# 💰 Finance Dashboard API

A RESTful backend API for managing personal finances, built with **Spring Boot 3** and secured with **JWT authentication**. It supports transaction tracking, role-based access control, and a financial summary dashboard.

---

## 🚀 Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot 3.5 | Application framework |
| Spring Security | Authentication & Authorization |
| Spring Data JPA | ORM / Database access |
| H2 Database | In-memory database |
| JWT (jjwt 0.12.6) | Stateless token-based auth |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

---

## 📁 Project Structure

```
Finance_Dashboard/
├── src/main/java/com/omkar/Finance_Dashboard/
│   ├── controller/          # REST controllers (Auth, Dashboard, Transaction, User)
│   ├── dto/                 # Request and Response DTOs
│   ├── exception/           # Global exception handler
│   ├── model/               # JPA entities (User, Transaction, RefreshToken)
│   ├── repository/          # Spring Data JPA repositories
│   ├── security/            # JWT filter, security config, bean config
│   └── service/             # Business logic (Dashboard, Transaction, JWT)
└── src/main/resources/
    └── application.yaml     # App configuration
```

---

## 🔐 Authentication

The API uses **JWT-based stateless authentication**. On login or registration, you receive an `accessToken` and a `refreshToken`. Include the access token in all protected requests:

```
Authorization: Bearer <accessToken>
```

Refresh tokens are persisted in the database with support for revocation by IP.

---

## 👥 Roles & Permissions

| Role | Permissions |
|---|---|
| `VIEWER` | View dashboard summary |
| `ANALYST` | View dashboard + list all transactions |
| `ADMIN` | Full access including user management and transaction CRUD |

---

## 📡 API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user | No |
| `POST` | `/api/auth/login` | Login and receive tokens | No |

**Register Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "secret123",
  "role": "VIEWER"
}
```

**Login Request Body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response:**
```json
{
  "accessToken": "<jwt_token>",
  "refreshToken": "<refresh_token>"
}
```

---

### Transactions — `/api/transactions`

| Method | Endpoint | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/transactions` | Create a transaction | `ANALYST`, `ADMIN` |
| `GET` | `/api/transactions` | List all transactions (filterable) | `ANALYST`, `ADMIN` |
| `GET` | `/api/transactions/{id}` | Get transaction by ID | `ADMIN` |
| `PUT` | `/api/transactions/{id}` | Update a transaction | `ADMIN` |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction | `ADMIN` |

**Query Parameters for GET `/api/transactions`:**
- `startDate` — ISO datetime, e.g. `2024-01-01T00:00:00`
- `endDate` — ISO datetime
- `category` — e.g. `Food`, `Salary`
- `type` — `INCOME` or `EXPENSE`

**Transaction Request Body:**
```json
{
  "amount": 500.00,
  "type": "EXPENSE",
  "category": "Food",
  "date": "2024-03-15T12:00:00",
  "notes": "Grocery shopping"
}
```

---

### Dashboard — `/api/dashboard`

| Method | Endpoint | Description | Required Role |
|---|---|---|---|
| `GET` | `/api/dashboard/summary` | Get financial summary | `VIEWER`, `ANALYST`, `ADMIN` |

**Response:**
```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 3200.00,
  "netBalance": 1800.00,
  "categoryWiseTotalsIncome": { "Salary": 5000.00 },
  "categoryWiseTotalsExpense": { "Food": 800.00, "Rent": 2400.00 },
  "recentActivity": [ ... ]
}
```

---

### Users — `/api/users`

| Method | Endpoint | Description | Required Role |
|---|---|---|---|
| `GET` | `/api/users` | List all users | `ADMIN` |
| `PUT` | `/api/users/{id}/role?newRole=ANALYST` | Update user role | `ADMIN` |

---

## ⚙️ Configuration

Edit `src/main/resources/application.yaml` before running:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:financedb
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console

jwt:
  secret-key: <your-base64-encoded-secret>
  access-key:
    expiration: 900000       # 15 minutes in ms
  refresh-key:
    expiration: 604800000    # 7 days in ms
```

> ⚠️ **Security Note:** Replace the `jwt.secret-key` with a securely generated Base64-encoded secret before deploying to production. Do not commit real secrets to version control.

---

## 🛠️ Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+ (or use the included `mvnw` wrapper)

### Run the Application

```bash
# Clone the repository
git clone <your-repo-url>
cd Finance_Dashboard

# Build and run with Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The server starts at **`http://localhost:8080`**.

### Access H2 Console

Navigate to `http://localhost:8080/h2-console` with:
- **JDBC URL:** `jdbc:h2:mem:financedb`
- **Username:** `sa`
- **Password:** *(leave blank)*

### Run Tests

```bash
./mvnw test
```

---

## 🏗️ Build JAR

```bash
./mvnw clean package
java -jar target/Finance_Dashboard-0.0.1-SNAPSHOT.jar
```

---

## 📋 Data Models

### Transaction Types
- `INCOME`
- `EXPENSE`

### Account Status
- `ACTIVE`
- `INACTIVE`

### Roles
- `VIEWER` — Read-only dashboard access
- `ANALYST` — Dashboard + transaction listing
- `ADMIN` — Full system access

---

## 🔧 Error Handling

All errors return a structured JSON response:

```json
{
  "timestamp": "2024-03-15T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email is required",
    "amount": "Amount must be positive"
  }
}
```

---

## 📄 License

This project is open-source. See [LICENSE](LICENSE) for details.
