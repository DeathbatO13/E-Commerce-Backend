# Payment Service

Microservicio de pagos **simulado** de la plataforma **E-Commerce Backend**.
No se conecta a ninguna pasarela real: registra cada intento de pago y, si se aprueba, marca el pedido como pagado en order-service.

Puerto por defecto: **8085**. Contrato: [`docs/openapi/payment.yaml`](../docs/openapi/payment.yaml).

---

## Tecnologías

* Java 17, Spring Boot 3.5
* Spring Web, Spring Security (JWT), Spring Data JPA
* PostgreSQL + Flyway (`V1__init_payment_schema.sql`)
* `RestClient` para llamar a order-service
* JUnit 5, Mockito, MockMvc

---

## Arquitectura

Arquitectura hexagonal, igual que el resto de los servicios:

```
domain/
  model/        Payment, PaymentStatus, OrderSummary
  port/in/      ProcessPaymentUseCase
  port/out/     PaymentRepositoryPort, OrderPort
application/
  service/      PaymentApplicationService
adapter/
  in/rest/      PaymentController, DTOs, GlobalExceptionHandler
  out/persistence/  PaymentAdapter (JPA)
  out/http/     OrderHttpAdapter (order-service)
config/         SecurityConfig, JwtAuthFilter, JwtProvider, BeanConfiguration
```

---

## Flujo de `POST /payments/process`

1. El usuario envía `orderId`, `amount` y, opcionalmente, `simulateResult` con su JWT.
2. payment-service pide el pedido a order-service (`GET /orders/{id}`) **con el token del usuario**.
   order-service aplica el ownership: si el pedido es de otro usuario responde 403, y si no existe, 404.
3. Se valida que el pedido esté en `CREATED` (si no, **409**) y que `amount` coincida con su total (si no, **400**).
4. Se registra el pago con el resultado simulado: `simulateResult` o, si no se envía, `APPROVED`.
5. Si es `APPROVED`, payment-service llama a `POST /orders/{id}/pay` con el header `X-Internal-Token`.
   Ese endpoint solo acepta el token interno de servicio, nunca el JWT de un usuario.

Un pago `REJECTED` también se guarda (auditoría), pero el pedido sigue en `CREATED` y se puede reintentar.

### Ejemplo

```http
POST /payments/process
Authorization: Bearer <jwt>
Content-Type: application/json

{
  "orderId": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
  "amount": 150.00,
  "simulateResult": "REJECTED"
}
```

```json
{
  "id": "6f1c...",
  "orderId": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
  "amount": 150.00,
  "status": "REJECTED",
  "transactionId": "TX-3F9A1C2B",
  "createdAt": "2026-11-01T10:15:30"
}
```

---

## Configuración

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `server.port` | `PORT` | `8085` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/payment_db` |
| `spring.datasource.username` / `password` | `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` |
| `security.jwt.secret` | `JWT_SECRET` | mismo valor de desarrollo que los otros 4 servicios |
| `security.internal-token` | `INTERNAL_SERVICE_TOKEN` | `dev-internal-token-change-me` (**debe coincidir con order-service**) |
| `order.service.url` | `ORDER_SERVICE_URL` | `http://localhost:8084` |

`ddl-auto` está en `validate`: el esquema lo crea Flyway, así que la base `payment_db` debe existir antes de arrancar.

---

## Ejecutar

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

* `PaymentApplicationServiceTest`: reglas del caso de uso (aprobado, rechazado, monto distinto, pedido ya pagado, pedido inexistente).
* `PaymentControllerTest`: códigos HTTP (200, 400, 403, 409) y autenticación.

Los tests no necesitan Postgres ni los otros servicios.

---

## Limitaciones conocidas

* Si se guarda un pago `APPROVED` pero la llamada a `/orders/{id}/pay` falla, el pago queda registrado y el pedido sigue en `CREATED`. Para un MVP alcanza; en un sistema real se resolvería con un estado intermedio y reintentos, o con un patrón saga.
* No hay idempotencia: dos requests simultáneos para el mismo pedido pueden registrar dos pagos. order-service rechaza el segundo `/pay` (409), pero el registro duplicado queda en `payments`.
