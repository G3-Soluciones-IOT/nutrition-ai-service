package pe.edu.upc.nutrition_ai_service.interfaces.rest.resources;

import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.ProactiveTipPeriod;

import java.time.LocalDateTime;

public record HomeTipResource(Long userId, String message, ProactiveTipPeriod period, LocalDateTime generatedAt) {
}
