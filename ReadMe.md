# E-commerce Platform – MVP (Backend)

## Visión General

Este proyecto es una **plataforma de E-commerce** diseñada con un enfoque **moderno, escalable y orientado a buenas prácticas de arquitectura**.

El objetivo principal del MVP es **demostrar dominio técnico y criterio arquitectónico**, aplicando:

- **Backend:** Spring Boot 3.5 (Java 17), Arquitectura Hexagonal, Microservicios
- **Frontend:** React con Microfrontends (repositorio aparte)
- **Seguridad:** JWT y control de roles
- **Persistencia:** PostgreSQL + Flyway
- **Calidad:** JUnit, Mockito, JaCoCo (80% mínimo en dominio y aplicación)
- **Infraestructura:** Docker, Docker Compose, GitHub Actions y Render
- **Documentación:** OpenAPI
- **Control de versiones:** Git Flow

Este proyecto está pensado como **portafolio profesional y ejercicio técnico**, no como un producto comercial en producción.

---

## Alcance del MVP

### Funcionalidades Incluidas

#### Autenticación y Usuarios
- Registro de usuarios
- Inicio de sesión con JWT
- Gestión de roles
- Roles definidos:
  - **SUPER_ADMIN**: usuario raíz del sistema, creado por configuración
  - **ADMIN**: gestión del catálogo y de los pedidos
  - **CLIENT**: usuario final

#### Catálogo
- CRUD de productos (**ADMIN**)
- CRUD de categorías (**ADMIN**)
- Listado público de productos
- Filtro de productos por categoría y búsqueda por nombre

#### Carrito de Compras
- Un carrito activo por usuario
- Agregar productos al carrito
- Modificar cantidades
- Eliminar productos
- Vaciar carrito

#### Pedidos
- Creación de pedidos a partir del carrito, con precios recalculados en el servidor
- Estados del pedido:
  - `CREATED`
  - `PAID`
  - `CANCELLED`
- Listado de pedidos por usuario
- Listado global de pedidos (**ADMIN**)

#### Pagos (Mock)
- Simulación de pagos
- Resultado de pago controlado (aprobado / rechazado)
- Cambio de estado del pedido a `PAID`

#### Panel de Administración
- Gestión de productos
- Gestión de categorías
- Visualización de pedidos
- Listado de usuarios (**ADMIN** y **SUPER_ADMIN**)
- Asignación y revocación del rol **ADMIN** (solo **SUPER_ADMIN**)

---

## Funcionalidades Excluidas del MVP

Las siguientes funcionalidades **no forman parte del MVP** y se consideran futuras mejoras:

- Integración con pasarelas de pago reales
- Promociones, cupones y descuentos
- Gestión avanzada de inventario (hoy los pedidos no descuentan stock)
- Envíos y logística
- Notificaciones (email, SMS)
- Mensajería asíncrona (Kafka, RabbitMQ)
- Tests end-to-end y contract testing
- Despliegue en Kubernetes

---

## Arquitectura General

### Microservicios

| Servicio | Puerto | Responsabilidad | Documentación |
|---|---|---|---|
| auth-service | 8081 | Autenticación, emisión de JWT y usuarios | [ReadMe](auth-service/ReadMe.md) · [OpenAPI](docs/openapi/auth.yaml) |
| catalog-service | 8082 | Productos y categorías (fuente del precio) | [ReadMe](catalog-service/ReadMe.md) · [OpenAPI](docs/openapi/catalog.yaml) |
| cart-service | 8083 | Carrito por usuario | [ReadMe](cart-service/ReadMe.md) · [OpenAPI](docs/openapi/cart.yaml) |
| order-service | 8084 | Pedidos a partir del carrito | [ReadMe](order-service/ReadMe.md) · [OpenAPI](docs/openapi/order.yaml) |
| payment-service | 8085 | Simulación de pagos | [ReadMe](payment-service/ReadMe.md) · [OpenAPI](docs/openapi/payment.yaml) |

Cada microservicio:
- Implementa **arquitectura hexagonal**
- Tiene **base de datos independiente** (esquema propio en Render)
- Expone **APIs REST documentadas con OpenAPI**

