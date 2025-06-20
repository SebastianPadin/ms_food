package pe.edu.vallegrande.foodcost.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.util.context.Context;

@Configuration
public class AuthContextWebFilter {

    @Bean
    public WebFilter jwtTokenPropagationFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) ->
                ReactiveSecurityContextHolder.getContext()
                        .map(securityContext -> {
                            AbstractAuthenticationToken auth = (AbstractAuthenticationToken) securityContext.getAuthentication();
                            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                                String tokenValue = jwtAuth.getToken().getTokenValue();
                                return Context.of("Authorization", tokenValue);
                            }
                            return Context.empty();
                        })
                        .flatMap(context -> chain.filter(exchange).contextWrite(context));
    }
}
