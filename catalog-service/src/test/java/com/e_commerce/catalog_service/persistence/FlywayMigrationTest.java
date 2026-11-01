package com.e_commerce.catalog_service.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Aplica las migraciones de db/migration sobre H2 en modo PostgreSQL y arranca Hibernate con
 * ddl-auto=validate: si una entidad no coincide con el esquema (tabla, columna o secuencia),
 * el contexto no levanta y el test falla.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:flyway-catalog;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class FlywayMigrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    void migrationsApplyAndMatchEntities() {
        assertTrue(flyway.info().applied().length > 0, "no se aplicó ninguna migración");
    }
}
