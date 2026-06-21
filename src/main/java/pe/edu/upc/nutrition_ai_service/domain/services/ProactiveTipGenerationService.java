package pe.edu.upc.nutrition_ai_service.domain.services;

import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.NutritionAiContext;

public interface ProactiveTipGenerationService {
    String generateHomeTip(NutritionAiContext context);
}
