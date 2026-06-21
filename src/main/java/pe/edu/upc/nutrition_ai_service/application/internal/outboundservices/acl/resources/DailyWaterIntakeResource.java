package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

import java.time.LocalDate;

public record DailyWaterIntakeResource(Long userId, LocalDate date, Integer totalWaterMl) {
}
