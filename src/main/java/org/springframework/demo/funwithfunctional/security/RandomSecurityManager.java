package org.springframework.demo.funwithfunctional.security;

import java.security.Principal;
import java.util.Random;

import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * @author Arjen Poutsma
 */
public class RandomSecurityManager implements SecurityManager {

	private Random rnd = new Random();

	@Override
	public boolean hasAccess(ServerRequest request) {
		return this.rnd.nextBoolean();
	}
}
