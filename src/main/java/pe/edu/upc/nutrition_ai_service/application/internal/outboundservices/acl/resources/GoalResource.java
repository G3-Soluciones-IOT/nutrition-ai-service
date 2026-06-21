package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

public record GoalResource(
        Long userId,
        String objective,
        Double targetWeightKg,
        String pace,
        String dietPreset,
        Integer proteinPct,
        Integer carbsPct,
        Integer fatPct
) {
}
