package com.e_commerce.catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Clase principal que inicia la aplicación Spring Boot del microservicio de Catálogo.
 * <p>
 * Este es el punto de entrada del servicio Catalog Service, responsable de gestionar
 * productos, categorías y toda la información relacionada con el catálogo de productos
 * del e-commerce.
 * </p>
 * <p>
 * Utiliza la anotación {@code @SpringBootApplication} para habilitar la auto-configuración,
 * escaneo de componentes y configuración de Spring Boot en el paquete actual y subpaquetes.
 * </p>
 */

@SpringBootApplication
public class CatalogServiceApplication {


    /**
     * Método principal que arranca la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos pasados a la aplicación
     */
	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}

}
