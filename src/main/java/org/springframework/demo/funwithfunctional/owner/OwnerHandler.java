package org.springframework.demo.funwithfunctional.owner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author Arjen Poutsma
 */
@Component
public class OwnerHandler {

	private final OwnerRepository ownerRepository;

	public OwnerHandler(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	public Mono<ServerResponse> showOwner(ServerRequest request) {
		String id = request.pathVariable("id");
		return this.ownerRepository.findById(id)
				.flatMap(pet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(
						pet)))
				.switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()));
	}

	public Mono<ServerResponse> showOwners(ServerRequest request) {
		Flux<Owner> owners = this.ownerRepository.findAll();
		return ServerResponse.ok().contentType(APPLICATION_JSON).body(owners, Owner.class);
	}

	public RouterFunction<?> ownerRouter() {
		return nest(path("/owners").and(accept(APPLICATION_JSON)),
				route(GET("/{id}"), this::showOwner)
						.andRoute(method(HttpMethod.GET), this::showOwners));
	}

}
