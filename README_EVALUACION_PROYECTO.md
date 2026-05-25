# Evaluación del Proyecto E-Commerce Backend

## 1. Resumen ejecutivo

Este informe evalúa el estado actual del backend del proyecto Spring Boot con microservicios y compara lo implementado con los contratos iniciales y los requerimientos documentados.

Estado general:
- Servicios implementados en el repositorio: `auth-service`, `catalog-service`, `cart-service`, `order-service`.
- Servicio documentado pero no encontrado en el código: `payment-service`.
- El contrato OpenAPI existe para `auth`, `cart`, `catalog`, `order` y `payment`.
- El backend está parcialmente alineado con los requerimientos funcionales, pero hay inconsistencias importantes en rutas, DTOs, seguridad e integraciones.

---

## 2. Cumplimiento con los contratos

### 2.1 Auth Service
- `POST /auth/login`: implementado.
- `POST /auth/register`: implementado.
- `GET /users`: no implementado como tal. El código expone `GET /admin/users`.
- `PUT /users/{id}/role`: no implementado como tal. El código expone `PUT /admin/users/{id}/role` y usa `@RequestParam role`.

DTOs e integraciones:
- `LoginResponse` real devuelve solo `token`; el contrato define `accessToken`, `tokenType` y `expiresIn`.
- El controlador de usuario expone roles como lista y estado `enabled`, mientras que el contrato define un `role` simple.
- `@PreAuthorize("hasRole('SUPER_ADMIN')")` está comentado en `UserController`, por lo que el control de acceso no está activo.

### 2.2 Catalog Service
- `GET /categories`: implementado.
- `POST /categories`: implementado.
- `PUT /categories/{id}`: implementado.
- `DELETE /categories/{id}`: implementado.
- `GET /products`: implementado con filtro por `categoryId` y `name`; el contrato define `categoryId` y `active`.
- `POST /products`: implementado.
- `GET /products/{id}`: implementado.
- `PUT /products/{id}`: implementado.
- `DELETE /products/{id}`: implementado.

DTOs e integraciones:
- `CategoryRequest` real solo incluye `name`; el contrato describe `name` y `description`.
- `ProductRequest` real incluye `stock` y `categoryId`; el contrato no documenta `stock` pero sí `active`.
- Los filtros de productos en el controller usan `name`, mientras el contrato abre la puerta a `active`.

### 2.3 Cart Service
- `GET /cart`: implementado.
- `DELETE /cart`: implementado.
- `POST /cart/items`: implementado.
- `PUT /cart/items/{productId}`: implementado.
- `DELETE /cart/items/{productId}`: implementado.

DTOs e integraciones:
- El contrato de carrito está mayormente alineado con la implementación.
- Sin embargo, el controlador usa un import incorrecto de `Authentication` que debe corregirse.

### 2.4 Order Service
- `POST /orders`: implementado.
- `GET /orders`: implementado.
- `GET /orders/{id}`: implementado.

DTOs e integraciones:
- El contrato OpenAPI exige autenticación bearer JWT, pero el controller real usa `@RequestHeader("X-User-Id")` en lugar de extraer el usuario del token.
- El contrato describe `CreateOrderRequest` con `address`; la implementación también requiere `total` e `items`.
- No se encontró integración real con `cart-service` para construir el pedido desde el carrito.
- No hay evidencia de integración con `payment-service` en el código existente.

### 2.5 Payment Service
- Existe un contrato OpenAPI en `docs/openapi/payment.yaml`.
- No se encontró un módulo o servicio `payment-service` en el repositorio.
- La documentación indica un servicio mock de pagos, pero no existe la implementación correspondiente.

---

## 3. Errores e inconsistencias detectadas

### 3.1 Problemas críticos de seguridad y rutas
- `auth-service` expone `UserController` en `/admin`, lo que no coincide con `/users` del contrato.
- El endpoint de administración no tiene autorización activa: `@PreAuthorize` está comentado.
- Solo el `auth-service` tiene configuración JWT. `catalog-service`, `cart-service` y `order-service` no aplican seguridad JWT en sus controladores.
- `order-service` depende de un header `X-User-Id` en vez de un token JWT válido, lo que es un riesgo serio de seguridad e inconsistencia de contrato.

### 3.2 Errores de implementación o código sospechoso
- En `cart-service/src/main/java/com/e_commerce/cart_service/adapter/in/rest/CartController.java` se importa `org.apache.tomcat.util.net.openssl.ciphers.Authentication` en lugar de `org.springframework.security.core.Authentication`.
- `OrderController.createOrder()` responde con `ResponseEntity.created(null)`, lo que no construye una URI válida para el recurso creado.
- `auth-service` `changeRole()` usa `@RequestParam String role` en lugar de un payload JSON como en el contrato.

### 3.3 Contratos incompletos o no sincronizados
- La definición de `LoginResponse` en OpenAPI no coincide con el DTO final.
- La definición de `ProductRequest` en OpenAPI difiere de la implementación real (`stock` y `active`).
- `CategoryRequest` en la implementación solo incluye `name`; no se documenta `description` como indica el contrato.
- El contrato de `order` no refleja que el payload real también envía `items` y `total`.

