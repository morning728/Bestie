package com.morning.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "security/v1/auth/register",
            "security/v1/auth/authenticate",
            "security/v1/auth/refresh-token",
            "api/v1/auth/register",
            "api/v1/auth/authenticate",
            "api/v1/auth/refresh-token",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
