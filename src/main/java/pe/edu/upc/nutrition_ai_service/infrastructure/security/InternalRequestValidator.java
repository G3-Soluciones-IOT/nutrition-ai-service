package pe.edu.upc.nutrition_ai_service.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InternalRequestValidator {
    private final String expectedToken;

    public InternalRequestValidator(@Value("${nutrition-ai.internal-token}") String expectedToken) {
        this.expectedToken = expectedToken;
    }

    public void validate(String token) {
        if (!StringUtils.hasText(token) || !token.equals(expectedToken)) {
            throw new AccessDeniedException("Invalid internal token");
        }
    }
}