### 3.4 Ausencia de elementos clave
- Servicio `payment-service` ausente.
- No hay evidencia de flujo de pedido desde carrito a orden.
- No se detecta una capa de gateway o API Gateway para unificar seguridad y rutas entre microservicios.
- No se localizaron pruebas de contrato OpenAPI ni pruebas end-to-end.

---

## 4. Elementos faltantes respecto a los requerimientos funcionales y no funcionales

### Funcionales
- `payment-service` no implementado.
- Flujo de checkout completo no presente: no hay integración de `cart-service` con `order-service`.
- No se verifica el estado del pedido (`PAID`, `CANCELLED`) ni se actualiza a partir de pago.
- No hay control de roles aplicado en el servicio de administración de usuarios.
- No hay endpoint de `products` con filtro `active` según el contrato.

### No funcionales
- Seguridad inconsistente entre servicios.
- Falta de control de acceso en endpoints administrativos.
- Documentación OpenAPI no está sincronizada con la implementación.
- No hay contract testing o pruebas de integración de APIs.
- No se observa un README de cada microservicio ni un proceso de despliegue unificado.

---

## 5. Recomendaciones de buenas prácticas

### Tests
- Agregar pruebas de integración para cada microservicio.
- Implementar pruebas de contrato API basadas en OpenAPI.
- Añadir tests de seguridad para verificar JWT, roles y autorización.
- Crear pruebas end-to-end del flujo crítico: login → carrito → pedido → pago.

### Seguridad
- Normalizar la autenticación JWT en todos los servicios.
- Corregir el import de `Authentication` en `CartController`.
- Rehabilitar y aplicar `@PreAuthorize` en endpoints administrativos.
- Eliminar el uso de `X-User-Id` como reemplazo de la identidad del usuario.
- Usar claims del JWT para obtener el `userId` en `order-service`.

### Documentación
- Sincronizar OpenAPI con el código real o generar controladores desde el contrato.
- Incluir ejemplos de request/response en la documentación.
- Documentar cada microservicio con su propio README y con instrucciones de ejecución.
- Actualizar el README general para reflejar el alcance real del repositorio actual.

### Arquitectura
- Definir si `payment-service` debe existir como módulo independiente o si el contrato debe eliminarse.
- Implementar un flujo de checkout claro: carrito → orden → pago.
- Evaluar un API Gateway o un BFF para centralizar seguridad y enrutamiento.
- Mantener la arquitectura hexagonal con fronteras claras entre adaptadores y dominio.

---

## 6. Paso a paso ordenado para concluir el proyecto profesionalmente

1. Corregir la seguridad básica en `auth-service`:
   - Rehabilitar `@PreAuthorize("hasRole('SUPER_ADMIN')")` en `UserController`.
   - Alinear las rutas administrativas con el contrato o actualizar el contrato según la ruta real.
   - Verificar que `POST /auth/login` y `POST /auth/register` mantengan consistencia de respuesta.

2. Arreglar fallos de compilación funcionales en `cart-service`:
   - Cambiar el import de `Authentication` al tipo de Spring Security.
   - Validar que todos los endpoints de carrito requieren JWT.

3. Alinear `catalog-service` con el contrato:
   - Revisar `ProductRequest` y documentación OpenAPI para incluir o eliminar campos como `stock` y `active`.
   - Ajustar `CategoryRequest` para que el contrato y el controller sean consistentes.
   - Agregar filtro `active` si el requisito se mantiene.

4. Fortalecer `order-service`:
   - Reemplazar `X-User-Id` por identificación derivada del token JWT.
   - Implementar flujo de checkout real desde `cart-service` o documentar claramente por qué no se hace.
   - Hacer que `createOrder()` devuelva una URI válida en `Location`.

5. Implementar o remover `payment-service`:
   - Si se requiere pago mock, crear el servicio y conectarlo al flujo de pedido.
   - Si no se va a implementar ahora, eliminar el contrato OpenAPI o marcarlo claramente como pendiente.

6. Sincronizar la documentación:
   - Ajustar `docs/openapi/*.yaml` con las rutas, cuerpo y DTOs reales.
   - Documentar el estado actual de cada servicio en un README local.
   - Añadir un README raíz de backend que explique qué servicios están completos y cuáles están en desarrollo.

7. Añadir pruebas faltantes:
   - Contract testing con OpenAPI para cada servicio.
   - Integración de seguridad JWT.
   - Flujo de negocio completo: carrito → pedido → pago.

8. Revisar calidad y entregar:
   - Verificar que cada servicio compile y pase `mvn test`.
   - Ejecutar un test de humo del flujo completo.
   - Documentar los comandos de lanzamiento, puertos y variables de entorno.

---

## 7. Conclusión

El backend tiene una base sólida en términos de estructura de microservicios y documentación inicial, pero hoy el proyecto no está totalmente alineado con los contratos y todavía presenta elementos críticos faltantes:
- `payment-service` no existe.
- hay incoherencias de rutas y DTOs en `auth-service` y `catalog-service`.
- la seguridad no está aplicada de forma consistente.
- el flujo de carrito a pedido/ pago no está completado.

Para cerrar el proyecto profesionalmente, prioriza primero la corrección de seguridad, la alineación de contratos y la implementación del flujo de checkout, y luego añade pruebas de integración y documentación precisa.
