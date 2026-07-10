package pe.edu.upc.nutrition_ai_service.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.ExternalPaymentsService;
import pe.edu.upc.nutrition_ai_service.application.internal.queryservices.HomeTipQueryService;
import pe.edu.upc.nutrition_ai_service.interfaces.rest.transform.HomeTipResourceFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/ai", produces = "application/json")
public class HomeTipController {
    private final HomeTipQueryService homeTipQueryService;
    private final ExternalPaymentsService externalPaymentsService;

    public HomeTipController(HomeTipQueryService homeTipQueryService, ExternalPaymentsService externalPaymentsService) {
        this.homeTipQueryService = homeTipQueryService;
        this.externalPaymentsService = externalPaymentsService;
    }

    @GetMapping("/home-tip/{userId}")
    public ResponseEntity<?> getHomeTip(@PathVariable Long userId) {
        if (!externalPaymentsService.hasPremiumAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        return homeTipQueryService.getLastTipByUserId(userId)
                .map(HomeTipResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
