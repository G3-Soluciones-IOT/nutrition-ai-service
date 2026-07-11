package pe.edu.upc.nutrition_ai_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.UserProfileResource;

@FeignClient(name = "profiles-service", path = "/api/v1/user-profiles")
public interface ProfilesClient {
    @GetMapping("/by-user/{userId}")
    UserProfileResource getUserProfileByUserId(@PathVariable Long userId);
}
