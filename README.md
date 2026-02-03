# Spring-Boot-API

API REST de ejemplo para gestionar estudiantes y cursos con Spring Boot. Enfocada en claridad, prácticas simples y aprendizaje.

**Objetivo**
- Practicar arquitectura por capas en Spring Boot.
- Trabajar entidades JPA y relaciones muchos-a-muchos.
- Usar validaciones con Bean Validation.
- Exponer endpoints REST coherentes.
- Escribir tests básicos de servicios y repositorios.

**Stack**
- Java 21.
- Spring Boot 3.5.6.
- Spring Web.
- Spring Data JPA (Hibernate).
- Bean Validation (Jakarta Validation).
- PostgreSQL.
- H2 (tests).
- Maven.
- Lombok.
- JUnit 5 + Mockito + AssertJ.
- Docker Compose (opcional).

**Requisitos**
- Java 21.
- Maven o `./mvnw`.
- Docker opcional para Postgres.

**Base de datos con Docker Compose**
1. `docker compose up -d`
2. Postgres en `localhost:5432`.
3. Base por defecto: `springboot_api`.

**Perfiles**
- Spring profile por defecto: `dev` (definido en `application.properties`).
- Cambiar con `SPRING_PROFILES_ACTIVE`.
- En Maven, el perfil `prod` está activo por defecto en el `pom.xml`. No afecta el profile de Spring salvo que lo establezcas.

**Ejecución local**
1. `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`
2. API en `http://localhost:9081/api/v1`.

**Modelo de datos**
- `Student` con `firstName`, `lastName`, `studentNumber`, `createdAt`, `updatedAt`.
- `Course` con `code`, `title`, `description`, `createdAt`, `updatedAt`.
- Relación muchos-a-muchos vía `student_course`.
- `studentNumber` es único.

**Validaciones**
- `firstName` y `lastName` obligatorios, máximo 60.
- `studentNumber` obligatorio, máximo 30 y único (409 si se repite).
- `code` obligatorio, máximo 10.
- `title` obligatorio, máximo 150.
- `description` obligatoria, máximo 255.

**Endpoints principales**
- `GET /api/v1/students?lastName=...&firstName=...&studentNumber=...&page=...&size=...`
- `GET /api/v1/students/{id}`
- `POST /api/v1/students`
- `PUT /api/v1/students/{id}`
- `DELETE /api/v1/students/{id}`
- `GET /api/v1/students/{id}/courses?page=...&size=...`
- `POST /api/v1/students/{idStudent}/courses/{idCourse}` (409 si ya estaba asignado)
- `GET /api/v1/courses?code=...&title=...&description=...&page=...&size=...`
- `GET /api/v1/courses/{id}`
- `POST /api/v1/courses`
- `PUT /api/v1/courses/{id}`
- `DELETE /api/v1/courses/{id}`
- `GET /api/v1/courses/{id}/students?page=...&size=...`

**Ejemplos rápidos**
```bash
curl -X POST http://localhost:9081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ana","lastName":"Lopez","studentNumber":"STU-0001"}'
```

```bash
curl -X POST http://localhost:9081/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{"code":"COD-101","title":"Intro","description":"Basico"}'
```

```bash
curl -X POST http://localhost:9081/api/v1/students/1/courses/1
```

**Tests**
- Ejecutar: `./mvnw clean test`.

