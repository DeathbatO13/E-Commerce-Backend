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

- [ ] **`payment-service` no existe todavía** — solo hay un contrato OpenAPI (`docs/openapi/payment.yaml`) pero ningún código. Sin este servicio, un pedido nunca puede pasar de `CREATED` a `PAID`.
- [ ] **`CatalogPort` en cart-service nunca se inyecta** (`cart-service/.../CartApplicationService.java`): es un campo de clase sin constructor ni `@Autowired`, siempre queda `null`. Cualquier intento de agregar un producto al carrito revienta con `NullPointerException`. Tampoco existe ninguna clase que implemente ese puerto (falta el adaptador HTTP hacia catalog-service).
- [ ] **Order-service no crea pedidos desde el carrito**: hoy el cliente manda `items` y `total` directamente en el request, lo cual contradice la regla de negocio "un pedido solo puede crearse a partir de un carrito activo" y permitiría a cualquier usuario inventar precios.
- [x] **Paquetes duplicados en order-service**: resuelto (se eliminó `order_service.persistence.*`, conservando únicamente `adapter.out.persistence.*`).
- [ ] **Typo `"ROLE_ADMMIN"`** en `OrderController` — hace que un ADMIN real nunca sea reconocido como admin al listar pedidos.
- [ ] **Ruta duplicada `/admin/admin/...`** en `UserController` de auth-service, por sumar el path del método al del `@RequestMapping` de clase.
- [ ] **Listado de usuarios restringido solo a SUPER_ADMIN**, cuando la regla de negocio dice que ADMIN también debería poder listarlos.
- [ ] **No hay manejo global de errores** (`@ControllerAdvice`) en ningún servicio — los errores de dominio se devuelven como 500 genérico en vez de 400/404/409 con un body claro.
- [ ] **Sin Dockerfile** en cart-service, catalog-service y order-service (solo existe en auth-service). Sin `docker-compose.yml` raíz. Sin `render.yaml`. Sin `.github/workflows`.
- [ ] **Secrets hardcodeados** (contraseñas de DB, JWT secret, password del super admin) directamente en los `application*.yml`, en vez de venir de variables de entorno — inviable para producción/Render.
- [ ] **CORS solo configurado en auth-service** — el frontend no podrá llamar a los demás servicios desde el navegador.

---

## Fase 1 — Corregir bugs funcionales existentes

Objetivo: que lo que "ya está hecho" funcione de verdad antes de sumar código nuevo.

### 1.1 Order-service

- [x] **Eliminar el paquete de persistencia duplicado.** Quedarte solo con `adapter.out.persistence.*` (es el que respeta la convención hexagonal del resto del proyecto) y borrar `order_service.persistence.*`.
- [ ] **Corregir `"ROLE_ADMMIN"` → `"ROLE_ADMIN"`** en `OrderController`. *Por qué:* es la condición que decide si un usuario ve todos los pedidos (admin) o solo los suyos; con el typo, esa rama nunca se ejecuta.
- [ ] **Agregar validación de "ownership" en `GET /orders/{id}`**: hoy cualquier usuario autenticado puede ver el pedido de otro usuario solo sabiendo el ID. Hay que verificar que `order.getUserId()` sea igual al usuario autenticado, o que el usuario sea ADMIN/SUPER_ADMIN.

### 1.2 Cart-service

- [ ] **Implementar el adaptador HTTP para `CatalogPort`**: una clase en `adapter/out` que llame por HTTP a `GET /products/{id}` de catalog-service (usando `RestClient` de Spring, que es el cliente HTTP moderno recomendado desde Spring 6).
- [ ] **Inyectar `CatalogPort` correctamente** en `CartApplicationService` (por constructor, como ya se hace con `CartRepository`). *Por qué:* Spring solo puede inyectar dependencias que reciba explícitamente (constructor o `@Autowired`); un campo declarado "a mano" sin eso queda siempre `null`.
- [ ] **Validar que el producto exista y esté activo** antes de agregarlo al carrito (regla de negocio: "no se pueden agregar productos inexistentes o deshabilitados").

### 1.3 Auth-service

- [ ] **Corregir la ruta duplicada**: dejar `@PostMapping("/users/{id}/role")` (sin repetir `/admin`, porque ya está en el `@RequestMapping` de la clase).
- [ ] **Permitir que ADMIN también liste usuarios**: mover el `@PreAuthorize("hasRole('SUPER_ADMIN')")` del nivel de clase al método `changeRole` únicamente, dejando `listUsers` accesible para ADMIN y SUPER_ADMIN.

---

