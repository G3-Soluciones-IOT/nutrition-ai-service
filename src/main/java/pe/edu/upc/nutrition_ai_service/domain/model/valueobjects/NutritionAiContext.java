package pe.edu.upc.nutrition_ai_service.domain.model.valueobjects;

import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.DailyWaterIntakeResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.GoalResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingGoalResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingProgressResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.UserProfileResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.WeeklyWaterIntakeResource;

import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Collectors;

public record NutritionAiContext(
        Long userId,
        ProactiveTipPeriod period,
        UserProfileResource profile,
        TrackingResource tracking,
        TrackingProgressResource progress,
        TrackingGoalResource trackingGoal,
        DailyWaterIntakeResource todayWater,
        WeeklyWaterIntakeResource weeklyWater,
        GoalResource goal
) {
    public String toPromptText() {
        var allergies = Optional.ofNullable(profile)
                .map(UserProfileResource::allergyNames)
                .map(values -> values.isEmpty() ? "sin alergias registradas" : String.join(", ", values))
                .orElse("sin datos");
        var mealEntries = Optional.ofNullable(tracking)
                .map(TrackingResource::mealPlanEntries)
                .map(entries -> entries.stream()
                        .map(entry -> Optional.ofNullable(entry.mealPlanType()).orElse("comida"))
                        .distinct()
                        .collect(Collectors.joining(", ")))
                .filter(value -> !value.isBlank())
                .orElse("sin comidas registradas");

        return """
                Usuario: %s
                Periodo: %s
                Hora local aproximada: %s
                Objetivo del usuario: %s
                Nivel de actividad: %s
                Alergias: %s
                Tipo de dieta: %s

                Datos de hoy:
                - Agua consumida: %s ml
                - Calorias consumidas: %s kcal
                - Meta calorica: %s kcal
                - Proteinas consumidas: %s g
                - Meta de proteinas: %s g
                - Carbohidratos consumidos: %s g
                - Meta de carbohidratos: %s g
                - Grasas consumidas: %s g
                - Meta de grasas: %s g
                - Comidas registradas: %s

                Historial semanal:
                - Promedio de agua: %s ml
                - Dias bajo meta de agua: %s
                - Mejor dia de agua: %s ml
                """.formatted(
                userId,
                period,
                LocalTime.now().withSecond(0).withNano(0),
                value(profile, UserProfileResource::objectiveName),
                value(profile, UserProfileResource::activityLevelName),
                allergies,
                value(goal, GoalResource::dietPreset),
                value(todayWater, DailyWaterIntakeResource::totalWaterMl),
                consumedCalories(),
                progressTargetCalories(),
                consumedProteins(),
                targetProteins(),
                consumedCarbs(),
                targetCarbs(),
                consumedFats(),
                targetFats(),
                mealEntries,
                value(weeklyWater, WeeklyWaterIntakeResource::averageWaterMl),
                value(weeklyWater, WeeklyWaterIntakeResource::daysBelowGoal),
                value(weeklyWater, WeeklyWaterIntakeResource::bestDayMl)
        );
    }

    private String consumedCalories() {
        return Optional.ofNullable(tracking)
                .map(TrackingResource::consumedMacros)
                .map(macros -> macros.calories())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String consumedProteins() {
        return Optional.ofNullable(tracking)
                .map(TrackingResource::consumedMacros)
                .map(macros -> macros.proteins())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String consumedCarbs() {
        return Optional.ofNullable(tracking)
                .map(TrackingResource::consumedMacros)
                .map(macros -> macros.carbs())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String consumedFats() {
        return Optional.ofNullable(tracking)
                .map(TrackingResource::consumedMacros)
                .map(macros -> macros.fats())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String progressTargetCalories() {
        return Optional.ofNullable(progress)
                .map(TrackingProgressResource::calories)
                .map(value -> value.target())
                .map(String::valueOf)
                .orElseGet(this::targetCaloriesFromGoal);
    }

    private String targetCaloriesFromGoal() {
        return Optional.ofNullable(trackingGoal)
                .map(TrackingGoalResource::targetMacros)
                .map(macros -> macros.calories())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String targetProteins() {
        return Optional.ofNullable(trackingGoal)
                .map(TrackingGoalResource::targetMacros)
                .map(macros -> macros.proteins())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String targetCarbs() {
        return Optional.ofNullable(trackingGoal)
                .map(TrackingGoalResource::targetMacros)
                .map(macros -> macros.carbs())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private String targetFats() {
        return Optional.ofNullable(trackingGoal)
                .map(TrackingGoalResource::targetMacros)
                .map(macros -> macros.fats())
                .map(String::valueOf)
                .orElse("sin datos");
    }

    private static <T> String value(T source, java.util.function.Function<T, ?> mapper) {
        return Optional.ofNullable(source).map(mapper).map(String::valueOf).orElse("sin datos");
    }
}
