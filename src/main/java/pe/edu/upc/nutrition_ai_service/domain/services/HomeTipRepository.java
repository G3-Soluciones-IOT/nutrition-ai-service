package pe.edu.upc.nutrition_ai_service.domain.services;

import pe.edu.upc.nutrition_ai_service.domain.model.aggregates.HomeTip;

import java.util.Optional;

public interface HomeTipRepository {
    Optional<HomeTip> findTopByUserIdOrderByGeneratedAtDesc(Long userId);
    void deleteByUserId(Long userId);
    HomeTip save(HomeTip homeTip);
}
