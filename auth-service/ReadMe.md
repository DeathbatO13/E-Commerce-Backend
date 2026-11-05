# Auth Service

Registro, login con JWT y administración de usuarios de la plataforma **E-Commerce Backend**.
Es el único servicio que **emite** tokens; los otros 4 solo los validan con el mismo `JWT_SECRET`.

Puerto por defecto: **8081**. Contrato: [`docs/openapi/auth.yaml`](../docs/openapi/auth.yaml).

---

## Tecnologías

* Java 17, Spring Boot 3.5
* Spring Web, Spring Security, Spring Data JPA
* JWT con jjwt 0.11.5 (HS256)
* PostgreSQL + Flyway (`V1__create_users_table.sql`)
* JUnit 5, Mockito, MockMvc, H2 (tests)

---

## Roles

| Rol | Cómo se obtiene | Puede |
|---|---|---|
| `CLIENT` | Al registrarse (siempre) | Comprar: carrito, pedidos, pagos |
| `ADMIN` | Se lo asigna el SUPER_ADMIN | Gestionar el catálogo, ver todos los pedidos, **listar** usuarios |
| `SUPER_ADMIN` | Lo crea el servicio al arrancar (uno solo) | Todo lo de ADMIN, y además **cambiar roles** |

El JWT lleva el id del usuario en `sub` y sus roles en el claim `roles`.

### SUPER_ADMIN inicial

Al arrancar, `SuperAdminInitializer` crea el SUPER_ADMIN si no existe, con estas propiedades:

| Propiedad | Variable de entorno | Valor en `dev` |
|---|---|---|
| `admin.email` | `ADMIN_EMAIL` | `administrator@admin.com` |
| `admin.fullname` | `ADMIN_FULLNAME` | `Administrador` |
| `admin.password` | `ADMIN_PASSWORD` | `Admin1234` |

En el perfil `prod` no hay valores por defecto: sin `ADMIN_EMAIL` y `ADMIN_PASSWORD` el servicio no arranca.

---

## Endpoints

| Método | Ruta | Acceso | Respuesta |
|---|---|---|---|
| POST | `/auth/register` | público | 201 `{id, email}` · 409 si el email existe |
| POST | `/auth/login` | público | 200 `{accessToken, tokenType, expiresIn}` · 401 |
| GET | `/admin/users` | ADMIN, SUPER_ADMIN | 200 lista de usuarios |
| POST | `/admin/users/{id}/role?role=ADMIN` | SUPER_ADMIN | 200 · 400 · 404 · 409 |

Sin token (o con uno inválido) los endpoints protegidos responden **401**; con un rol insuficiente, **403**.

### Registro

```http
POST /auth/register
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "Cliente1234",
  "fullname": "Ana Pérez"
}
```

```json
{ "id": "8d15a676-fc86-4f8e-8a1e-06737e79bfbc", "email": "cliente@example.com" }
```

El rol no se elige: todo usuario nuevo es `CLIENT`.

### Login

```http
POST /auth/login
Content-Type: application/json

{ "email": "cliente@example.com", "password": "Cliente1234" }
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

`expiresIn` está en segundos. Los otros servicios esperan el token en `Authorization: Bearer <accessToken>`.
Un usuario deshabilitado recibe 401; ese motivo solo se informa si la contraseña es correcta, para no revelar qué cuentas existen.

### Cambiar el rol de un usuario

```http
POST /admin/users/8d15a676-fc86-4f8e-8a1e-06737e79bfbc/role?role=ADMIN
Authorization: Bearer <token del SUPER_ADMIN>
```

* Reemplaza el rol del usuario por `CLIENT` o `ADMIN`.
* Asignar `SUPER_ADMIN` responde **400**; cambiar el rol del SUPER_ADMIN, **409**.

---

## Configuración

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `server.port` | `PORT` | `8081` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` (o `DB_HOST` / `DB_PORT` / `DB_NAME` en `prod`) | `jdbc:postgresql://localhost:5432/auth_db` |
| `spring.datasource.username` / `password` | `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` |
| `security.jwt.secret` | `JWT_SECRET` | clave de desarrollo (**mismo valor en los 5 servicios**) |
| `security.jwt.expiration` | `JWT_EXPIRATION` | `86400000` ms (24 h) |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5001,http://localhost:5173` |

El esquema lo crea Flyway y Hibernate solo lo valida (`ddl-auto: validate`), así que la base `auth_db` tiene que existir antes de arrancar.

---

## Ejecutar

```bash
./mvnw spring-boot:run
```

O todo el stack con Docker desde la raíz del repo (ver el [ReadMe raíz](../ReadMe.md)).

## Tests

```bash
./mvnw verify
```

* `RegisterTest`: casos de uso de registro, login y cambio de rol (Mockito).
* `UserTest`: reglas del dominio (no asignar SUPER_ADMIN, no cambiar su rol).
* `AuthControllerTest` / `UserControllerTest`: códigos HTTP y seguridad con el `JwtAuthFilter` real.
* `FlywayMigrationTest`: la migración coincide con las entidades JPA.

Ningún test necesita Postgres: usan H2 con el perfil `test`.
`verify` además falla si `domain` o `application` bajan del 80% de cobertura (JaCoCo).
