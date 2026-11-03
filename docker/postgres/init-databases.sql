-- Se ejecuta una sola vez, cuando el volumen de Postgres está vacío.
-- Una base por microservicio; el esquema de cada una lo crea Flyway al arrancar el servicio.
CREATE DATABASE auth_db;
CREATE DATABASE catalog_db;
CREATE DATABASE cart_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
