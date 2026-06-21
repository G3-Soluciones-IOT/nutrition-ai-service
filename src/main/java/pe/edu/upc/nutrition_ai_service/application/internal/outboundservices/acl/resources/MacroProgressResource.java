package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record MacroProgressResource(Double consumed, Double target, Double remaining, Double progressPercentage) {
}
