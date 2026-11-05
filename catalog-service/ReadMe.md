# Catalog Service

Productos y categorías de la plataforma **E-Commerce Backend**.
Además de servir el catálogo al frontend, es la **fuente del precio**: cart-service y order-service le piden el precio vigente de cada producto.

Puerto por defecto: **8082**. Contrato: [`docs/openapi/catalog.yaml`](../docs/openapi/catalog.yaml).

---

## Tecnologías

* Java 17, Spring Boot 3.5
* Spring Web, Spring Security (JWT), Spring Data JPA
* PostgreSQL + Flyway (`V1__create_catalog_tables.sql`)
* JUnit 5, Mockito, MockMvc, H2 (tests)

---

## Arquitectura

Arquitectura hexagonal:

```
domain/
  model/        Product, Category (Product valida nombre, precio > 0, stock >= 0 y categoría)
  port/in/      casos de uso (Create/Update/Delete/List/Get/Search ...)
  port/out/     ProductRepository, CategoryRepository
application/
  service/      ProductApplicationService, CategoryApplicationService
adapter/
  in/rest/      ProductController, CategoryController, DTOs, GlobalExceptionHandler
  out/persistence/  ProductAdapter, CategoryAdapter (JPA), mappers
config/         SecurityConfig, JwtAuthFilter, JwtProvider, CorsConfig
```

---

## Seguridad

* Todos los `GET` son **públicos**: se puede navegar el catálogo sin login (un token vencido no bloquea la navegación).
* `POST`, `PUT` y `DELETE` requieren un JWT con rol **ADMIN** o **SUPER_ADMIN**: sin token responden 401, con otro rol 403.

---

## Endpoints

| Método | Ruta | Acceso | Notas |
|---|---|---|---|
| GET | `/categories` | público | Solo categorías activas |
| POST | `/categories` | ADMIN | 201 · 409 si ya existe una activa con ese nombre |
| PUT | `/categories/{id}` | ADMIN | Renombra · 404 |
| DELETE | `/categories/{id}` | ADMIN | 204 · 409 si tiene productos activos |
| GET | `/products` | público | Filtros opcionales `?categoryId=` o `?name=` |
| GET | `/products/{id}` | público | 404 si no existe o está inactivo |
| POST | `/products` | ADMIN | 201 · 404 si la categoría no existe o está inactiva |
| PUT | `/products/{id}` | ADMIN | Reemplaza todos los campos · 404 si el producto no existe (no lo crea) |
| DELETE | `/products/{id}` | ADMIN | 204 |

"ADMIN" quiere decir ADMIN o SUPER_ADMIN.

**Borrado lógico**: `DELETE` pone `active = false`. Lo inactivo deja de aparecer en los listados y en `GET /products/{id}`, así que tampoco se puede agregar al carrito.

**Búsqueda**: `?name=tv` busca por coincidencia parcial sin distinguir mayúsculas. Si se envían los dos filtros, `categoryId` tiene prioridad.

### Crear un producto

```http
POST /products
Authorization: Bearer <token ADMIN>
Content-Type: application/json

{
  "name": "TV LCD 55\"",
  "description": "Smart TV 4K",
  "price": 899.90,
  "stock": 10,
  "categoryId": "b5f1a3c4-2f1b-4f7a-9f6e-9a2e6d9c1f44"
}
```

```json
{
  "id": "107b770c-bf8a-4f1e-9259-1536fbbe5ebf",
  "name": "TV LCD 55\"",
  "description": "Smart TV 4K",
  "price": 899.90,
  "stock": 10,
  "categoryId": "b5f1a3c4-2f1b-4f7a-9f6e-9a2e6d9c1f44",
  "active": true
}
```

En `PUT /products/{id}` el body es el mismo más `active`. **Si se omite `active`, el producto queda inactivo.**

---

## Configuración

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `server.port` | `PORT` | `8082` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` (o `DB_HOST` / `DB_PORT` / `DB_NAME` en `prod`) | `jdbc:postgresql://localhost:5432/catalog_db` |
| `spring.datasource.username` / `password` | `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` |
| `security.jwt.secret` | `JWT_SECRET` | clave de desarrollo (**mismo valor en los 5 servicios**) |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5001,http://localhost:5173` |

El esquema lo crea Flyway y Hibernate solo lo valida (`ddl-auto: validate`), así que la base `catalog_db` tiene que existir antes de arrancar.

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

* `ProductTest`: invariantes del dominio.
* `ProductServiceTest` / `CategoryServiceTest`: casos de uso (Mockito).
* `ProductControllerTest` / `CategoryControllerTest`: códigos HTTP y seguridad con el `JwtAuthFilter` real.
* `CatalogPersistenceAdapterTest`: mapeo JPA y borrado lógico (`@DataJpaTest`).
* `FlywayMigrationTest`: la migración coincide con las entidades JPA.

Ningún test necesita Postgres: usan H2 con el perfil `test`.
