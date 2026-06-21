package pe.edu.upc.nutrition_ai_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.GoalResource;

@FeignClient(name = "goals-service", path = "/api/v1/goals")
public interface GoalsClient {
    @GetMapping
    GoalResource getGoalByUserId(@RequestParam Long userId);
}
