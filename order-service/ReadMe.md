# Order Service

Pedidos de la plataforma **E-Commerce Backend**. Un pedido se crea **solo** a partir del carrito del usuario y pasa a pagado cuando payment-service aprueba el pago.

Puerto por defecto: **8084**. Contrato: [`docs/openapi/order.yaml`](../docs/openapi/order.yaml).

---

## Tecnologías

* Java 17, Spring Boot 3.5
* Spring Web, Spring Security (JWT + token interno), Spring Data JPA
* PostgreSQL + Flyway (`V1__create_order_tables.sql`)
* `RestClient` para llamar a cart-service y catalog-service
* JUnit 5, Mockito, MockMvc, `MockRestServiceServer`, H2 (tests)

---

## Arquitectura

Arquitectura hexagonal:

```
domain/
  model/        Order, OrderItem, OrderStatus, CartSnapshot
  ports/in/     CreateOrder, GetOrder, PayOrder, CancelOrder
  ports/out/    OrderRepositoryPort, CartPort, CatalogPort
application/
  service/      OrderService
adapter/
  in/rest/      OrderController, DTOs, GlobalExceptionHandler
  out/persistence/  OrderAdapter (JPA), OrderMapper
  out/http/     CartHttpAdapter, CatalogHttpAdapter
  config/       BeanConfiguration
config/         SecurityConfig, JwtAuthFilter, InternalServiceAuthFilter, JwtProvider, CorsConfig
```

---

## Estados del pedido

```
CREATED ──(pago aprobado)──▶ PAID
   │
   └──(cancelar)──▶ CANCELLED
```

* Solo un pedido `CREATED` se puede pagar.
* Un pedido `PAID` no se puede cancelar (409).

---

## Endpoints

| Método | Ruta | Acceso | Respuesta |
|---|---|---|---|
| POST | `/orders` | CLIENT, ADMIN, SUPER_ADMIN | 201 + header `Location: /orders/{id}` |
| GET | `/orders` | CLIENT, ADMIN, SUPER_ADMIN | 200 · un CLIENT ve solo los suyos |
| GET | `/orders/{id}` | CLIENT, ADMIN, SUPER_ADMIN | 200 · 403 si es de otro usuario · 404 |
| POST | `/orders/{id}/cancel` | CLIENT, ADMIN, SUPER_ADMIN | 200 · 403 · 404 · 409 si ya está pagado |
| POST | `/orders/{id}/pay` | **solo payment-service** (`X-Internal-Token`) | 200 · 404 · 409 |

ADMIN y SUPER_ADMIN ven y cancelan pedidos de cualquier usuario.

### Crear un pedido

```http
POST /orders
Authorization: Bearer <token>
Content-Type: application/json

{ "address": "Calle 123, Bogotá" }
```

Respuesta: **201** sin cuerpo, con `Location: /orders/96673ffd-e3cd-4c93-8b08-9efed5973223`.
CORS expone ese header, así que el frontend puede leer el id desde el navegador.

Qué pasa por dentro:

1. Lee el carrito del usuario en cart-service (`GET /cart`), reenviando su token.
2. Pide a catalog-service el **precio vigente** de cada producto y calcula el total. Los precios guardados en el carrito no se usan.
3. Guarda el pedido en `CREATED` y vacía el carrito (`DELETE /cart`).

Errores posibles:

| Situación | Respuesta |
|---|---|
| Carrito vacío | 400 |
| Algún producto ya no existe o está inactivo | 404 (no se guarda el pedido ni se vacía el carrito) |
| cart-service rechaza el token | 401 |
| cart o catalog responden con un error inesperado | 502 |
| cart o catalog no responden | 503 |

### `POST /orders/{id}/pay` (interno)

Lo llama payment-service cuando aprueba un pago. Se autentica con el header `X-Internal-Token` (`INTERNAL_SERVICE_TOKEN`, el mismo valor en los dos servicios), no con un JWT: un token de usuario, aunque sea ADMIN, recibe **403**, y un token interno incorrecto, **401**.

---

## Configuración

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `server.port` | `PORT` | `8084` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` (o `DB_HOST` / `DB_PORT` / `DB_NAME` en `prod`) | `jdbc:postgresql://localhost:5432/order_db` |
| `spring.datasource.username` / `password` | `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` |
| `security.jwt.secret` | `JWT_SECRET` | clave de desarrollo (**mismo valor en los 5 servicios**) |
| `security.internal-token` | `INTERNAL_SERVICE_TOKEN` | `dev-internal-token-change-me` (**debe coincidir con payment-service**) |
| `cart.service.url` | `CART_SERVICE_URL` (o `CART_SERVICE_HOSTPORT` en `prod`) | `http://localhost:8083` |
| `catalog.service.url` | `CATALOG_SERVICE_URL` (o `CATALOG_SERVICE_HOSTPORT` en `prod`) | `http://localhost:8082` |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5001,http://localhost:5173` |

El esquema lo crea Flyway y Hibernate solo lo valida (`ddl-auto: validate`), así que la base `order_db` tiene que existir antes de arrancar.

---

## Ejecutar

```bash
./mvnw spring-boot:run
```

Para crear pedidos, cart-service y catalog-service tienen que estar corriendo. Lo más simple es levantar todo el stack con Docker desde la raíz del repo (ver el [ReadMe raíz](../ReadMe.md)).

## Tests

```bash
./mvnw verify
```

* `OrderTest` / `OrderItemTest`: estados e invariantes del dominio.
* `OrderServiceTest`: casos de uso (Mockito), incluido que el total salga de catalog.
* `OrderControllerTest`: códigos HTTP, ownership y seguridad (JWT real y token interno).
* `CartHttpAdapterTest` / `CatalogHttpAdapterTest`: llamadas a otros servicios con `MockRestServiceServer`.
* `OrderPersistenceAdapterTest` / `FlywayMigrationTest`: persistencia y migración.

Ningún test necesita Postgres ni los otros servicios.

---

## Limitaciones conocidas

* Crear un pedido no es atómico entre servicios: si el pedido se guarda pero `DELETE /cart` falla, el pedido queda creado y el carrito no se vacía.
* No hay control de stock: el pedido no descuenta unidades en catalog-service.
