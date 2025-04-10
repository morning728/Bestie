package com.morning.apigateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestTemplate template;

    @Value("${services.path.security-validate-user}")
    private String userValidationPath;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // Проверяем, защищен ли эндпоинт (isSecured)
            if (validator.isSecured.test(exchange.getRequest())) {
                // Проверяем, есть ли токен в заголовках
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return sendUnauthorizedResponse(exchange, "The token is missing");
                }

                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    headers.add(HttpHeaders.AUTHORIZATION, exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));

                    HttpEntity<String> entity = new HttpEntity<>("body", headers);

                    ResponseEntity<String> response = template.exchange(userValidationPath, HttpMethod.GET, entity, String.class);

                    if (response.getStatusCode() != HttpStatus.OK) {
                        return sendUnauthorizedResponse(exchange, "The token is invalid");
                    }
                } catch (Exception e) {
                    return sendUnauthorizedResponse(exchange, "Token verification error");
                }
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> sendUnauthorizedResponse(org.springframework.web.server.ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorJson = "{\"error\": \"" + message + "\"}";

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(errorJson.getBytes())));
    }

    public static class Config {
    }
}