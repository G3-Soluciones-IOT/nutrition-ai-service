package pe.edu.upc.nutrition_ai_service.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.nutrition_ai_service.domain.model.aggregates.HomeTip;
import pe.edu.upc.nutrition_ai_service.domain.services.HomeTipRepository;

import java.util.Optional;

@Repository
public interface HomeTipJpaRepository extends JpaRepository<HomeTip, Long>, HomeTipRepository {
    Optional<HomeTip> findTopByUserIdOrderByGeneratedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
