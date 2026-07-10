package pe.edu.upc.nutrition_ai_service.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

@Configuration
public class ResourceServerConfig {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${legacy.jwt.issuer:iam-service}") String legacyJwtIssuer
    ) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/internal/api/v1/ai/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ai/home-tip/**").access((authentication, context) ->
                        hasReadAiOrLegacyJwt(authentication, context, legacyJwtIssuer))
                .anyRequest().authenticated());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(NutritionAiJwtAuthenticationConverter.jwtAuthenticationConverter())));
        return http.build();
    }

    private AuthorizationDecision hasReadAiOrLegacyJwt(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext context,
            String legacyJwtIssuer) {
        var authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(
                hasAuthority(authentication, "read:ai")
                        || hasAuthority(authentication, "ROLE_SERVICE")
                        || isLegacyJwt(authentication, legacyJwtIssuer));
    }

    private boolean hasAuthority(Authentication authentication, String expectedAuthority) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> expectedAuthority.equals(authority.getAuthority()));
    }

    private boolean isLegacyJwt(Authentication authentication, String legacyJwtIssuer) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return false;
        }
        var issuer = jwtAuthentication.getToken().getClaimAsString("iss");
        return legacyJwtIssuer.equals(issuer);
    }
}
