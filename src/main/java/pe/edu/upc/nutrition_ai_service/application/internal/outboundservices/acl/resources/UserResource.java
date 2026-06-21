package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

import java.util.List;

public record UserResource(Long id, String username, List<String> roles) {
}
