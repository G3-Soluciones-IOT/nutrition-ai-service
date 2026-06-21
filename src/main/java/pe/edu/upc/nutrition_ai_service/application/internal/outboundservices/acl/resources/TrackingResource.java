package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

import java.time.LocalDate;
import java.util.List;

public record TrackingResource(
        Integer id,
        Long userId,
        LocalDate date,
        MacronutrientValuesResource consumedMacros,
        List<MealPlanEntryResource> mealPlanEntries
) {
}
