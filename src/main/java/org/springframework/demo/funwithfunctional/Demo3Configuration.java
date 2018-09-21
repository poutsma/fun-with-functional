package org.springframework.demo.funwithfunctional;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.demo.funwithfunctional.owner.OwnerHandler;
import org.springframework.demo.funwithfunctional.owner.OwnerRepository;
import org.springframework.demo.funwithfunctional.pet.PetHandler;
import org.springframework.demo.funwithfunctional.pet.PetRepository;
import org.springframework.demo.funwithfunctional.security.RandomSecurityManager;
import org.springframework.demo.funwithfunctional.security.SecurityManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class Demo3Configuration {

	private SecurityManager securityManager = new RandomSecurityManager();

	@Bean
	public PetHandler petHandler(PetRepository repository) {
		return new PetHandler(repository);
	}

	@Bean
	OwnerHandler ownerHandler(OwnerRepository repository) {
		return new OwnerHandler(repository);
	}

	@Bean
	RouterFunction<ServerResponse> mainRouter(PetHandler petHandler, OwnerHandler ownerHandler) {

		RouterFunction<ServerResponse> petsRouter = petsRouter(petHandler);
		RouterFunction<ServerResponse> ownerRouter = ownerRouter(ownerHandler);

		return petsRouter.and(ownerRouter)
				.filter(this::security);
	}

	public Mono<ServerResponse> security(ServerRequest request, HandlerFunction<ServerResponse> next) {
		if (this.securityManager.hasAccess(request)) {
			return next.handle(request);
		}
		else {
			return ServerResponse.status(HttpStatus.FORBIDDEN).build();
		}
	}



	public RouterFunction<ServerResponse> petsRouter(PetHandler petHandler) {

		RouterFunction<ServerResponse> html = route()
				.nest(accept(TEXT_HTML), builder -> { builder
					.GET("/{id}", petHandler::renderPet)
					.GET("", petHandler::renderPets);
				}).build();

		RouterFunction<ServerResponse> json = route()
				.nest(accept(APPLICATION_JSON), builder -> { builder
					.GET("/{id}", accept(APPLICATION_JSON), petHandler::showPet)
					.GET("", accept(APPLICATION_JSON), petHandler::showPets);
				}).build();

		return route()
				.path("/pets", () -> html.and(json))
				.build();
	}


	public RouterFunction<ServerResponse> ownerRouter(OwnerHandler ownerHandler) {
		return nest(path("/owners").and(accept(APPLICATION_JSON)),
				route(GET("/{id}"), ownerHandler::showOwner)
						.andRoute(method(HttpMethod.GET), ownerHandler::showOwners));
	}

}
