package pe.edu.upc.nutrition_ai_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.UserResource;

import java.util.List;

@FeignClient(name = "iam-service", path = "/api/v1/users")
public interface IamClient {
    @GetMapping
    List<UserResource> getUsers();

    @GetMapping("/{userId}")
    UserResource getUserById(@PathVariable Long userId);
}
