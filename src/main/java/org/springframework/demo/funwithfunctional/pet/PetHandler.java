package org.springframework.demo.funwithfunctional.pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RenderingResponse;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author Arjen Poutsma
 */
@Component
public class PetHandler {

	private final PetRepository petRepository;

	public PetHandler(PetRepository petRepository) {
		this.petRepository = petRepository;
	}

	public Mono<ServerResponse> showPet(ServerRequest request) {
		String id = request.pathVariable("id");
		return this.petRepository.findById(id)
				.flatMap(pet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(
						pet)))
				.switchIfEmpty(Mono.defer(() ->ServerResponse.notFound().build()));
	}

	public Mono<ServerResponse> showPets(ServerRequest request) {
		Flux<Pet> pets = this.petRepository.findAll();
		return ServerResponse.ok().contentType(APPLICATION_JSON).body(pets, Pet.class);
	}

	public Mono<RenderingResponse> renderPet(ServerRequest request) {
		String id = request.pathVariable("id");
		return this.petRepository.findById(id).flatMap(
				pet -> RenderingResponse.create("pet").modelAttribute("pet", pet).build());
	}

	public Mono<RenderingResponse> renderPets(ServerRequest request) {
		Flux<Pet> pets = this.petRepository.findAll();
		return RenderingResponse.create("pets").modelAttribute("pets", pets).build();
	}

	/*
		@Bean
		public RouterFunction<?> personRouter(PetHandler personHandler) {
			RouterFunction<RenderingResponse> html =
					route(GET("/pets/{id}").and(accept(MediaType.TEXT_HTML)), personHandler::renderPet)
					.andRoute(GET("/pets").and(accept(MediaType.TEXT_HTML)), personHandler::renderPets);

			RouterFunction<ServerResponse> json =
					route(GET("/pets/{id}").and(accept(MediaType.APPLICATION_JSON)),personHandler::showPet)
					.andRoute(GET("/pets").and(accept(MediaType.APPLICATION_JSON)),personHandler::showPets);

			return html.andOther(json);
		}
	*/

/*
	@Bean
	public RouterFunction<?> personRouter(PetHandler personHandler) {
		RouterFunction<RenderingResponse> html =
				nest(accept(TEXT_HTML),
					route(GET("/{id}"), personHandler::renderPet)
					.andRoute(method(HttpMethod.GET), personHandler::renderPets));

		RouterFunction<ServerResponse> json =
				nest(accept(APPLICATION_JSON),
					route(GET("/{id}"),personHandler::showPet)
					.andRoute(method(HttpMethod.GET),personHandler::showPets));

		return nest(path("/pets"), html.andOther(json));
	}
*/

/*
	@Bean
	public RouterFunction<?> petsRouter(PetHandler petHandler) {
		RouterFunction<RenderingResponse> html =
				nest(isHtml(),
					route(GET("/{id}"), petHandler::renderPet)
					.andRoute(method(HttpMethod.GET), petHandler::renderPets));

		RouterFunction<ServerResponse> json =
				nest(isJson(),
					route(GET("/{id}"), petHandler::showPet)
					.andRoute(method(HttpMethod.GET), petHandler::showPets));

		return nest(path("/pets"), html.andOther(json));
	}
*/

	public RouterFunction<?> petsRouter() {
		RouterFunction<RenderingResponse> html =
				nest(isHtml(),
					route(GET("/{id}"), this::renderPet)
					.andRoute(method(HttpMethod.GET), this::renderPets));

		RouterFunction<ServerResponse> json =
				nest(isJson(),
					route(GET("/{id}"), this::showPet)
					.andRoute(method(HttpMethod.GET), this::showPets));

		return nest(path("/pets"), html.andOther(json));
	}

	private RequestPredicate isHtml() {
		return accept(TEXT_HTML).or(queryParam("format", "html"));
	}

	private RequestPredicate isJson() {
		return accept(APPLICATION_JSON).or(queryParam("format", "json"));
	}


}
