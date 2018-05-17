package org.springframework.demo.funwithfunctional;

import java.time.Instant;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.demo.funwithfunctional.owner.OwnerHandler;
import org.springframework.demo.funwithfunctional.owner.OwnerRepository;
import org.springframework.demo.funwithfunctional.pet.PetHandler;
import org.springframework.demo.funwithfunctional.pet.PetRepository;
import org.springframework.demo.funwithfunctional.security.RandomSecurityManager;
import org.springframework.demo.funwithfunctional.security.SecurityManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RenderingResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class FunWithFunctionalConfiguration {

	@Bean
	public PetHandler personHandler(PetRepository repository) {
		return new PetHandler(repository);
	}

	@Bean
	public OwnerHandler ownerHandler(OwnerRepository repository) {
		return new OwnerHandler(repository);
	}

	@Bean
	public SecurityManager securityManager() {
		return new RandomSecurityManager();
	}

	@SuppressWarnings("unchecked")
	@Bean
	public RouterFunction<?> mainRoute(PetHandler petHandler, OwnerHandler ownerHandler,
			SecurityManager securityManager) {

		return petHandler.petsRouter().andOther(ownerHandler.ownerRouter())
				.filter(new SecurityFilterFunction(securityManager));
//				.filter(new DateFilterFunction());
	}

	private static class SecurityFilterFunction<T extends ServerResponse> implements HandlerFilterFunction<T, ServerResponse> {

		private final SecurityManager securityManager;

		public SecurityFilterFunction(SecurityManager securityManager) {
			this.securityManager = securityManager;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Mono<ServerResponse> filter(ServerRequest request,
				HandlerFunction<T> next) {
			if (this.securityManager.hasAccess(request)) {
				return (Mono<ServerResponse>) next.handle(request);
			}
			else {
				return ServerResponse.status(HttpStatus.FORBIDDEN).build();
			}
		}
	}

	private static class DateFilterFunction<T extends ServerResponse> implements HandlerFilterFunction<T, ServerResponse> {

		@Override
		public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<T> next) {
			return next.handle(request)
					.flatMap(serverResponse -> {
						if (serverResponse instanceof RenderingResponse) {
							RenderingResponse renderingResponse =
									(RenderingResponse) serverResponse;
							return RenderingResponse.from(renderingResponse)
									.modelAttribute("date", Instant.now()).build();
						}
						else {
							return Mono.just(serverResponse);
						}
					});
		}
	}
}
