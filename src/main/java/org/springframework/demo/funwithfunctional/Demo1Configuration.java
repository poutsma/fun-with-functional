package org.springframework.demo.funwithfunctional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.demo.funwithfunctional.pet.PetHandler;
import org.springframework.demo.funwithfunctional.pet.PetRepository;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class Demo1Configuration {

	@Bean
	public PetHandler petHandler(PetRepository repository) {
		return new PetHandler(repository);
	}

	@Bean
	public RouterFunction<ServerResponse> routerFunction(PetHandler petHandler) {
		RouterFunction<ServerResponse> html = route()
				.GET("/pets/{id}", accept(TEXT_HTML), petHandler::renderPet)
				.GET("/pets", accept(TEXT_HTML), petHandler::renderPets)
				.build();

		RouterFunction<ServerResponse> json = route()
				.GET("/pets/{id}", accept(APPLICATION_JSON), petHandler::showPet)
				.GET("/pets", accept(APPLICATION_JSON), petHandler::showPets)
				.build();

		return html.and(json);
	}

}
