package pe.edu.upc.nutrition_ai_service.infrastructure.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@Configuration
public class JwtDecoderConfiguration {

    @Bean
    public JwtDecoder auth0JwtDecoder(
            @Value("${auth0.issuer-uri}") String issuerUri,
            @Value("${auth0.audience}") String audience) {
        var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUriFromIssuer(issuerUri)).build();
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                new AudienceValidator(audience)
        ));
        return jwtDecoder;
    }

    @Bean
    public JwtDecoder legacyJwtDecoder(
            @Value("${legacy.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${legacy.jwt.issuer:iam-service}") String issuer) {
        var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return jwtDecoder;
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder(
            @Qualifier("auth0JwtDecoder") JwtDecoder auth0JwtDecoder,
            @Qualifier("legacyJwtDecoder") JwtDecoder legacyJwtDecoder,
            @Value("${legacy.jwt.enabled:true}") boolean legacyJwtEnabled) {
        return token -> {
            try {
                return auth0JwtDecoder.decode(token);
            } catch (JwtException exception) {
                if (legacyJwtEnabled) {
                    return legacyJwtDecoder.decode(token);
                }
                throw exception;
            }
        };
    }

    private static String jwkSetUriFromIssuer(String issuerUri) {
        var normalizedIssuer = issuerUri.endsWith("/") ? issuerUri : issuerUri + "/";
        return normalizedIssuer + ".well-known/jwks.json";
    }

    private static final class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;
        private final OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

        private AudienceValidator(String audience) {
            this.audience = audience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            var audiences = token.getAudience();
            if (audiences == null) {
                audiences = List.of();
            }
            return audiences.contains(audience)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(error);
        }
    }
}
