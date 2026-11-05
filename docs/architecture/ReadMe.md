# Arquitectura del Sistema – E-commerce

## 📌 Introducción

Este documento describe la **arquitectura de alto nivel** del proyecto **E-commerce**, diseñada como parte del MVP con el objetivo de demostrar buenas prácticas de diseño de software, separación de responsabilidades y escalabilidad.

La arquitectura se apoya en:

* **Microservicios** para el backend
* **Microfrontends** para el frontend
* **Arquitectura Hexagonal** en cada microservicio
* Comunicación mediante **APIs REST** documentadas con OpenAPI

Para su representación se utiliza el **modelo C4**, enfocado en facilitar la comprensión del sistema a diferentes niveles.

---

## 🧩 Modelo C4

En esta fase del proyecto se cubren los dos primeros niveles del modelo C4:

* **Context Diagram (Nivel 1)**
* **Container Diagram (Nivel 2)**

Los niveles de Component y Code se abordarán en etapas posteriores del desarrollo.

---

## 🌍 Diagrama C4 – Context

### Propósito

El diagrama de contexto muestra el sistema como una **caja negra**, identificando:

* Los tipos de usuarios que interactúan con la plataforma
* El alcance funcional general del sistema
* La ausencia de dependencias externas reales (pagos simulados)

### Actores

* **Cliente**: Usuario final que navega el catálogo, gestiona su carrito y realiza pedidos.
* **Administrador**: Usuario encargado de la gestión de productos, categorías y pedidos.
* **Super Administrador**: Usuario con control total del sistema, incluyendo la gestión de usuarios y roles.

### Sistema

* **E-commerce Platform**: Plataforma central que provee funcionalidades de compra en línea y administración.

### Diagrama

![C4 Context Diagram](./c4-context.png)

---

## 🧱 Diagrama C4 – Container

### Propósito

El diagrama de contenedores describe cómo el sistema se divide internamente en:

* Aplicaciones frontend
* Microservicios backend
* Bases de datos
* Mecanismos de comunicación

Este nivel permite entender **responsabilidades técnicas** sin entrar aún en detalles de implementación.

---

### Frontend

El frontend está basado en una arquitectura de **microfrontends**, orquestados por una aplicación contenedora (Shell).

Microfrontends incluidos:

* **Auth App**: Autenticación y gestión de sesión
* **Catalog App**: Visualización de productos y categorías
* **Cart App**: Gestión del carrito de compras
* **Orders App**: Consulta de pedidos
* **Admin App**: Gestión administrativa del sistema

---

### Backend

El backend está compuesto por **microservicios independientes**, cada uno implementando arquitectura hexagonal y siendo dueño de su propio dominio y base de datos.

Microservicios:

* **Auth/User Service**
  Gestión de usuarios, roles y autenticación basada en JWT.

* **Catalog Service**
  Gestión de productos y categorías.

* **Cart Service**
  Manejo del carrito de compras por usuario.

* **Order Service**
  Creación y seguimiento de pedidos.

* **Payment Service (Mock)**
  Simulación del proceso de pago sin integración con pasarelas externas.

---

### Persistencia

Cada microservicio cuenta con su **propia base de datos**, garantizando:

* Bajo acoplamiento
* Independencia de despliegue
* Aislamiento de datos

---

### Comunicación

* El frontend se comunica con los microservicios mediante **APIs REST**.
* La autenticación se basa en **JWT**, emitidos por el Auth Service.
* La comunicación entre servicios es síncrona vía HTTP (en el MVP).

---

### Diagrama

![C4 Container Diagram](./c4-container.png)

---

## 🗺️ Servicios, puertos y bases de datos

| Servicio | Puerto | Base local / Docker | Esquema en Render | README | Contrato |
|---|---|---|---|---|---|
| auth-service | 8081 | `auth_db` | `auth` | [ReadMe](../../auth-service/ReadMe.md) | [auth.yaml](../openapi/auth.yaml) |
| catalog-service | 8082 | `catalog_db` | `catalog` | [ReadMe](../../catalog-service/ReadMe.md) | [catalog.yaml](../openapi/catalog.yaml) |
| cart-service | 8083 | `cart_db` | `cart` | [ReadMe](../../cart-service/ReadMe.md) | [cart.yaml](../openapi/cart.yaml) |
| order-service | 8084 | `order_db` | `orders` | [ReadMe](../../order-service/ReadMe.md) | [order.yaml](../openapi/order.yaml) |
| payment-service | 8085 | `payment_db` | `payment` | [ReadMe](../../payment-service/ReadMe.md) | [payment.yaml](../openapi/payment.yaml) |

