package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record TrackingProgressResource(
        MacroProgressResource calories,
        MacroProgressResource carbs,
        MacroProgressResource proteins,
        MacroProgressResource fats
) {
}