## Fase 2 — Completar el flujo de negocio real (Carrito → Pedido → Pago)

Esta es la fase más importante para entender **cómo se comunican los microservicios entre sí** vía HTTP síncrono.

### 2.1 Order-service pide el carrito, no lo recibe del cliente

- [ ] Cambiar `CreateOrderRequest` para que solo reciba `address` (nada de `items`/`total` desde el cliente). *Por qué:* si el precio y los productos vienen del cliente, cualquiera podría manipular el request y comprar algo a $0. El precio siempre se recalcula en el servidor.
- [ ] Crear el puerto de salida `CartPort` en order-service, con un método tipo `getActiveCart(userId)`.
- [ ] Implementar el adaptador HTTP de `CartPort` (llamando a `GET /cart` de cart-service, pasando el JWT del usuario para que cart-service sepa de quién es el carrito).
- [ ] Reusar (o crear) `CatalogPort` en order-service para volver a consultar el precio actual de cada producto al momento de crear el pedido (nunca confiar en el precio que traiga el carrito, por si cambió).
- [ ] En el caso de uso `CreateOrder`: obtener el carrito activo → validar que no esté vacío → construir los `OrderItem` con precios verificados → crear el `Order` → llamar a cart-service para **vaciar el carrito** una vez creado el pedido.

### 2.2 Endpoints de transición de estado

- [ ] `POST /orders/{id}/pay` — pensado para ser llamado por payment-service cuando el pago es aprobado. Usa el método `order.markAsPaid()` que ya existe en el dominio.
- [ ] `POST /orders/{id}/cancel` — usa `order.cancel()`, ya existente, que valida que el pedido no esté `PAID`.

### 2.3 Payment-service (microservicio nuevo)

- [ ] Crear el módulo `payment-service/` replicando la estructura hexagonal de los demás servicios (`domain/model`, `domain/port/in|out`, `application/service`, `adapter/in/rest`, `adapter/out/persistence`, `config`).
- [ ] Modelo de dominio `Payment` (id, orderId, amount, status `APPROVED`/`REJECTED`, transactionId, fecha).
- [ ] Caso de uso `ProcessPaymentUseCase`: simula el resultado del pago (por ejemplo, aprobar si el monto es válido, o según un flag de prueba en el request — es un mock, no una pasarela real).
- [ ] Endpoint `POST /payments/process`, protegido con JWT, conforme al contrato ya definido en `docs/openapi/payment.yaml`.
- [ ] Cuando el pago se aprueba: payment-service llama a `POST /orders/{id}/pay` en order-service (cliente HTTP, igual que hicimos con `CatalogPort`/`CartPort`).
- [ ] Persistir cada intento de pago en su propia base de datos (tabla `payments`, con su migración Flyway), aunque sea simulado — sirve para auditoría y para practicar el patrón "una DB por microservicio".
- [ ] Archivos `application.yaml`, `application-dev.yaml`, `application-test.yaml` (puerto sugerido: 8085, ya reservado en el OpenAPI).

---

## Fase 3 — Calidad transversal (aplica a los 5 servicios)

### 3.1 Manejo de errores

- [ ] Crear un `@ControllerAdvice` (clase con `@ExceptionHandler`) en cada servicio que traduzca las excepciones de dominio a códigos HTTP correctos:
  - `IllegalArgumentException` → `400 Bad Request`
  - `IllegalStateException` (ej. cancelar un pedido pagado) → `409 Conflict`
  - "no encontrado" → `404 Not Found`
  - *Por qué:* sin esto, Spring Boot devuelve una página de error genérica con stacktrace, lo cual es una mala práctica de API (expone detalles internos) y obliga al frontend a adivinar qué pasó.

### 3.2 Configuración y secretos

- [ ] Mover todo secreto (JWT secret, credenciales de DB, password del super admin) a variables de entorno, usando la sintaxis `${VARIABLE:valor-por-defecto}` en los YAML, y crear un perfil `application-prod.yml` por servicio.
- [ ] Usar **el mismo valor de `JWT_SECRET`** en los 5 servicios en producción (se define una vez como variable de entorno y se repite en cada servicio de Render). *Por qué:* auth-service firma el token con ese secreto; si otro servicio usa un secreto distinto para *verificar* la firma, el token siempre se rechaza aunque sea válido.
- [ ] Agregar `spring-boot-starter-actuator` a los 5 servicios y exponer `/actuator/health`. *Por qué:* es el endpoint que Render (y cualquier orquestador) usa para saber si tu servicio sigue vivo.
- [ ] Hacer que el puerto del servidor lea la variable `PORT` (`server.port=${PORT:8081}`), porque Render asigna el puerto dinámicamente vía esa variable de entorno.

