package pe.edu.upc.nutrition_ai_service.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.nutrition_ai_service.domain.model.aggregates.HomeTip;
import pe.edu.upc.nutrition_ai_service.domain.services.HomeTipRepository;

import java.util.Optional;

@Service
public class HomeTipQueryService {
    private final HomeTipRepository homeTipRepository;

    public HomeTipQueryService(HomeTipRepository homeTipRepository) {
        this.homeTipRepository = homeTipRepository;
    }

    public Optional<HomeTip> getLastTipByUserId(Long userId) {
        return homeTipRepository.findTopByUserIdOrderByGeneratedAtDesc(userId);
    }
}
