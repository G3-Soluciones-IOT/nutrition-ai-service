package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record TrackingGoalResource(Long id, Long userId, MacronutrientValuesResource targetMacros) {
}
