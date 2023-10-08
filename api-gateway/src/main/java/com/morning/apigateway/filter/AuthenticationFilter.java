package com.morning.apigateway.filter;


import com.morning.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.Collections;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestTemplate template;
    private JwtUtil jwtUtil;
    @Value("${services.path.security-validate-user}")
    private String userValidationPath;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Access Denied!");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
//                    //REST call to AUTH service



                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    headers.add(
                            HttpHeaders.AUTHORIZATION,
                            exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)
                    );

                    HttpEntity<String> entity = new HttpEntity<>("body", headers);

                    template.exchange(userValidationPath, HttpMethod.GET, entity, String.class);

                    //template.getForObject("http://security//validate?token" + authHeader, String.class);
                    //jwtUtil.validateToken(authHeader);

                } catch (Exception e) {
                    System.out.println(e.toString());
                    return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
//                    throw new RuntimeException("Access Denied!");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}