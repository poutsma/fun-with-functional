package org.springframework.demo.funwithfunctional.security;

import java.security.Principal;

import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * @author Arjen Poutsma
 */
public interface SecurityManager {

	boolean hasAccess(ServerRequest request);

}
