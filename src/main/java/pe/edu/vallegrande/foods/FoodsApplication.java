/**
 * Paquete principal de la aplicación Foods.
 */
package pe.edu.vallegrande.foods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodsApplication {

	private FoodsApplication() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 *
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(FoodsApplication.class, args);
	}
}
