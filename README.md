# MedConnect — Digital Healthcare API

A clinic management backend built with Spring Boot, focused on one core guarantee: **no doctor can ever be double-booked.**

## Overview

MedConnect is a RESTful API that lets patients register, log in, browse doctors, and book appointments — while guaranteeing that no two appointments for the same doctor can ever overlap in time. Authentication is stateless (JWT-based), access is restricted by role (patient/doctor/admin), and the whole stack is containerized with Docker so it runs identically on any machine.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security 6, JWT (jjwt), BCrypt |
| Persistence | Spring Data JPA / Hibernate |
| Database | H2 (local development) · MySQL 8 (Docker / production-like) |
| Containerization | Docker, Docker Compose (multi-stage build) |
| Build tool | Maven |
| API testing | Postman |

## Core Features

- **User registration & login** for doctors and patients, with BCrypt-hashed passwords
- **JWT authentication** — stateless, signed tokens, no server-side sessions
- **Role-Based Access Control (RBAC)** — `@PreAuthorize` restricts endpoints by role (e.g. only patients can book appointments)
- **Conflict-detection algorithm** — rejects any appointment that overlaps an existing booking for the same doctor, enforced at the database-query level before persistence
- **Global exception handling** — consistent JSON error responses (`404`, `400`, `409`, `401`, `403`) instead of raw stack traces
- **Dockerized environment** — the app and MySQL run as separate containers, wired together with a healthcheck so the app never starts before the database is ready

## Architecture

Layered MVC + service pattern:

```
Controller  →  Service  →  Repository  →  Database
   (HTTP)      (business logic)   (Spring Data JPA)
```

Controllers stay thin — no business logic. All rules (conflict checks, validation, RBAC) live in the service layer.

### JPA Joined Inheritance

`User` is an abstract base entity. `Doctor` and `Patient` extend it, each with their own table joined back to `users` by a shared primary key:

```
users (id, email, password_hash, role, ...)
  ├── doctors (user_id → FK, specialty, license_number)
  └── patients (user_id → FK, blood_group, date_of_birth)
```

This avoids a single table full of nulls (doctors don't have a blood group; patients don't have a specialty) while still sharing common fields cleanly.

### The Conflict-Detection Algorithm

Two time ranges overlap if:
```
existing.start < new.end   AND   existing.end > new.start
```

This check runs as a JPQL query against the database **before** any appointment is inserted:

```java
@Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.doctor.id = :doctorId
      AND a.status <> 'CANCELLED'
      AND a.startTime < :endTime
      AND a.endTime > :startTime
    """)
boolean existsOverlappingAppointment(...)
```

If a conflict is found, the request is rejected with `409 Conflict` — the whole booking flow runs inside a `@Transactional` boundary, so nothing is ever partially written.

## Project Structure

```
com.project.medconnect
├── controller/       # HTTP layer — thin, no business logic
├── service/          # Business rules (conflict detection, registration, login)
├── repository/        # Spring Data JPA interfaces
├── domain/entity/     # User ← JOINED → Doctor, Patient; Appointment
├── domain/enums/       # Role, AppointmentStatus
├── dto/               # Request/response objects — entities never leave the service layer
├── jwt/                # JwtService (sign/verify tokens), JwtAuthFilter
├── config/             # SecurityConfig (filter chain, RBAC, CORS)
├── exception/           # Custom exceptions + GlobalExceptionHandler
```

## Getting Started

### Option 1 — Local development with H2 (no setup required)

```bash
mvn spring-boot:run
```

- API base URL: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:medconnect`
  - Username: `sa` · Password: *(empty)*

### Option 2 — Full stack with Docker + MySQL

1. Copy `.env.example` to `.env` and fill in real values:
   ```bash
   cp .env.example .env
   ```
   Generate a JWT secret:
   ```bash
   openssl rand -base64 32
   ```

2. Build and start both containers:
   ```bash
   docker compose up --build
   ```

3. The app waits for MySQL's healthcheck to pass before starting — no manual timing needed.

4. Same API, now backed by MySQL: `http://localhost:8080`

## API Overview

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register/doctor` | Public | Register a doctor |
| POST | `/api/v1/auth/register/patient` | Public | Register a patient |
| POST | `/api/v1/auth/login` | Public | Log in, receive a JWT |
| GET | `/api/v1/me` | Authenticated | Current user's profile |
| POST | `/api/v1/appointments` | PATIENT only | Book an appointment (conflict-checked) |

##Screenshots 

<img width="1916" height="1005" alt="Screenshot From 2026-08-18 06-19-01" src="https://github.com/user-attachments/assets/4514c337-4ce0-476f-ad93-7ba0391ccb2f" />
<img width="1578" height="1048" alt="Screenshot From 2026-08-18 05-45-51" src="https://github.com/user-attachments/assets/fc10d5a6-daa4-40c9-8aaa-f7c280dfc066" />
<img width="1918" height="1009" alt="Screenshot From 2026-08-18 04-47-11" src="https://github.com/user-attachments/assets/c6789cd1-4ea2-424d-995f-aa9af7c9ae7a" />
<img width="1918" height="1001" alt="Screenshot From 2026-08-18 04-47-00" src="https://github.com/user-attachments/assets/0ff8c5f9-4ea5-4034-8e3d-2e10ee10e4a5" />
<img width="1920" height="1016" alt="Screenshot From 2026-08-18 04-46-45" src="https://github.com/user-attachments/assets/5e3887d6-b90c-4bf8-a697-f6021c1dd978" />


## Security Notes

- Passwords are hashed with BCrypt — never stored or logged in plain text.
- JWTs are signed with a secret loaded from environment variables, never hardcoded.
- `.env` (real secrets) is excluded from version control; `.env.example` documents required variables without exposing real values.
- Login failures return a deliberately identical message ("Invalid email or password") whether the email doesn't exist or the password is wrong, to avoid leaking which emails are registered.

## What This Project Demonstrates

- Designing a normalized relational schema with JPA inheritance strategies
- Implementing an interval-overlap algorithm correctly, including edge cases
- Building stateless authentication with JWT from scratch (no Spring Security defaults)
- Layering authorization (RBAC) separately from authentication
- Writing centralized, consistent API error handling
- Containerizing a multi-service application with Docker Compose, including healthchecks and persistent volumes
- Debugging real, non-trivial issues across the stack — security filter ordering, exception-type mismatches masking as the wrong HTTP status, Docker networking, and database case-sensitivity differences between H2 and MySQL

## License

This is a personal learning/portfolio project.
