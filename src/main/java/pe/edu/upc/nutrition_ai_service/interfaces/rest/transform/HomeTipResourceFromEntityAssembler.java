package pe.edu.upc.nutrition_ai_service.interfaces.rest.transform;

import pe.edu.upc.nutrition_ai_service.domain.model.aggregates.HomeTip;
import pe.edu.upc.nutrition_ai_service.interfaces.rest.resources.HomeTipResource;

public class HomeTipResourceFromEntityAssembler {
    private HomeTipResourceFromEntityAssembler() {
    }

    public static HomeTipResource toResourceFromEntity(HomeTip entity) {
        return new HomeTipResource(entity.getUserId(), entity.getMessage(), entity.getPeriod(), entity.getGeneratedAt());
    }
}
