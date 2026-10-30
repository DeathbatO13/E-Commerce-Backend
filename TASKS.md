# Backlog Técnico – E-commerce Backend

> Documento de referencia para completar el MVP, agregarle pruebas y dejarlo listo para desplegar en Render con CI/CD en GitHub Actions.
> Cada tarea incluye una explicación breve de **qué hace y por qué importa**, pensada para ir aprendiendo el concepto mientras se implementa. Márcalas con `[x]` a medida que las completes.

---

## Cómo usar este documento

Está organizado en **fases**. Sigue el orden: cada fase depende en parte de la anterior (por ejemplo, no tiene sentido dockerizar un servicio que todavía tiene bugs funcionales). Dentro de cada fase, las tareas están agrupadas por servicio o por tema.

Antes de empezar, un mini-glosario de términos que vas a ver repetidos:

- **Puerto (port)**: en arquitectura hexagonal, una interfaz que define "qué necesita o qué ofrece" el dominio, sin decir cómo se implementa. Hay puertos **de entrada** (`in`, ej. `CreateOrderUseCase`: lo que alguien puede pedirle al servicio) y puertos **de salida** (`out`, ej. `CartRepository`: lo que el servicio necesita de afuera, como guardar en una base de datos).
- **Adaptador (adapter)**: la implementación concreta de un puerto. Un `adapter/in/rest/OrderController` adapta una petición HTTP a una llamada al puerto de entrada. Un `adapter/out/persistence/OrderAdapter` adapta el puerto de salida a JPA/Postgres.
- **DTO (Data Transfer Object)**: un objeto simple usado para mover datos entre capas (ej. lo que llega en el body de un request), distinto del modelo de dominio.
- **JWT (JSON Web Token)**: un token firmado que el auth-service genera al hacer login, y que los demás servicios verifican (sin llamar a auth-service) para saber quién es el usuario y qué rol tiene.
- **CI (Integración Continua)**: automatizar que, en cada cambio de código, se compile y corran los tests, para detectar errores antes de que lleguen a producción.
- **CD (Despliegue Continuo)**: automatizar que, si el código pasa CI, se despliegue solo (en este caso, Render lo hace al detectar un push).
- **Contenedor / Docker**: empaquetar la app + su entorno de ejecución en una imagen que corre igual en cualquier máquina. Cada microservicio va a tener su propio `Dockerfile`.

---

## Fase 0 — Diagnóstico (contexto, ya identificado)

Estos son los problemas concretos encontrados al revisar el código actual. No son tareas nuevas: son la causa raíz de varias tareas de las fases siguientes, listados aquí para que entiendas el "por qué" cuando llegues a corregirlos.

> **Última revisión: 2026-10-28**, sobre `develop` actualizado (`f186f28`) más la reparación del merge (ver 0.1). Se ejecutó `mvnw test` (JDK 17) en cada servicio:
>
> | Servicio | Compila | Tests | Notas |
> |---|---|---|---|
> | auth-service | ✅ | ✅ 20/20 | requiere Postgres local con `auth_db` |
> | catalog-service | ✅ | ✅ 15/15 | `contextLoads` usa el perfil `test` (H2) |
> | cart-service | ✅ | ✅ 14/14 | `contextLoads` y `@DataJpaTest` usan el perfil `test` (H2) |
> | order-service | ✅ | ✅ 15/15 | incluye `contextLoads` contra `order_db` |
> | payment-service | ✅ | — (sin tests) | |
>
> ⚠️ **Corrección del diagnóstico del 25-oct:** se hizo sobre un `develop` local desactualizado (`1ad3395`). `origin/develop` ya tenía commiteado payment-service, la Fase 2 de order y el manejo de errores (`d62e8bf`, `de778d0`, `ef16919`). No se había "perdido" código: solo faltaba sincronizar con `git pull`. Varias tareas marcadas abajo como "recuperadas" ya existían en remoto.

### 0.1 Bloqueantes

