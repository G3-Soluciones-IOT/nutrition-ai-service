package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources;

import java.util.List;

public record UserProfileResource(
        Integer id,
        String gender,
        Double height,
        Double weight,
        Integer userScore,
        String birthDate,
        Long activityLevelId,
        String activityLevelName,
        Integer objectiveId,
        String objectiveName,
        List<String> allergyNames
) {
}
