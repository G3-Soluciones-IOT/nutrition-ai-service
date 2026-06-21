package pe.edu.upc.nutrition_ai_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.DailyWaterIntakeResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingGoalResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingProgressResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.WeeklyWaterIntakeResource;

@FeignClient(name = "tracking-service")
public interface TrackingClient {
    @GetMapping("/api/v1/tracking/user/{userId}")
    TrackingResource getTrackingByUserId(@PathVariable Long userId);

    @GetMapping("/api/v1/tracking/user/{userId}/progress")
    TrackingProgressResource getProgressByUserId(@PathVariable Long userId);

    @GetMapping("/api/v1/tracking-goals/user/{userId}")
    TrackingGoalResource getTrackingGoalByUserId(@PathVariable Long userId);

    @GetMapping("/api/v1/water-intakes/user/{userId}/today")
    DailyWaterIntakeResource getTodayWaterIntake(@PathVariable Long userId);

    @GetMapping("/api/v1/water-intakes/user/{userId}/weekly-summary")
    WeeklyWaterIntakeResource getWeeklyWaterSummary(@PathVariable Long userId);
}