- [ ] **`develop` en remoto (`f186f28`) no compila.** Los merges `25eee15` (PR #101) y `c7407ac` (PR #102) se resolvieron conservando *ambos* lados de cada conflicto: beans y constructores duplicados en `BeanConfiguration` y `OrderService`, `<version>` duplicado en `payment-service/pom.xml`, dos claves `security:` en `order-service/application.yaml` y un `=======` suelto en `TASKS.md`. **Reparado en el working tree (28-oct), pendiente de commit**: se tomó la versión coherente de cada archivo, conservando lo que aportaba develop (JWT unificado y el constructor privado de `RestMapper`).
- [ ] **`payment-service/target/` quedó commiteado** (24 archivos de compilación) a pesar de su `.gitignore`. Sacarlo del índice con `git rm -r --cached payment-service/target`.
- [x] **El secreto JWT no es el mismo entre servicios**: resuelto en `ef16919`. Los 5 servicios leen `security.jwt.secret` con `${JWT_SECRET:...}` y el mismo valor por defecto.
- [x] **cart-service nunca guarda los items**: resuelto (`CartMapper.toEntity` agrega los items a la entidad).
- [x] **`POST /products` siempre responde 500** en catalog-service: `@NotBlank` sobre `BigDecimal`, `int` y `UUID` lanzaba `UnexpectedTypeException`. Resuelto (28-oct) en `CreateProductRequest` y `UpdateProductRequest` con `@NotNull` / `@Positive` / `@PositiveOrZero`; lo cubre `ProductControllerTest.shouldRejectProductWithInvalidPrice`.

> **Cómo evitar que se repita:** al resolver un conflicto, no basta con borrar los marcadores `<<<<<<<` / `=======` / `>>>>>>>`; hay que quedarse con una sola versión de cada bloque. Antes de commitear un merge, compilar (`./mvnw -q compile`) todos los servicios tocados. Esto es justamente lo que automatiza el CI de la Fase 6.

### 0.2 Diagnóstico original (actualizado)

- [ ] **`CatalogPort` en cart-service nunca se inyecta** (`cart-service/.../CartApplicationService.java`): es un campo de clase sin constructor ni `@Autowired`, siempre queda `null`. Cualquier intento de agregar un producto al carrito revienta con `NullPointerException`. Tampoco existe ninguna clase que implemente ese puerto (falta el adaptador HTTP hacia catalog-service).
- [ ] **Order-service no crea pedidos desde el carrito**: hoy el cliente manda `items` y `total` directamente en el request, lo cual contradice la regla de negocio "un pedido solo puede crearse a partir de un carrito activo" y permitiría a cualquier usuario inventar precios.
- [x] **Paquetes duplicados en order-service**: resuelto (se eliminó `order_service.persistence.*`, conservando únicamente `adapter.out.persistence.*`).
- [x] **Typo `"ROLE_ADMMIN"`** en `OrderController`: resuelto. Ojo: el listado sigue roto porque está comentado (ver 0.1).
- [x] **Ruta duplicada `/admin/admin/...`** en `UserController` de auth-service, por sumar el path del método al del `@RequestMapping` de clase.
- [x] **Listado de usuarios restringido solo a SUPER_ADMIN** (resuelto el 29-oct, ver 1.3), cuando la regla de negocio dice que ADMIN también debería poder listarlos. La restricción está en **dos lugares**: el `@PreAuthorize` de clase y `requestMatchers("/admin/**").hasRole("SUPER_ADMIN")` en `SecurityConfig`.
- [x] **El manejo global de errores es incompleto**: resuelto (28-oct), ver 3.1.
- [ ] **Sin Dockerfile** en cart-service, catalog-service y order-service (solo existe en auth-service, y no es multi-stage). Sin `docker-compose.yml` raíz. Sin `render.yaml`. Sin `.github/workflows`.
- [ ] **Secrets hardcodeados** (contraseñas de DB, JWT secret, password del super admin) directamente en los `application*.yml`, en vez de venir de variables de entorno — inviable para producción/Render.
- [ ] **CORS solo configurado en auth-service** — el frontend no podrá llamar a los demás servicios desde el navegador.
- [ ] **Flyway roto donde existe**: en auth-service está deshabilitado y `V1__create_users_table.sql` no coincide con la entidad (falta `fullname`, y la tabla de roles `user_roles` no es la que genera JPA). En cart-service el archivo se llama `v1__...` en minúscula, así que Flyway lo ignora (el prefijo `V` distingue mayúsculas).

### 0.3 Hallazgos nuevos de menor severidad

- [x] **Hexagonal violada en cart-service**: resuelto (28-oct), ver 1.2.
- [ ] **Hexagonal violada en catalog-service**: `ProductController` depende de la clase concreta `CategoryApplicationService` en vez del puerto `ConfirmCategoryExistsUseCase`. Por ahora `ProductControllerTest` mockea `CategoryApplicationService`; al pasar al puerto, cambiar ese mock.
- [ ] **Versiones inconsistentes**: Spring Boot 3.5.9 / 3.5.10 / 3.5.11 según el servicio. springdoc 2.3.0 y 2.5.0 no son compatibles con Boot 3.5 (necesita 2.8.x), y cart y order ni siquiera lo incluyen. order-service mezcla `jjwt-impl 0.12.6` con `jjwt-api 0.11.5`.
- [ ] **Repos git anidados**: cada servicio tiene un `.git` vacío (rama `master` sin commits) que dejó Spring Initializr. Confunde al IDE y a git.
- [ ] **`.gitignore` raíz**: incluye `TASKS.md`, pero el archivo ya está trackeado (la regla no tiene efecto). También lista `README-TAREAS.md`, que no existe.

---

## Fase 1 — Corregir bugs funcionales existentes

Objetivo: que lo que "ya está hecho" funcione de verdad antes de sumar código nuevo.

### 1.0 Bloqueantes (hacer primero)

- [x] **payment-service completo y coherente**: el servicio ya estaba en `origin/develop` (`d62e8bf`, `de778d0`). En PR #102 se alinearon jjwt a 0.11.5, Java 17 en el pom y `JwtProvider` con `security.jwt.secret`.
- [ ] **Corregir lo que quedó pendiente en payment-service**:
  - ~~`OrderHttpAdapter` no reenvía el JWT~~: resuelto con el token interno de servicio (`X-Internal-Token`), y el endpoint `/pay` ya existe en order-service.
  - No hay forma de simular un pago `REJECTED`: con `@Positive` en `amount`, todo pago válido se aprueba.
  - No se valida que `amount` coincida con el total del pedido.
  - El parámetro `paymentToken` recibe el header `Authorization` pero no se usa.
  - No tiene tests ni `ReadMe.md`.
- [x] **Fase 1, Fase 2 de order y Fase 3 parcial presentes en `develop`**:
  - **Fase 1**: `CartMapper` persiste los items, `CatalogHttpAdapter` + `CatalogPort` inyectado en cart, ownership y `OrderResponse` en `GET /orders/{id}`, `GET /orders` funcionando, y en `UserController` la ruta sin `/admin/admin` y `listUsers` para ADMIN.
  - **Fase 2 de order-service**: `CreateOrderRequest` solo con `address`, `CartPort` / `CatalogPort` con sus adaptadores HTTP, precios verificados contra catalog, vaciado del carrito y `POST /orders/{id}/pay` y `/cancel`.
  - **Fase 3 parcial**: `GlobalExceptionHandler` + `ErrorResponse` en los 5 servicios, actuator en los 5 y Flyway en catalog y order.
- [x] **`BeanConfiguration` de order registraba la misma instancia de `OrderService` 5 veces** (un bean más un alias por cada puerto de entrada), así que Spring encontraba "2 beans OrderService" y el contexto no arrancaba. Se dejó un único bean `orderService`; Spring lo inyecta por cualquiera de sus interfaces.
- [ ] **Pendientes de la Fase 2 / 3 en order, cart y auth**:
  - [x] **`POST /orders/{id}/pay` restringido a payment-service**: `InternalServiceAuthFilter` valida el header `X-Internal-Token` (comparación en tiempo constante) y asigna `ROLE_SERVICE`; `SecurityConfig` exige ese rol en `/orders/*/pay`. CLIENT y ADMIN reciben 403. payment-service envía el header desde `OrderHttpAdapter`. Configuración: `security.internal-token: ${INTERNAL_SERVICE_TOKEN:...}`, con el mismo valor en order y payment. Cubierto por 4 tests en `OrderControllerTest`.
  - [x] **Contrato cart → order alineado**: `CartResponse.item` renombrado a `items`, igual que `CartDto` de order y `cart.yaml`.
  - ~~El handler genérico convertía en 500 los 403, los 404, los JSON malformados y los errores de `RestClient`~~: resuelto (28-oct), ver 3.1.
  - Flyway en catalog y order sin migraciones: si `catalog_db` / `order_db` ya tienen tablas creadas por `ddl-auto`, Flyway falla con *non-empty schema without history table*. Crear las `V1__` (3.4) o usar `baseline-on-migrate` mientras tanto.
  - `OrderService` (capa de aplicación) importa `CartDto` del adaptador HTTP: mover un modelo equivalente al dominio o a `domain/ports/out`.
  - ~~`SecurityConfig` de auth bloqueaba a ADMIN en `/admin/**`~~: resuelto (29-oct), ver 1.3.
- [x] **Hacer compilar order-service**: compila y pasa 9/9 tests (incluido `contextLoads`). `@Enumerated` sobre `String` lo quitó `ef16919`, y el mock de `JwtProvider` se agregó en `OrderControllerTest`.
- [x] **Unificar la configuración JWT**: resuelto en `ef16919` (ver 0.1).

### 1.1 Order-service

- [x] **Eliminar el paquete de persistencia duplicado.** Quedarte solo con `adapter.out.persistence.*` (es el que respeta la convención hexagonal del resto del proyecto) y borrar `order_service.persistence.*`.
- [x] **Corregir `"ROLE_ADMMIN"` → `"ROLE_ADMIN"`** en `OrderController`. *Por qué:* es la condición que decide si un usuario ve todos los pedidos (admin) o solo los suyos; con el typo, esa rama nunca se ejecuta.
- [x] **Agregar validación de "ownership" en `GET /orders/{id}`**: hoy cualquier usuario autenticado puede ver el pedido de otro usuario solo sabiendo el ID. Hay que verificar que `order.getUserId()` sea igual al usuario autenticado, o que el usuario sea ADMIN/SUPER_ADMIN.
- [x] **No devolver el modelo de dominio** en `GET /orders/{id}`: mapear a `OrderResponse`, igual que en el listado.
- [ ] **Corregir los matchers de `SecurityConfig`**: usan `/order/**`, pero la ruta real es `/orders`, así que nunca coinciden.
- [ ] **Quitar `@Enumerated` de `OrderEntity.status`** (es un `String`; o cambiar el tipo a `OrderStatus`, que es lo más limpio).
- [ ] **Alinear versiones de jjwt** (`jjwt-impl` está en 0.12.6 y `jjwt-api` / `jjwt-jackson` en 0.11.5) y quitar `spring-boot-starter-oauth2-resource-server`, que no se usa.

### 1.2 Cart-service

- [x] **Persistir los items**: en `CartMapper.toEntity`, asignar la lista de `CartItemJpaEntity` construida a la entidad del carrito (hoy se crea y se descarta).
- [x] **Quitar las anotaciones JPA del dominio** (`@Entity`, `@Table`, `@Id`, `@OneToMany`, `@ManyToOne` en `Cart` y `CartItem`). Hecho (28-oct); también se quitaron los constructores vacíos que solo existían para JPA. *Por qué:* el dominio no debe conocer la base de datos; además, hoy hay dos entidades JPA mapeando las mismas tablas.
- [x] **Evitar `LazyInitializationException`**: `CartJpaRepository.findByUserId` / `findById` usan `@EntityGraph(attributePaths = "items")` y `CartPersistenceAdapter.save` es `@Transactional`, así el mapeo ocurre con los items ya cargados. Lo cubre `CartPersistenceAdapterTest` (corre sin transacción de test, como en producción).
- [x] **No se podía agregar un producto a un carrito ya guardado**: `CartMapper.toDomain` armaba los items con `.toList()` (lista inmutable) y `Cart.addProduct` lanzaba `UnsupportedOperationException` → 500. Ahora `Cart` copia la lista a un `ArrayList` y `getItems()` expone una vista de solo lectura.
- [x] **Corregir `deleteByUserId`**: ahora usa la query derivada `CartJpaRepository.deleteByUserId` (antes llamaba a `deleteById(userId)`).
- [ ] **Renombrar la migración** `v1__create_cart_tables.sql` → `V1__create_cart_tables.sql`, y pasar a `ddl-auto: validate` (ver 3.4).
- [x] **Quitar `price` de `AddItemRequest`**: se quitó del DTO y de `AddProductCartUseCase.addProduct`; el precio sale siempre de `CatalogPort` (y se consulta antes de tocar el carrito).
- [x] **Validar los requests**: `@Valid` en el controller, `@NotNull productId` y `@Positive quantity` (mínimo 1 en ambos, como dice `cart.yaml`; para quitar un producto está `DELETE /cart/items/{productId}`).
- [x] **Renombrar `CartResponse.item` → `items`**, como dice el contrato `cart.yaml`.
- [ ] **Permitir el carrito a SUPER_ADMIN** (o documentar que no puede comprar): hoy `/cart/**` solo admite ADMIN y CLIENT.
- [x] **Implementar el adaptador HTTP para `CatalogPort`**: una clase en `adapter/out` que llame por HTTP a `GET /products/{id}` de catalog-service (usando `RestClient` de Spring, que es el cliente HTTP moderno recomendado desde Spring 6).
- [x] **Inyectar `CatalogPort` correctamente** en `CartApplicationService` (por constructor, como ya se hace con `CartRepository`). *Por qué:* Spring solo puede inyectar dependencias que reciba explícitamente (constructor o `@Autowired`); un campo declarado "a mano" sin eso queda siempre `null`.
- [x] **Validar que el producto exista y esté activo** antes de agregarlo al carrito (regla de negocio: "no se pueden agregar productos inexistentes o deshabilitados").

### 1.3 Auth-service

- [x] **Corregir la ruta duplicada**: dejar `@PostMapping("/users/{id}/role")` (sin repetir `/admin`, porque ya está en el `@RequestMapping` de la clase).
- [x] **Permitir que ADMIN también liste usuarios** (29-oct): `SecurityConfig` usa `hasAnyRole("ADMIN", "SUPER_ADMIN")` en `/admin/**` y `changeRole` sigue exigiendo SUPER_ADMIN con `@PreAuthorize`; lo cubre `UserControllerTest`. Antes: mover el `@PreAuthorize("hasRole('SUPER_ADMIN')")` del nivel de clase al método `changeRole` únicamente, dejando `listUsers` accesible para ADMIN y SUPER_ADMIN (✅ hecho en `UserController`). **Falta**, cambiar en `SecurityConfig` `requestMatchers("/admin/**").hasRole("SUPER_ADMIN")` por `hasAnyRole("ADMIN", "SUPER_ADMIN")`; si no, el filtro sigue bloqueando a ADMIN antes de llegar al `@PreAuthorize`.
- [x] **Proteger al SUPER_ADMIN** (29-oct): `User.changeRole` (reemplaza al `@Setter` de Lombok) rechaza asignar SUPER_ADMIN (400) y cambiar el rol de un SUPER_ADMIN (409). La validación salió del controller.
- [x] **Rechazar el login de usuarios deshabilitados** (29-oct): `DisabledException` → 401. Se verifica después de la contraseña, para no revelar qué cuentas existen.
- [ ] **Corregir la migración y activar Flyway**: agregar `fullname` y alinear la tabla de roles con la entidad (`@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))` + `@Column(name = "role")`). Quitar `@Enumerated` de `Set<String> roles`.
- [x] **Limpieza** (29-oct): se borró `SecuriryBeansConfig` (nadie usaba ese bean; el hash lo hace `BCryptPasswordEncoderAdapter`), se quitaron `extractUserId` / `extractEmail` de auth, cart y order, y los `JwtProvider` de los 4 servicios usan SLF4J (`debug` para tokens expirados, `warn` para el resto). Antes: eliminar el `PasswordEncoder` duplicado (`SecuriryBeansConfig`, que además tiene un typo en el nombre, o el adaptador), borrar `JwtProvider.extractUserId` / `extractEmail` (leen claims que no existen) y reemplazar `System.err` por un logger (SLF4J). Esta limpieza aplica también al `JwtProvider` de los otros servicios.
- [x] **`LoginResponse.expiresIn`** tenía `3600` hardcodeado, pero el token dura 24 h. Ahora se calcula desde `security.jwt.expiration` (ms → s, 86400).

### 1.4 Catalog-service

- [ ] **Corregir las validaciones de los DTOs** `CreateProductRequest` / `UpdateProductRequest`: `@NotNull @Positive` para `price`, `@PositiveOrZero` para `stock`, `@NotNull` para `categoryId`, y `@Valid` también en `update`. Validar `name` en las requests de categoría.
- [ ] **Depender del puerto, no de la clase**: `ProductController` debe inyectar `ConfirmCategoryExistsUseCase`. Mejor aún, eliminar esa validación del controller, porque ya la hacen el servicio y el adaptador (hoy se valida tres veces).
- [ ] **`PUT /products/{id}` no debe crear productos**: verificar que existan antes de guardar (hoy funciona como un *upsert*).
- [ ] **No desactivar categorías con productos activos**: usar `existsByCategory`, que ya existe pero no se usa.
- [ ] **Unificar `existsById` de categoría** para que ignore las inactivas, igual que `findById`.
- [ ] **Códigos HTTP**: `201 Created` en los POST y `204 No Content` en los DELETE.
- [ ] **Permitir Swagger** (`/swagger-ui/**`, `/v3/api-docs/**`) en `SecurityConfig`.

---

## Fase 2 — Completar el flujo de negocio real (Carrito → Pedido → Pago)

Esta es la fase más importante para entender **cómo se comunican los microservicios entre sí** vía HTTP síncrono.

### 2.1 Order-service pide el carrito, no lo recibe del cliente

- [x] Cambiar `CreateOrderRequest` para que solo reciba `address` (nada de `items`/`total` desde el cliente). *Por qué:* si el precio y los productos vienen del cliente, cualquiera podría manipular el request y comprar algo a $0. El precio siempre se recalcula en el servidor.
- [x] Crear el puerto de salida `CartPort` en order-service, con un método tipo `getActiveCart(userId)`.
- [x] Implementar el adaptador HTTP de `CartPort` (llamando a `GET /cart` de cart-service, pasando el JWT del usuario para que cart-service sepa de quién es el carrito).
- [x] Reusar (o crear) `CatalogPort` en order-service para volver a consultar el precio actual de cada producto al momento de crear el pedido (nunca confiar en el precio que traiga el carrito, por si cambió).
- [x] En el caso de uso `CreateOrder`: obtener el carrito activo → validar que no esté vacío → construir los `OrderItem` con precios verificados → crear el `Order` → llamar a cart-service para **vaciar el carrito** una vez creado el pedido.

### 2.2 Endpoints de transición de estado

- [x] `POST /orders/{id}/pay` — pensado para ser llamado por payment-service cuando el pago es aprobado. Usa el método `order.markAsPaid()` que ya existe en el dominio.
- [x] `POST /orders/{id}/cancel` — usa `order.cancel()`, ya existente, que valida que el pedido no esté `PAID`.

### 2.3 Payment-service (microservicio nuevo)

- [ ] Crear el módulo `payment-service/` replicando la estructura hexagonal de los demás servicios (`domain/model`, `domain/port/in|out`, `application/service`, `adapter/in/rest`, `adapter/out/persistence`, `config`).
- [ ] Modelo de dominio `Payment` (id, orderId, amount, status `APPROVED`/`REJECTED`, transactionId, fecha).
- [ ] Caso de uso `ProcessPaymentUseCase`: simula el resultado del pago (por ejemplo, aprobar si el monto es válido, o según un flag de prueba en el request — es un mock, no una pasarela real).
- [ ] Endpoint `POST /payments/process`, protegido con JWT, conforme al contrato ya definido en `docs/openapi/payment.yaml`.
- [ ] Cuando el pago se aprueba: payment-service llama a `POST /orders/{id}/pay` en order-service (cliente HTTP, igual que hicimos con `CatalogPort`/`CartPort`).
- [ ] Persistir cada intento de pago en su propia base de datos (tabla `payments`, con su migración Flyway), aunque sea simulado — sirve para auditoría y para practicar el patrón "una DB por microservicio".
- [ ] Archivos `application.yaml`, `application-dev.yaml`, `application-test.yaml` (puerto sugerido: 8085, ya reservado en el OpenAPI).
- [ ] Definir en `payment.yaml` cómo forzar un pago rechazado (por ejemplo, un campo `simulateResult: APPROVED | REJECTED`) y las respuestas de error (400/404/409).

> Nota: varias de estas piezas ya existieron (dominio, puertos, handler de errores, migración, `application.yml`) y se perdieron. Ver la tarea de recuperación en 1.0.

---

## Fase 3 — Calidad transversal (aplica a los 5 servicios)

### 3.1 Manejo de errores

- [x] Crear un `@ControllerAdvice` (clase con `@ExceptionHandler`) en cada servicio que traduzca las excepciones a códigos HTTP correctos. Completado el 28-oct: el `GlobalExceptionHandler` de los 5 servicios extiende `ResponseEntityExceptionHandler` y todas las respuestas usan el mismo `ErrorResponse` (`status`, `error`, `message`, `timestamp`):
  - `IllegalArgumentException` → `400 Bad Request`
  - Validación (`@Valid`), JSON malformado, UUID inválido en la ruta, parámetro faltante → `400` (los resuelve `ResponseEntityExceptionHandler`, igual que `405`, `415` y `404` de rutas inexistentes)
  - `AuthenticationException` (ej. `BadCredentialsException` en login) → `401 Unauthorized`
  - `AccessDeniedException` (ej. pedido de otro usuario) → `403 Forbidden`
  - `ResourceNotFoundException` (nueva, en `domain/exception` de cada servicio) → `404 Not Found`
  - `IllegalStateException` (ej. cancelar un pedido pagado, email o categoría duplicados) → `409 Conflict`
  - En cart, order y payment: error HTTP de un servicio downstream (`RestClientResponseException`) → `502`; servicio caído o timeout (`ResourceAccessException`) → `503`. Los adaptadores traducen antes los casos esperados: catalog 404 → `ResourceNotFoundException`; en payment, order 404/409 → 404/409.
  - Cualquier otra → `500` con mensaje genérico, y ahora se **loguea** el stacktrace (antes se perdía).
  - `OrderController` ya no arma 403/404 vacíos a mano: lanza las excepciones y el handler responde con cuerpo.
- [ ] Pendientes de errores: `OrderService` responde 400 cuando el carrito está vacío (sería más claro 409 o 422); si cart-service rechaza el token al crear un pedido, order responde 502 en vez de propagar el 401.
  - *Por qué:* sin esto, Spring Boot devuelve una página de error genérica con stacktrace, lo cual es una mala práctica de API (expone detalles internos) y obliga al frontend a adivinar qué pasó.

### 3.2 Configuración y secretos

- [ ] Mover todo secreto (JWT secret, credenciales de DB, password del super admin) a variables de entorno, usando la sintaxis `${VARIABLE:valor-por-defecto}` en los YAML, y crear un perfil `application-prod.yml` por servicio.
- [ ] Usar **el mismo valor de `JWT_SECRET`** en los 5 servicios en producción (se define una vez como variable de entorno y se repite en cada servicio de Render). *Por qué:* auth-service firma el token con ese secreto; si otro servicio usa un secreto distinto para *verificar* la firma, el token siempre se rechaza aunque sea válido.
- [ ] Agregar `spring-boot-starter-actuator` a los 5 servicios y exponer `/actuator/health`. (La dependencia ya está en los 5; falta exponer `health` en los YAML de auth, catalog, cart y order, y permitir `/actuator/health` en cada `SecurityConfig`.) *Por qué:* es el endpoint que Render (y cualquier orquestador) usa para saber si tu servicio sigue vivo.
- [ ] Hacer que el puerto del servidor lea la variable `PORT` (`server.port=${PORT:8081}`), porque Render asigna el puerto dinámicamente vía esa variable de entorno.
- [ ] Usar **un mismo nombre de propiedad** en los 5 servicios (`security.jwt.secret`, `security.jwt.expiration`). Hoy conviven `security.jwt.*`, `spring.jwt.*` y `spring.security.jwt.*`; las dos últimas no son propiedades de Spring y solo generan confusión.
- [ ] **Alinear versiones**: misma versión de Spring Boot en todos los `pom.xml`, springdoc `2.8.x` (compatible con Boot 3.5) en los 5 servicios, y la misma versión de jjwt en sus 3 artefactos. *Por qué:* las versiones mezcladas producen errores en tiempo de ejecución (`NoSuchMethodError`) que no aparecen al compilar. (Opcional: un `pom.xml` padre en la raíz para declararlas una sola vez.)

### 3.3 CORS

- [ ] Replicar en cart-service, catalog-service, order-service y payment-service la misma configuración CORS que ya existe en auth-service, parametrizando los orígenes permitidos por variable de entorno (para poder apuntar al dominio real del frontend).

### 3.4 Migraciones de base de datos

- [ ] Agregar migraciones Flyway (`db/migration/V1__...sql`) a catalog-service y order-service (auth-service y cart-service tienen una, pero rota: ver 1.2 y 1.3). *Por qué:* usar `ddl-auto: update` está bien para prototipar, pero en producción el esquema debe crearse de forma explícita y versionada.
- [ ] Agregar `flyway-core` + `flyway-database-postgresql` donde falten (catalog-service tiene `flyway.enabled: true`, pero no la dependencia) y pasar a `ddl-auto: validate` en dev/prod. Así Hibernate avisa si la entidad y la migración no coinciden, que es justo el problema actual de auth-service.

### 3.5 Higiene del repositorio

- [ ] Borrar los `.git` anidados y vacíos de `auth-service/`, `cart-service/`, `catalog-service/` y `order-service/` (los dejó Spring Initializr; el repo real es el de la raíz).
- [ ] Limpiar el `.gitignore` raíz: quitar `TASKS.md` (ya está trackeado) y `README-TAREAS.md` (no existe), y agregar `target/`, `.idea/` y `HELP.md` a nivel raíz.
- [ ] Commitear temprano y seguido, en especial los servicios nuevos (lo que le pasó a payment-service).

---

## Fase 4 — Testing

Con ~25 tests en todo el repo, casi todos "happy path" sobre controllers, falta cobertura real en varias capas. Desde el 28-oct pasan todos los tests existentes de los 5 servicios (ver la tabla de la Fase 0).

- [x] **Hacer pasar los tests existentes primero** (28-oct):
  - Los `@WebMvcTest` de catalog y cart importan su `SecurityConfig` real y mockean `JwtProvider`, así que también prueban las reglas de acceso (se agregaron casos 403 para CLIENT y sin autenticación). Se agregó `spring-security-test` a catalog.
  - `CartControllerTest` reescrito: autentica con `user(userId)` (el controller lee el userId de `Authentication`, no de un atributo del request), ya no mockea el DTO `AddItemRequest` y el test de actualización usa `PUT /cart/items/{productId}`.
  - `CatalogServiceApplicationTests` usa `@ActiveProfiles("test")` (H2).
- [x] **`contextLoads` de cart sin Postgres**: H2 en scope test y `@ActiveProfiles("test")`. (Order y auth todavía dependen de Postgres local.)

- [ ] **Tests de dominio puro** (clases `Order`, `Cart`, `User`, `Product`, sin Spring): probar transiciones de estado inválidas (pagar un pedido cancelado, agregar cantidad negativa, etc.) y las invariantes del constructor.
- [ ] **Tests de casos de uso** con los puertos de salida mockeados (Mockito). `CartApplicationServiceTest` ya existe (28-oct); faltan los de catalog (parcial), auth y payment.
- [ ] **Tests de seguridad**: para cada servicio, verificar 401 sin token, 403 con rol insuficiente, y que un CLIENT no pueda llegar a endpoints de ADMIN.
- [ ] **Tests de integración entre servicios**: simular las respuestas de catalog-service/cart-service/order-service con WireMock o `MockWebServer`, para probar el flujo completo sin levantar los 5 servicios reales.
- [ ] **Tests de persistencia** (`@DataJpaTest`) para los mappers JPA↔dominio, al menos en catalog-service y order-service.
- [ ] Agregar el plugin **JaCoCo** (`jacoco-maven-plugin`) en cada `pom.xml` para medir cobertura, y definir un umbral mínimo razonable (ej. 70% en `domain`/`application`).

---

## Fase 5 — Dockerización

- [ ] Crear `Dockerfile` (build multi-stage: Maven compila el jar, y una imagen final liviana `eclipse-temurin:21-jre` solo lo ejecuta) para cart-service, catalog-service, order-service y payment-service. Ojo: `auth-service/docker/Dockerfile` **no** es multi-stage (copia un jar ya compilado) y usa JRE 21 mientras el `pom.xml` compila con Java 17. Convertirlo también y alinear la versión de Java (17 o 21, pero la misma en el pom y en la imagen).
- [ ] Corregir `auth-service/docker-compose.yml` (o reemplazarlo por el compose raíz): la variable `JWT_SECRET` no la lee nadie, las credenciales de la base (`auth_user` / `auth_pass`) no coinciden con las del YAML (`postgres` / `postgres`), y el datasource apunta a `localhost`, que dentro del contenedor no es la base de datos.
- [ ] Crear un `docker-compose.yml` en la **raíz del repo** que levante los 5 microservicios + sus bases Postgres, con la red y las variables de entorno necesarias para que se hablen entre sí localmente. *Por qué:* hoy solo existe un compose aislado dentro de auth-service; para probar el flujo completo (carrito → pedido → pago) necesitas todo el stack corriendo a la vez.

---

## Fase 6 — CI/CD con GitHub Actions

- [ ] Crear `.github/workflows/ci.yml` con un job por servicio (matrix build) que corra `mvn -B verify` (compila + ejecuta tests) en cada push/PR a `develop`/`main`.
- [ ] Usar `paths` en el trigger del workflow para que, si solo cambia `catalog-service/`, no se reconstruyan los otros 4 servicios (ahorra tiempo de CI).
- [ ] Configurar cache de dependencias Maven con `actions/setup-java` (`cache: maven`) para acelerar los builds.
- [ ] (Opcional) Job que construya la imagen Docker de cada servicio y la publique en GitHub Container Registry (`ghcr.io`) cuando se hace push a `main`.
- [ ] *Nota sobre CD:* Render puede auto-desplegar al detectar un push a la rama configurada, sin que GitHub Actions tenga que "empujar" el deploy — el rol de Actions acá es simplemente garantizar que el código en `main` compila y pasa los tests antes de que Render lo tome.

---

## Fase 7 — Despliegue en Render

- [ ] Crear `render.yaml` (Render Blueprint) en la raíz, definiendo 5 web services (uno por microservicio, runtime Docker) + las bases Postgres gestionadas que necesiten.
- [ ] Configurar las variables de entorno de cada servicio en Render: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL` (Render la inyecta automáticamente al conectar una DB), `JWT_SECRET` (mismo valor en los 5), `INTERNAL_SERVICE_TOKEN` (mismo valor en order-service y payment-service; un valor largo y aleatorio, nunca el de desarrollo), y las URLs internas para que cada servicio encuentre a los demás (ej. `CATALOG_SERVICE_URL`).
- [ ] Verificar que Render pueda usar `/actuator/health` como health check de cada servicio.
- [ ] Confirmar que cada servicio arranca leyendo `PORT` desde el entorno (ver tarea de la Fase 3.2).

---

## Fase 8 — Documentación final

- [ ] Agregar `cart-service/ReadMe.md`, `order-service/ReadMe.md` y `payment-service/ReadMe.md` (auth-service y catalog-service ya tienen el suyo).
- [ ] **Corregir `auth-service/ReadMe.md`**:
  - el ejemplo de registro manda `"roles": ["SUPER_ADMIN"]` (contradice la regla 1.1) y no incluye `fullname`, que es obligatorio;
  - la respuesta del login es `accessToken` / `tokenType` / `expiresIn`, no `token`;
  - documentar `/admin/users` y el SUPER_ADMIN inicial (`admin.*`);
  - aclarar que `contextLoads` necesita Postgres.
- [ ] **Corregir `catalog-service/ReadMe.md`**:
  - las rutas reales son `/categories` y `/products`, no `/catalog/...`;
  - hay JSON inválido (coma sobrante);
  - la sección Docker menciona un `docker/` que no existe;
  - la contraseña de ejemplo (`postgresPassword`) no coincide con el YAML;
  - el "✅ Tests implementados" no es cierto hoy;
  - documentar la seguridad (GET público, escritura solo ADMIN/SUPER_ADMIN), los filtros `?categoryId=` y `?name=`, y los endpoints `GET /products/{id}`, PUT y DELETE.
- [ ] **Alinear los OpenAPI con el código** (hoy ninguno coincide):
  - `auth.yaml`: dice `GET /users` y `PUT /users/{id}/role` con body; el código es `/admin/users` y POST con query param. También difieren `fullName` / `fullname` y la forma de `UserResponse` (`role` / `fullName` en el spec, `roles[]` / `enabled` en el código). Sacar `SUPER_ADMIN` del enum de `UpdateRoleRequest` y borrar el schema huérfano `CreateUserRequest`.
  - `cart.yaml`: `CartResponse` no tiene `id`, y el código responde `item` en vez de `items`.
  - `order.yaml`: ya describe el diseño objetivo (`address` solo). Falta agregar `POST /orders/{id}/pay`, `POST /orders/{id}/cancel` y las respuestas 401/403/404/409.
  - Actualizar `catalog.yaml` si cambian los DTOs al implementar la Fase 2.
- [ ] **Actualizar `docs/busines-rules.md`** con las reglas que hoy no están escritas:
  - quién dispara el pago y cómo payment-service avisa a order-service;
  - que el precio siempre se recalcula en el servidor;
  - que el carrito se vacía al crear el pedido;
  - que un usuario deshabilitado no puede hacer login;
  - si un pago rechazado se puede reintentar.
  
  Resolver también la contradicción con el README ("ADMIN: gestión de usuarios" frente a la regla 6.2, donde ADMIN solo lista). (Opcional: renombrar el archivo a `business-rules.md`.)
- [ ] **Completar `docs/architecture/ReadMe.md`**: agregar una tabla de servicios/puertos/bases de datos, las dependencias síncronas entre servicios (cart→catalog, order→cart/catalog, payment→order) y un diagrama de secuencia del checkout.
- [ ] Actualizar el `ReadMe.md` raíz:
  - quitar Kubernetes (no está en el alcance actual) o moverlo a "futuras mejoras";
  - agregar prerequisitos, una tabla de puertos y enlaces a `docs/` y a este backlog;
  - documentar cómo levantar todo el stack local (`docker-compose up`) y qué variables de entorno configurar en Render;
  - quitar el "🚧 En desarrollo" cuando el MVP esté cerrado.

---

## Orden recomendado de implementación

0. **Fase 1.0** (bloqueantes) — recuperar payment-service, hacer compilar order-service y unificar el JWT. Sin esto, nada de lo demás se puede probar de punta a punta.
1. **Fase 1** (bugs) — son rápidas y desbloquean funcionalidad que ya "aparenta" estar hecha. Dentro de esta fase, conviene hacer primero "Hacer pasar los tests existentes" (Fase 4), para tener una red de seguridad mientras corriges.
2. **Fase 2** (carrito → pedido → pago) — el corazón del negocio; ahí vas a aprender la comunicación entre microservicios vía HTTP.
3. **Fase 3** (calidad transversal) y **Fase 4** (tests) en paralelo a la Fase 2, no al final — es más fácil testear cada pieza mientras la construís que reconstruir tests para todo junto después.
4. **Fase 5** (Docker) — una vez el código funciona de verdad.
5. **Fase 6** (CI) — automatiza lo que ya podés correr manualmente (`mvn verify`, `docker build`).
6. **Fase 7** (Render) — último paso, depende de todo lo anterior.
7. **Fase 8** (documentación) — se va actualizando en paralelo, no es un paso separado al final.
