package br.com.uniamerica.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.uniamerica.api.service.AgendaService;
/**
 * @author Eduardo Mendes
 * 
 * @version 1.0.0, 22/03/2022
 */
@SpringBootApplication	
public class ConsultorioApiApplication {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConsultorioApiApplication.class, args);
		AgendaService aS = new AgendaService();
		System.out.println(aS.testeLista());
	}
}
