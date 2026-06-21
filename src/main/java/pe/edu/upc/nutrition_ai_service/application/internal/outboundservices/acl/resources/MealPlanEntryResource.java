package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record MealPlanEntryResource(Long id, Long recipeId, String mealPlanType, Integer dayNumber) {
}
