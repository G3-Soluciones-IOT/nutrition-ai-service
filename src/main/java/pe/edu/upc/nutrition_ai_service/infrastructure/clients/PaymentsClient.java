package pe.edu.upc.nutrition_ai_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.PremiumAccessResource;

@FeignClient(name = "payments-service", path = "/api/v1/internal/subscriptions")
public interface PaymentsClient {
    @GetMapping("/users/{userId}/premium-access")
    PremiumAccessResource hasPremiumAccess(@PathVariable Long userId);
}
