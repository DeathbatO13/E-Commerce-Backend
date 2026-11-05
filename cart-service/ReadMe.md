# Cart Service

Carrito de compras de la plataforma **E-Commerce Backend**: un carrito por usuario, identificado por el `sub` del JWT.

Puerto por defecto: **8083**. Contrato: [`docs/openapi/cart.yaml`](../docs/openapi/cart.yaml).

---

## Tecnologías

* Java 17, Spring Boot 3.5
* Spring Web, Spring Security (JWT), Spring Data JPA
* PostgreSQL + Flyway (`V1__create_cart_tables.sql`)
* `RestClient` para llamar a catalog-service
* JUnit 5, Mockito, MockMvc, `MockRestServiceServer`, H2 (tests)

---

## Arquitectura

Arquitectura hexagonal:

```
domain/
  model/        Cart, CartItem (total, fusión de items, cantidades > 0)
  port/in/      AddProductCart, UpdateCartItemQuantity, RemoveProductFromCart, GetCart, ClearCart
  port/out/     CartRepository, CatalogPort
application/
  service/      CartApplicationService
adapter/
  in/rest/      CartController, DTOs, GlobalExceptionHandler
  out/          CartPersistenceAdapter (JPA)
  out/http/     CatalogHttpAdapter (catalog-service)
config/         SecurityConfig, JwtAuthFilter, JwtProvider, CorsConfig
```

---

## Endpoints

Todos requieren un JWT con rol **CLIENT** o **ADMIN** (sin token: 401; con otro rol: 403).
El usuario sale del token, nunca del body ni de la URL.

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| GET | `/cart` | — | 200 carrito (vacío si todavía no existe) |
| POST | `/cart/items` | `{productId, quantity}` | 200 carrito · 404 producto inexistente o inactivo |
| PUT | `/cart/items/{productId}` | `{quantity}` | 200 carrito · 404 si el producto no está en el carrito |
| DELETE | `/cart/items/{productId}` | — | 204 |
| DELETE | `/cart` | — | 204 (lo llama order-service al crear un pedido) |

### Agregar un producto

```http
POST /cart/items
Authorization: Bearer <token>
Content-Type: application/json

{ "productId": "107b770c-bf8a-4f1e-9259-1536fbbe5ebf", "quantity": 2 }
```

```json
{
  "id": "8f81e074-ff5a-4902-8c82-fb3ca794e0e1",
  "userId": "33b0604d-50a9-4237-9d33-9539eede880d",
  "items": [
    { "productId": "107b770c-bf8a-4f1e-9259-1536fbbe5ebf", "quantity": 2, "price": 250.00 }
  ],
  "total": 500.00
}
```

* **El precio no se manda**: cart-service lo pide a catalog-service (`GET /products/{id}`) y así valida, de paso, que el producto exista y esté activo.
* Si el producto ya estaba en el carrito, la cantidad se **suma**. `PUT` en cambio la **reemplaza**.
* Si catalog-service falla, el carrito no se modifica: responde 502 (error inesperado de catalog) o 503 (catalog no responde).

---

## Configuración

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `server.port` | `PORT` | `8083` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` (o `DB_HOST` / `DB_PORT` / `DB_NAME` en `prod`) | `jdbc:postgresql://localhost:5432/cart_db` |
| `spring.datasource.username` / `password` | `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` |
| `security.jwt.secret` | `JWT_SECRET` | clave de desarrollo (**mismo valor en los 5 servicios**) |
| `catalog.service.url` | `CATALOG_SERVICE_URL` (o `CATALOG_SERVICE_HOSTPORT` en `prod`) | `http://localhost:8082` |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5001,http://localhost:5173` |

El esquema lo crea Flyway y Hibernate solo lo valida (`ddl-auto: validate`), así que la base `cart_db` tiene que existir antes de arrancar.

---

## Ejecutar

```bash
./mvnw spring-boot:run
```

Para agregar productos al carrito, catalog-service tiene que estar corriendo. Lo más simple es levantar todo el stack con Docker desde la raíz del repo (ver el [ReadMe raíz](../ReadMe.md)).

## Tests

```bash
./mvnw verify
```

* `CartTest` / `CartItemTest`: reglas del dominio.
* `CartApplicationServiceTest`: casos de uso (Mockito).
* `CartControllerTest` / `CorsConfigTest`: códigos HTTP, seguridad con el `JwtAuthFilter` real y CORS.
* `CatalogHttpAdapterTest`: llamadas a catalog-service simuladas con `MockRestServiceServer` (404, 500, caída).
* `CartPersistenceAdapterTest` / `FlywayMigrationTest`: persistencia y migración.

Ningún test necesita Postgres ni catalog-service.