### 3.3 CORS

- [ ] Replicar en cart-service, catalog-service, order-service y payment-service la misma configuración CORS que ya existe en auth-service, parametrizando los orígenes permitidos por variable de entorno (para poder apuntar al dominio real del frontend).

### 3.4 Migraciones de base de datos

- [ ] Agregar migraciones Flyway (`db/migration/V1__...sql`) a catalog-service y order-service (hoy solo auth-service y cart-service las tienen). *Por qué:* usar `ddl-auto: update` está bien para prototipar, pero en producción el esquema debe crearse de forma explícita y versionada.

---

## Fase 4 — Testing

Con ~25 tests en todo el repo, casi todos "happy path" sobre controllers, falta cobertura real en varias capas.

- [ ] **Tests de dominio puro** (clases `Order`, `Cart`, `User`, `Product`, sin Spring): probar transiciones de estado inválidas (pagar un pedido cancelado, agregar cantidad negativa, etc.) y las invariantes del constructor.
- [ ] **Tests de casos de uso** con los puertos de salida mockeados (Mockito): `CartApplicationServiceTest` no existe hoy y es el más urgente, porque ahí vive el bug del `CatalogPort` nulo.
- [ ] **Tests de seguridad**: para cada servicio, verificar 401 sin token, 403 con rol insuficiente, y que un CLIENT no pueda llegar a endpoints de ADMIN.
- [ ] **Tests de integración entre servicios**: simular las respuestas de catalog-service/cart-service/order-service con WireMock o `MockWebServer`, para probar el flujo completo sin levantar los 5 servicios reales.
- [ ] **Tests de persistencia** (`@DataJpaTest`) para los mappers JPA↔dominio, al menos en catalog-service y order-service.
- [ ] Agregar el plugin **JaCoCo** (`jacoco-maven-plugin`) en cada `pom.xml` para medir cobertura, y definir un umbral mínimo razonable (ej. 70% en `domain`/`application`).

---

## Fase 5 — Dockerización

- [ ] Crear `Dockerfile` (build multi-stage: Maven compila el jar, y una imagen final liviana `eclipse-temurin:21-jre` solo lo ejecuta) para cart-service, catalog-service, order-service y payment-service, siguiendo el mismo patrón que `auth-service/docker/Dockerfile`.
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
- [ ] Configurar las variables de entorno de cada servicio en Render: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL` (Render la inyecta automáticamente al conectar una DB), `JWT_SECRET` (mismo valor en los 5), y las URLs internas para que cada servicio encuentre a los demás (ej. `CATALOG_SERVICE_URL`).
- [ ] Verificar que Render pueda usar `/actuator/health` como health check de cada servicio.
- [ ] Confirmar que cada servicio arranca leyendo `PORT` desde el entorno (ver tarea de la Fase 3.2).

---

## Fase 8 — Documentación final

- [ ] Agregar `payment-service/ReadMe.md` y `order-service/ReadMe.md` (falta este último; auth-service y catalog-service ya tienen el suyo).
- [ ] Actualizar los OpenAPI (`docs/openapi/order.yaml`, `cart.yaml`, `catalog.yaml`) si cambian los DTOs al implementar la Fase 2.
- [ ] Actualizar `docs/busines-rules.md` si alguna decisión de implementación agrega una regla nueva (por ejemplo, cómo se comunica payment-service con order-service).
- [ ] Actualizar el `ReadMe.md` raíz: quitar el "🚧 En desarrollo" cuando el MVP esté cerrado, y documentar cómo levantar todo el stack local (`docker-compose up`) y qué variables de entorno configurar en Render.

---

## Orden recomendado de implementación

1. **Fase 1** (bugs) — son rápidas y desbloquean funcionalidad que ya "aparenta" estar hecha.
2. **Fase 2** (carrito → pedido → pago) — el corazón del negocio; ahí vas a aprender la comunicación entre microservicios vía HTTP.
3. **Fase 3** (calidad transversal) y **Fase 4** (tests) en paralelo a la Fase 2, no al final — es más fácil testear cada pieza mientras la construís que reconstruir tests para todo junto después.
4. **Fase 5** (Docker) — una vez el código funciona de verdad.
5. **Fase 6** (CI) — automatiza lo que ya podés correr manualmente (`mvn verify`, `docker build`).
6. **Fase 7** (Render) — último paso, depende de todo lo anterior.
7. **Fase 8** (documentación) — se va actualizando en paralelo, no es un paso separado al final.
