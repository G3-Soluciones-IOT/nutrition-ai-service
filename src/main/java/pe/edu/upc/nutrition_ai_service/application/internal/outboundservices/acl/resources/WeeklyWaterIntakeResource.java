package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record WeeklyWaterIntakeResource(Long userId, Integer averageWaterMl, Integer daysBelowGoal, Integer bestDayMl) {
}