Más detalle en:
- [Arquitectura](docs/architecture/ReadMe.md): diagramas C4, dependencias entre servicios y secuencia del checkout.
- [Reglas de negocio](docs/busines-rules.md).
- [Backlog de tareas](TASKS.md).

### Microfrontends

| Microfrontend | Descripción |
|--------------|-------------|
| Auth App | Login y registro |
| Catalog App | Visualización del catálogo |
| Cart App | Gestión del carrito |
| Orders App | Historial de pedidos |
| Admin App | Panel de administración |

---

## Reglas Clave del Sistema

- Todo usuario nuevo se registra como **CLIENT**
- Existe un único **SUPER_ADMIN**, creado por configuración inicial
- Solo el **SUPER_ADMIN** puede asignar o revocar el rol **ADMIN**
- Cada usuario tiene un único carrito activo
- Los pedidos solo pueden crearse a partir del carrito
- El precio siempre lo calcula el servidor a partir del catálogo
- Solo payment-service puede marcar un pedido como pagado

---

## Cómo ejecutarlo

### Prerrequisitos

- **Docker** con Docker Compose (para levantar todo el stack)
- **Java 17** si se quiere correr un servicio suelto o los tests (cada servicio trae su Maven Wrapper `./mvnw`)

### Stack completo con Docker

```bash
cp .env.example .env
```

Editar `.env` (secreto JWT, token interno, credenciales del SUPER_ADMIN) y levantar:

```bash
docker compose up --build
```

Levanta un Postgres 16 (puerto **5433** del host, con las cinco bases) y los cinco servicios en los puertos 8081–8085 con el perfil `prod`.
Las variables de `.env` son:

| Variable | Uso |
|---|---|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Credenciales de Postgres |
| `JWT_SECRET` | Firma de los JWT; mismo valor en los 5 servicios (mínimo 32 bytes) |
| `INTERNAL_SERVICE_TOKEN` | Token entre payment-service y order-service |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | SUPER_ADMIN que crea auth-service en el primer arranque |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend, separados por coma |

Para probar: login como SUPER_ADMIN, crear una categoría y un producto, registrar un cliente, agregar al carrito, crear el pedido y pagarlo. Los ejemplos de cada paso están en los README de cada servicio.

### Un servicio suelto

```bash
cd catalog-service
./mvnw spring-boot:run
```

Usa el perfil `dev`: espera un Postgres en `localhost:5432` con la base del servicio ya creada (por ejemplo `catalog_db`).

### Tests

```bash
cd order-service
./mvnw verify
```

Los tests usan H2 y no necesitan Postgres ni los otros servicios. `verify` además exige un 80% de cobertura de líneas en `domain` y `application`.

---

## CI/CD

- **GitHub Actions** ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)): en cada push y pull request corre `mvn verify` solo en los servicios que cambiaron. En `main` además construye las imágenes Docker y las publica en GitHub Container Registry. El job `ci` resume el resultado y es el que conviene marcar como check obligatorio.
- **Render** ([`render.yaml`](render.yaml)): Blueprint con los cinco servicios Docker y un Postgres gratuito (un esquema por servicio). Al crear el Blueprint, Render pide los valores marcados como `sync: false`:

| Variable | Dónde |
|---|---|
| `CORS_ALLOWED_ORIGINS` | grupo `ecommerce-shared` (URL del frontend) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | auth-service |

`JWT_SECRET` e `INTERNAL_SERVICE_TOKEN` los genera Render; la conexión a la base y las URLs internas entre servicios se completan solas.
Limitaciones del plan gratuito (servicios que se duermen, base que vence a los 30 días) en [TASKS.md](TASKS.md), Fase 7.

---

## Organización de Repositorios

El proyecto se divide en dos repositorios principales:

### Backend
Repositorio: `ecommerce-backend` (este)

- Contiene todos los microservicios
- Enfoque monorepo
- Despliegue independiente por servicio

### Frontend
Repositorio: `ecommerce-frontend`

- Contiene el Shell App y los microfrontends
- Implementado con React y Module Federation

Esta separación permite:
- Independencia de despliegue
- Claridad de responsabilidades
- Mejor mantenibilidad

---

## Estado del Proyecto

✅ MVP del backend completo: los cinco servicios implementados, con tests, Docker, CI y Blueprint de Render.
Lo que queda (y las mejoras posibles) está en [TASKS.md](TASKS.md).