* En local y en Docker cada servicio tiene **su propia base** en un único Postgres (el compose crea las cinco con `docker/postgres/init-databases.sql`).
* En Render el plan gratuito permite una sola base, así que cada servicio usa **su propio esquema** (`DB_SCHEMA`). Ningún servicio lee tablas de otro: el aislamiento se mantiene.
* El esquema lo crea Flyway (`V1__...sql`) y Hibernate solo lo valida.

---

## 🔗 Dependencias síncronas entre servicios

```mermaid
flowchart LR
    FE[Frontend] --> AUTH[auth-service]
    FE --> CATALOG[catalog-service]
    FE --> CART[cart-service]
    FE --> ORDER[order-service]
    FE --> PAYMENT[payment-service]
    CART -- "GET /products/{id}<br/>(precio, existe y activo)" --> CATALOG
    ORDER -- "GET /cart, DELETE /cart<br/>(token del usuario)" --> CART
    ORDER -- "GET /products/{id}<br/>(precio vigente)" --> CATALOG
    PAYMENT -- "GET /orders/{id}<br/>(token del usuario)" --> ORDER
    PAYMENT -- "POST /orders/{id}/pay<br/>(X-Internal-Token)" --> ORDER
```

| Llamada | Autenticación | Si el destino falla |
|---|---|---|
| cart → catalog | ninguna (`GET` público) | 404 si el producto no existe; 502 / 503 |
| order → cart | reenvía el JWT del usuario | 401 si cart rechaza el token; 502 / 503 |
| order → catalog | ninguna (`GET` público) | 404 si algún producto ya no existe; 502 / 503 |
| payment → order (consulta) | reenvía el JWT del usuario | 403 / 404 / 409 según el pedido; 502 / 503 |
| payment → order (`/pay`) | token interno `X-Internal-Token` | 502 / 503 |

* **502**: el otro servicio respondió con un error inesperado. **503**: no respondió.
* auth-service no recibe llamadas de otros servicios: cada servicio valida el JWT localmente con el mismo `JWT_SECRET`.

---

## 🛒 Secuencia del checkout

```mermaid
sequenceDiagram
    actor U as Cliente
    participant A as auth
    participant C as cart
    participant K as catalog
    participant O as order
    participant P as payment

    U->>A: POST /auth/login
    A-->>U: accessToken (JWT)

    U->>C: POST /cart/items {productId, quantity}
    C->>K: GET /products/{id}
    K-->>C: precio, activo
    C-->>U: carrito con total

    U->>O: POST /orders {address}
    O->>C: GET /cart (JWT del usuario)
    C-->>O: items
    loop por cada item
        O->>K: GET /products/{id}
        K-->>O: precio vigente
    end
    O->>O: guarda pedido CREATED con el total recalculado
    O->>C: DELETE /cart
    O-->>U: 201 Location: /orders/{id}

    U->>P: POST /payments/process {orderId, amount}
    P->>O: GET /orders/{id} (JWT del usuario)
    O-->>P: dueño, estado CREATED, total
    P->>P: valida amount = total y registra el pago
    alt APPROVED
        P->>O: POST /orders/{id}/pay (X-Internal-Token)
        O-->>P: pedido PAID
    else REJECTED
        Note over P,O: el pedido sigue CREATED y se puede reintentar
    end
    P-->>U: {status, transactionId}
```

---

## 🧠 Decisiones arquitectónicas clave

* Separación total entre frontend y backend
* Un microservicio por dominio de negocio
* Una base de datos por microservicio
* Pagos simulados para evitar dependencias externas
* El precio vive solo en catalog-service y se recalcula en el servidor
* JWT validado localmente en cada servicio (sin llamar a auth en cada request)
* Token interno de servicio para la única operación que no puede hacer un usuario (marcar un pedido como pagado)
* Arquitectura orientada a escalabilidad y mantenibilidad

---

## 📍 Alcance actual

Esta documentación corresponde al **MVP** del proyecto. No se incluyen en esta fase:

* Integraciones con sistemas externos reales
* Comunicación asíncrona por eventos
* Diagramas de componentes o código

Estos aspectos se incorporarán en iteraciones posteriores.

---


