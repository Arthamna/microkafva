package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    
    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder, @Value("${auth.service.url}") String authServiceUrl) {

        this.webClient = webClientBuilder
            .baseUrl(authServiceUrl)
            .build();
    }

    @Override
    // apply for each request
    public GatewayFilter apply(Object config) {
        // exchange is request object
        // chain manages filter chain that already exist
        return (exchange, chain) -> {
            // get auth header
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                // just return 
                return exchange.getResponse().setComplete();
            } 

            // using webclient validate token  
            return webClient.get()
                    .uri("/validate") // add base uri with validate
                    .header("Authorization", token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange)) // tell chain to continue the exchange
                    ;
        };
    }

}
