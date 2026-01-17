# E-commerce Platform – MVP

## Visión General

Este proyecto es una **plataforma de E-commerce** diseñada con un enfoque **moderno, escalable y orientado a buenas prácticas de arquitectura**.

El objetivo principal del MVP es **demostrar dominio técnico y criterio arquitectónico**, aplicando:

- **Backend:** Spring Boot, Arquitectura Hexagonal, Microservicios  
- **Frontend:** React con Microfrontends  
- **Seguridad:** JWT y control de roles  
- **Calidad:** JUnit y Mockito  
- **Infraestructura:** Docker y Kubernetes  
- **Documentación:** OpenAPI / Swagger  
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
  - **SUPER_ADMIN**: usuario raíz del sistema
  - **ADMIN**: gestión del e-commerce
  - **CLIENT**: usuario final

---

#### Catálogo
- CRUD de productos (**ADMIN**)
- CRUD de categorías (**ADMIN**)
- Listado público de productos
- Filtro de productos por categoría

---

#### Carrito de Compras
- Un carrito activo por usuario
- Agregar productos al carrito
- Modificar cantidades
- Eliminar productos
- Vaciar carrito

---

#### Pedidos
- Creación de pedidos a partir del carrito
- Estados del pedido:
  - `CREATED`
  - `PAID`
  - `CANCELLED`
- Listado de pedidos por usuario
- Listado global de pedidos (**ADMIN**)

---

#### Pagos (Mock)
- Simulación de pagos
- Resultado de pago controlado (aprobado / rechazado)
- Cambio de estado del pedido a `PAID`

---

#### Panel de Administración
- Gestión de productos
- Gestión de categorías
- Visualización de pedidos
- Gestión de usuarios
- Asignación y revocación del rol **ADMIN** (solo **SUPER_ADMIN**)

---

## Funcionalidades Excluidas del MVP

Las siguientes funcionalidades **no forman parte del MVP** y se consideran futuras mejoras:

- Integración con pasarelas de pago reales
- Promociones, cupones y descuentos
- Gestión avanzada de inventario
- Envíos y logística
- Notificaciones (email, SMS)
- Mensajería asíncrona (Kafka, RabbitMQ)
- Tests end-to-end y contract testing

---

## Arquitectura General

### Microservicios

| Servicio | Responsabilidad |
|--------|------------------|
| Auth/User Service | Autenticación, autorización y usuarios |
| Catalog Service | Productos y categorías |
| Cart Service | Gestión del carrito |
| Order Service | Gestión de pedidos |
| Payment Service | Simulación de pagos |

Cada microservicio:
- Implementa **arquitectura hexagonal**
- Tiene **base de datos independiente**
- Expone **APIs REST documentadas con OpenAPI**

---

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

---

## Organización de Repositorios

El proyecto se divide en dos repositorios principales:

### Backend
Repositorio: `ecommerce-backend`

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

## Objetivo del MVP

Este MVP busca demostrar:

- Diseño limpio y mantenible
- Uso correcto de arquitectura hexagonal
- Separación clara de responsabilidades
- Seguridad basada en roles
- Buenas prácticas de testing, documentación y despliegue

---

## Estado del Proyecto

🚧 En desarrollo 
