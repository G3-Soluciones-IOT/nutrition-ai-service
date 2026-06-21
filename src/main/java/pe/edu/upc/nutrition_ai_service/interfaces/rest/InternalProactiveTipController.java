package pe.edu.upc.nutrition_ai_service.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.nutrition_ai_service.application.internal.commandservices.ProactiveTipCommandService;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.ProactiveTipPeriod;
import pe.edu.upc.nutrition_ai_service.infrastructure.security.InternalRequestValidator;

@RestController
@RequestMapping("/internal/api/v1/ai/proactive-tips")
public class InternalProactiveTipController {
    private final ProactiveTipCommandService proactiveTipCommandService;
    private final InternalRequestValidator internalRequestValidator;

    public InternalProactiveTipController(
            ProactiveTipCommandService proactiveTipCommandService,
            InternalRequestValidator internalRequestValidator
    ) {
        this.proactiveTipCommandService = proactiveTipCommandService;
        this.internalRequestValidator = internalRequestValidator;
    }

    @PostMapping("/run")
    public ResponseEntity<Void> run(
            @RequestParam ProactiveTipPeriod period,
            @RequestHeader(value = "X-Internal-Token", required = false) String internalToken
    ) {
        internalRequestValidator.validate(internalToken);
        proactiveTipCommandService.generateTipsForAllUsers(period);
        return ResponseEntity.accepted().build();
    }
}
