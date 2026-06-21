package pe.edu.upc.nutrition_ai_service.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.ProactiveTipPeriod;
import pe.edu.upc.nutrition_ai_service.shared.domain.model.entities.AuditableModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_tips")
public class HomeTip extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProactiveTipPeriod period;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private LocalDate tipDate;

    public HomeTip() {
    }

    public HomeTip(Long userId, String message, ProactiveTipPeriod period, LocalDateTime generatedAt, LocalDate tipDate) {
        this.userId = userId;
        this.message = message;
        this.period = period;
        this.generatedAt = generatedAt;
        this.tipDate = tipDate;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public ProactiveTipPeriod getPeriod() {
        return period;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public LocalDate getTipDate() {
        return tipDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPeriod(ProactiveTipPeriod period) {
        this.period = period;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setTipDate(LocalDate tipDate) {
        this.tipDate = tipDate;
    }
}
