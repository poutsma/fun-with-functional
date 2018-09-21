package org.springframework.demo.funwithfunctional.pet;

import java.util.Collections;

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

	public Mono<ServerResponse> renderPet(ServerRequest request) {
		String id = request.pathVariable("id");
		return this.petRepository.findById(id).flatMap(
				pet -> RenderingResponse.create("pet").modelAttribute("pet", pet).build());
	}

	public Mono<ServerResponse> renderPets(ServerRequest request) {
		Flux<Pet> pets = this.petRepository.findAll();
		return RenderingResponse.create("pets").modelAttribute("pets", pets).build()
				.map(r -> r);
	}

}
