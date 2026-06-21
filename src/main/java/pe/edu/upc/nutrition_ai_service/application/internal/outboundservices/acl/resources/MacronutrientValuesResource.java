package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record MacronutrientValuesResource(
        Long id,
        Double calories,
        Double carbs,
        Double proteins,
        Double fats
) {
}
