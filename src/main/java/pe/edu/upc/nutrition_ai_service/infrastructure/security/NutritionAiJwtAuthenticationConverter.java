package pe.edu.upc.nutrition_ai_service.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class NutritionAiJwtAuthenticationConverter {

    private NutritionAiJwtAuthenticationConverter() {
    }

    static JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(new NutritionAiGrantedAuthoritiesConverter());
        return authenticationConverter;
    }

    private static final class NutritionAiGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Set<String> authorities = new LinkedHashSet<>();
            addStringListClaim(authorities, jwt, "permissions");
            addScopeAuthorities(authorities, jwt.getClaimAsString("scope"));
            addStringListClaim(authorities, jwt, "authorities");
            addStringListClaim(authorities, jwt, "roles");
            return authorities.stream()
                    .filter(StringUtils::hasText)
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        }

        private void addStringListClaim(Set<String> authorities, Jwt jwt, String claimName) {
            List<String> claimValues = jwt.getClaimAsStringList(claimName);
            if (claimValues != null) {
                authorities.addAll(claimValues);
            }
        }

        private void addScopeAuthorities(Set<String> authorities, String scopes) {
            if (!StringUtils.hasText(scopes)) {
                return;
            }
            for (String scope : scopes.split(" ")) {
                if (StringUtils.hasText(scope)) {
                    authorities.add("SCOPE_" + scope);
                }
            }
        }
    }
}
