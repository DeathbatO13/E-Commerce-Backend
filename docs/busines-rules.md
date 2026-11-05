# Reglas de Negocio Globales – E-commerce MVP

## Introducción

Este documento define las **reglas de negocio globales** del sistema E-commerce.
Son **transversales a todos los microservicios** y deben respetarlas:

- Casos de uso
- Validaciones de dominio
- Seguridad
- Frontend

El objetivo es garantizar **consistencia funcional**, independientemente de la implementación técnica.
Entre paréntesis se indica el servicio que hace cumplir cada regla.

---

## 1. Gestión de Usuarios y Roles

### 1.1 Registro de usuarios
- Todo usuario que se registra obtiene automáticamente el rol **CLIENT**. *(auth)*
- No es posible registrarse como ADMIN o SUPER_ADMIN: el registro no acepta roles.
- El email es único: registrar uno existente responde 409.

### 1.2 Usuario SUPER_ADMIN
- Existe **un único usuario SUPER_ADMIN**.
- Se crea por **configuración** al arrancar auth-service (`ADMIN_EMAIL` / `ADMIN_PASSWORD`), solo si todavía no existe un usuario con ese email.
- No se crea mediante endpoints públicos, no puede ser eliminado y no puede perder su rol.

### 1.3 Asignación de rol ADMIN
- Solo el **SUPER_ADMIN** puede asignar o revocar el rol ADMIN (`CLIENT` ⇄ `ADMIN`).
- El rol `SUPER_ADMIN` no se puede asignar (400) y el rol del SUPER_ADMIN no se puede cambiar (409).
- Un ADMIN **no puede** asignar roles.

### 1.4 Login
- El login emite un JWT con el id del usuario (`sub`) y sus roles.
- Un usuario **deshabilitado** no puede hacer login (401). Ese motivo solo se informa si la contraseña es correcta, para no revelar qué cuentas existen.

---

## 2. Gestión del Catálogo

### 2.1 Permisos
- Solo **ADMIN** o **SUPER_ADMIN** pueden crear, modificar o eliminar productos y categorías. *(catalog)*

### 2.2 Acceso público
- El listado y el detalle de productos y categorías son **públicos**; no se requiere autenticación.

### 2.3 Productos y categorías
- Un producto debe tener nombre, precio mayor que 0, stock mayor o igual a 0 y una categoría activa.
- La eliminación es **lógica** (`active = false`): lo inactivo no se lista, no se puede consultar y no se puede agregar al carrito.
- No se puede eliminar una categoría que tenga productos activos (409).
- No pueden existir dos categorías activas con el mismo nombre (409).

### 2.4 Fuente del precio
- **catalog-service es la única fuente del precio.** El cliente nunca envía precios: el carrito y el pedido los piden a catalog en el servidor.

---

## 3. Carrito de Compras

### 3.1 Carrito activo
- Cada usuario autenticado tiene **un único carrito**, identificado por el `sub` del JWT. *(cart)*
- Si el usuario no tiene carrito, `GET /cart` devuelve uno vacío y se crea al agregar el primer producto.
- Pueden usar el carrito **CLIENT** y **ADMIN**. El SUPER_ADMIN no tiene carrito (y por eso tampoco puede crear pedidos).

### 3.2 Gestión del carrito
- El usuario puede agregar productos, modificar cantidades, eliminar productos y vaciar el carrito.
- Las cantidades son mayores que 0. Agregar un producto que ya está suma la cantidad; modificarla la reemplaza.
- No es posible agregar productos inexistentes o deshabilitados (404).
- El precio guardado en el carrito es **informativo**: el pedido lo vuelve a calcular (ver 4.1).

---

## 4. Pedidos

### 4.1 Creación de pedidos
- Un pedido **solo puede crearse a partir del carrito** del usuario; no es posible crearlo manualmente ni con el carrito vacío (400). *(order)*
- Al crearlo, el **precio de cada producto se recalcula** con el valor vigente en catalog-service. Si algún producto ya no existe o está inactivo, el pedido no se crea (404).
- Al crearse el pedido, **el carrito se vacía**.

### 4.2 Estados del pedido
- `CREATED`: pedido creado desde el carrito, pendiente de pago
- `PAID`: pago aprobado
- `CANCELLED`: pedido cancelado

```
CREATED ──(pago aprobado)──▶ PAID
   └──(cancelar)──▶ CANCELLED
```

### 4.3 Cancelación
- Un pedido solo puede cancelarse en estado `CREATED`.
- Un pedido `PAID` no puede cancelarse en el MVP (409).

---

## 5. Pagos

### 5.1 Pago simulado
- El sistema implementa un **pago simulado (mock)**; el resultado puede ser aprobado o rechazado. *(payment)*

### 5.2 Quién dispara el pago
- El pago lo inicia el **usuario** con `POST /payments/process`, indicando el pedido y el monto.
- Antes de registrar el pago, payment-service consulta el pedido en order-service con el token del usuario:
  - el pedido debe **pertenecer al usuario** (403);
  - debe estar en estado `CREATED` (409);
  - el **monto debe coincidir con el total** del pedido (400).

### 5.3 Impacto del pago
- Todo intento de pago queda registrado, aprobado o rechazado.
- Si el pago es **aprobado**, payment-service marca el pedido como `PAID` llamando a `POST /orders/{id}/pay` con un **token interno de servicio** (`X-Internal-Token`). Ningún usuario, ni siquiera un ADMIN, puede marcar un pedido como pagado.
- Si el pago es **rechazado**, el pedido permanece en `CREATED` y **se puede reintentar** el pago.

---

## 6. Acceso a la Información

### 6.1 Pedidos
- Un CLIENT solo puede ver y cancelar **sus propios pedidos** (403 si no son suyos).
- ADMIN y SUPER_ADMIN pueden ver y cancelar todos los pedidos.

### 6.2 Usuarios
- ADMIN y SUPER_ADMIN pueden **listar** usuarios.
- Solo el SUPER_ADMIN puede **modificar roles**. Un ADMIN no gestiona usuarios más allá de verlos.

---

## 7. Consistencia del Sistema

- Las reglas de negocio:
  - se implementan en la capa de dominio o de casos de uso;
  - se validan con tests unitarios.
- El frontend **no sustituye** las validaciones de backend.
- Cualquier cambio en estas reglas debe reflejarse en este documento y en los contratos de [`docs/openapi`](./openapi).

### Limitaciones conocidas del MVP
- Crear un pedido no es atómico entre servicios: si el pedido se guarda pero falla el vaciado del carrito, el carrito queda con los productos.
- No hay control de stock: crear o pagar un pedido no descuenta unidades.

---

## Alcance

Estas reglas aplican exclusivamente al **MVP**.
Reglas más avanzadas (inventario, envíos, promociones, etc.) quedan fuera de este alcance.

---

## Estado del Documento

📌 Versión 1.1 – MVP: alineada con la implementación de los 5 servicios.
