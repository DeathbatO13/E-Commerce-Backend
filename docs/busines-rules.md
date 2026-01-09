# Reglas de Negocio Globales – E-commerce MVP

## Introducción

Este documento define las **reglas de negocio globales** del sistema E-commerce.  
Estas reglas son **transversales a todos los microservicios** y deben ser respetadas por:

- Casos de uso
- Validaciones de dominio
- Seguridad
- Frontend

El objetivo es garantizar **consistencia funcional**, independientemente de la implementación técnica.

---

## 1. Gestión de Usuarios y Roles

### 1.1 Registro de usuarios
- Todo usuario que se registra en el sistema obtiene automáticamente el rol **CLIENT**.
- No es posible registrarse directamente como ADMIN o SUPER_ADMIN.

---

### 1.2 Usuario SUPER_ADMIN
- Existe **un único usuario SUPER_ADMIN** en el sistema.
- El SUPER_ADMIN:
  - Se crea mediante **configuración inicial del sistema**
  - No puede ser eliminado
  - No puede perder su rol
- El SUPER_ADMIN no se crea mediante endpoints públicos.

---

### 1.3 Asignación de rol ADMIN
- Solo un usuario con rol **SUPER_ADMIN** puede:
  - Asignar el rol ADMIN a otro usuario
  - Revocar el rol ADMIN
- Un usuario ADMIN puede volver a ser CLIENT si el SUPER_ADMIN lo decide.
- Un usuario ADMIN **no puede** asignar roles.

---

## 2. Gestión del Catálogo

### 2.1 Permisos
- Solo usuarios con rol **ADMIN** o **SUPER_ADMIN** pueden:
  - Crear productos
  - Modificar productos
  - Eliminar productos
  - Crear, modificar o eliminar categorías

---

### 2.2 Acceso público
- El listado de productos y categorías es **público**.
- No se requiere autenticación para visualizar el catálogo.

---

## 3. Carrito de Compras

### 3.1 Carrito activo
- Cada usuario autenticado puede tener **un único carrito activo**.
- Si un usuario no tiene carrito activo, el sistema debe crearlo automáticamente al primer uso.

---

### 3.2 Gestión del carrito
- El usuario puede:
  - Agregar productos al carrito
  - Modificar la cantidad de productos
  - Eliminar productos
  - Vaciar el carrito
- No es posible agregar productos inexistentes o deshabilitados.

---

## 4. Pedidos

### 4.1 Creación de pedidos
- Un pedido **solo puede crearse a partir de un carrito activo**.
- No es posible crear pedidos manualmente ni sin carrito.

---

### 4.2 Estados del pedido
Un pedido puede tener los siguientes estados:

- `CREATED`: pedido creado desde el carrito
- `PAID`: pago aprobado
- `CANCELLED`: pedido cancelado

---

### 4.3 Cancelación
- Un pedido solo puede cancelarse si:
  - Se encuentra en estado `CREATED`
- Un pedido en estado `PAID` no puede cancelarse en el MVP.

---

## 5. Pagos

### 5.1 Pago simulado
- El sistema implementa un **pago simulado (mock)**.
- El resultado del pago puede ser:
  - Aprobado
  - Rechazado

---

### 5.2 Impacto del pago
- Si el pago es aprobado:
  - El estado del pedido cambia a `PAID`
- Si el pago es rechazado:
  - El pedido permanece en estado `CREATED`

---

## 6. Acceso a la Información

### 6.1 Pedidos
- Un usuario CLIENT solo puede:
  - Visualizar sus propios pedidos
- Un usuario ADMIN o SUPER_ADMIN puede:
  - Visualizar todos los pedidos del sistema

---

### 6.2 Usuarios
- Solo ADMIN y SUPER_ADMIN pueden listar usuarios.
- Solo SUPER_ADMIN puede modificar roles.

---

## 7. Consistencia del Sistema

- Todas las reglas de negocio deben:
  - Implementarse en la capa de dominio o casos de uso
  - Validarse mediante tests unitarios
- El frontend **no sustituye** las validaciones de backend.
- Cualquier cambio en estas reglas debe reflejarse en este documento.

---

## Alcance

Estas reglas aplican exclusivamente al **MVP** del proyecto.  
Reglas más avanzadas (inventario, envíos, promociones, etc.) quedan fuera de este alcance.

---

## Estado del Documento

📌 Versión inicial – MVP  
📅 Pendiente de futuras extensiones
