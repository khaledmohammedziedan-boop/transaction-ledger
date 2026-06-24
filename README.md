# Transaction Ledger

Transaction Ledger is a Spring Boot REST API for user registration, JWT-based authentication, and authenticated transaction creation and lookup. Each transaction is owned by the authenticated user, and transaction listing is scoped to that user.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Validation
- Spring Data JPA
- jOOQ
- PostgreSQL
- Maven Wrapper
- Springdoc OpenAPI / Swagger UI
- H2 for the test profile

## Project Structure

```text
src/main/java/rs2/com/transaction_ledger
├── config      Security, OpenAPI, exception handling, and JWT filters
├── controller  REST controllers
├── dtos        Request and response DTOs
├── exception   Application business exception
├── helper      Shared helpers such as pagination response and query parsing
├── model       JPA entities and audit base entity
├── repo        JPA repositories and jOOQ read repository
└── service     Business logic and validation
```

## Prerequisites

- JDK 17
- Docker and Docker Compose, or a local PostgreSQL instance

On Windows, ensure Maven runs with JDK 17:

```bat
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
```

## Database Setup

The default runtime database configuration is:

```text
URL:      jdbc:postgresql://localhost:5432/transaction_ledger_DB
Username: postgres
Password: postgres
```

Start PostgreSQL with Docker Compose:

```bash
docker compose up -d postgres
```

The Docker database is initialized from:

```text
src/main/resources/Database/schema.sql
```

If the Docker volume already exists and you need to rerun the initialization script:

```bash
docker compose down -v
docker compose up -d postgres
```

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
```

This can create or update tables inside an existing database. PostgreSQL must still have the database itself created.

For production-like environments, set:

```text
SPRING_JPA_HIBERNATE_DDL_AUTO=none
```

and manage schema changes explicitly.

## Configuration

Runtime properties are defined in:

```text
src/main/resources/application.properties
```

Common environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP port |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/transaction_ledger_DB` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate schema mode |
| `JWT_SECRET_KEY` | empty | JWT signing secret override |

If `JWT_SECRET_KEY` is blank, the application uses the configured development fallback secret.

## Build And Test

Run the full test suite:

```bash
./mvnw test
```

Windows:

```bat
mvnw.cmd test
```

The test suite uses the `test` Spring profile and an in-memory H2 database, so tests do not require a running PostgreSQL instance.

## jOOQ Code Generation

jOOQ is configured in Maven and runs during the `generate-sources` phase. It reads:

```text
src/main/resources/Database/schema.sql
```

and generates table metadata under:

```text
target/generated-sources/jooq
```

The generated package is:

```text
rs2.com.transaction_ledger.jooq.generated
```

Generated files are build artifacts and should not be edited manually.

## Run The Application

Start PostgreSQL first, then run:

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

Base URL:

```text
http://localhost:8080/transaction-ledger
```

Swagger UI:

```text
http://localhost:8080/transaction-ledger/swagger-ui.html
```

## API Overview

Authentication APIs:

- `POST /auth/register` registers a user.
- `POST /auth/apiLogin` authenticates credentials and returns a JWT.
- `GET /auth/user` returns the authenticated user's profile.

Transaction APIs:

- `POST /transaction` creates a transaction for the authenticated user.
- `GET /transaction/getTransactionsPage` returns the authenticated user's transactions with pagination, filtering, and sorting.

Supported transaction query fields:

```text
amount
currency
description
counterPartyIban
createdAt
```

Supported filter operators:

```text
EQ, NE, GT, GTE, LT, LTE, LIKE, IN
```

Example transaction query:

```text
GET /transaction/getTransactionsPage?page=0&size=20&filter=amount,GT,100&sort=createdAt,DESC
```

## Security Notes

- `/auth/register`, `/auth/apiLogin`, Swagger, OpenAPI docs, and Actuator endpoints are public.
- `/auth/user` and `/transaction/**` require JWT authentication.
- The login endpoint returns the JWT in the response body and `Authorization` response header.
- Transaction listing is scoped by authenticated `user_id` in the jOOQ query to prevent object-level authorization bypass.
- Transaction filter and sort fields are allowlisted.

Use this header for authenticated requests:

```text
Authorization: Bearer <jwt>
```

## Postman

A Postman collection is included:

```text
postman_collection.json
```

Import it into Postman, set `baseUrl` if needed, run the login request, and copy the returned JWT into the collection variable `jwt`.

## Swagger Testing Flow

1. Open Swagger UI:

   ```text
   http://localhost:8080/transaction-ledger/swagger-ui.html
   ```

2. Register a user with `POST /auth/register`.

3. Log in with `POST /auth/apiLogin`.

4. Copy the returned `jwtToken`.

5. Click `Authorize` in Swagger and paste:

   ```text
   Bearer <jwtToken>
   ```

6. Call `GET /auth/user` to confirm authentication.

7. Create a transaction with `POST /transaction`.

8. Retrieve transactions with `GET /transaction/getTransactionsPage`.

## Useful Example Payloads

Register:

```json
{
  "name": "Khaled Saleh",
  "email": "khaled@example.com",
  "mobileNumber": "+962790000000",
  "pwd": "P@ssw0rd!"
}
```

Login:

```json
{
  "username": "khaled@example.com",
  "password": "P@ssw0rd!"
}
```

Create transaction:

```json
{
  "amount": 150.75,
  "currency": "EUR",
  "description": "Grocery purchase",
  "counterPartyIban": "DE44500105175407324931"
}
```

## Troubleshooting

If startup fails with a Hibernate dialect or JDBC metadata error, check:

- PostgreSQL is running.
- The database exists.
- The configured username and password are correct.
- The application is pointing at the expected JDBC URL.

If Docker initialization did not apply the latest schema, recreate the volume:

```bash
docker compose down -v
docker compose up -d postgres
```
