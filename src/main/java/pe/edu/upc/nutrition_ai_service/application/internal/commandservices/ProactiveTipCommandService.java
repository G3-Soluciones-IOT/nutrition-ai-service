package pe.edu.upc.nutrition_ai_service.application.internal.commandservices;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.DailyWaterIntakeResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.GoalResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingGoalResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingProgressResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.TrackingResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.UserProfileResource;
import pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl.resources.WeeklyWaterIntakeResource;
import pe.edu.upc.nutrition_ai_service.domain.model.aggregates.HomeTip;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.NutritionAiContext;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.ProactiveTipPeriod;
import pe.edu.upc.nutrition_ai_service.domain.services.HomeTipRepository;
import pe.edu.upc.nutrition_ai_service.domain.services.ProactiveTipGenerationService;
import pe.edu.upc.nutrition_ai_service.infrastructure.clients.GoalsClient;
import pe.edu.upc.nutrition_ai_service.infrastructure.clients.IamClient;
import pe.edu.upc.nutrition_ai_service.infrastructure.clients.ProfilesClient;
import pe.edu.upc.nutrition_ai_service.infrastructure.clients.TrackingClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
public class ProactiveTipCommandService {
    private static final Logger log = LoggerFactory.getLogger(ProactiveTipCommandService.class);

    private final IamClient iamClient;
    private final ProfilesClient profilesClient;
    private final TrackingClient trackingClient;
    private final GoalsClient goalsClient;
    private final ProactiveTipGenerationService generationService;
    private final HomeTipRepository homeTipRepository;

    public ProactiveTipCommandService(
            IamClient iamClient,
            ProfilesClient profilesClient,
            TrackingClient trackingClient,
            GoalsClient goalsClient,
            ProactiveTipGenerationService generationService,
            HomeTipRepository homeTipRepository
    ) {
        this.iamClient = iamClient;
        this.profilesClient = profilesClient;
        this.trackingClient = trackingClient;
        this.goalsClient = goalsClient;
        this.generationService = generationService;
        this.homeTipRepository = homeTipRepository;
    }

    @Transactional
    public void generateTipsForAllUsers(ProactiveTipPeriod period) {
        var users = iamClient.getUsers();
        for (var user : users) {
            try {
                generateTipForUser(user.id(), period);
            } catch (Exception exception) {
                log.warn("Could not generate AI tip for user {}", user.id(), exception);
            }
        }
    }

    @Transactional
    public HomeTip generateTipForUser(Long userId, ProactiveTipPeriod period) {
        UserProfileResource profile = fetchOrNull(() -> profilesClient.getUserProfileByUserId(userId), "profile", userId);
        TrackingResource tracking = fetchOrNull(() -> trackingClient.getTrackingByUserId(userId), "tracking", userId);
        TrackingProgressResource progress = fetchOrNull(() -> trackingClient.getProgressByUserId(userId), "progress", userId);
        TrackingGoalResource trackingGoal = fetchOrNull(() -> trackingClient.getTrackingGoalByUserId(userId), "tracking goal", userId);
        DailyWaterIntakeResource todayWater = fetchOrNull(() -> trackingClient.getTodayWaterIntake(userId), "today water", userId);
        WeeklyWaterIntakeResource weeklyWater = fetchOrNull(() -> trackingClient.getWeeklyWaterSummary(userId), "weekly water", userId);
        GoalResource goal = fetchOrNull(() -> goalsClient.getGoalByUserId(userId), "goal", userId);

        var context = new NutritionAiContext(userId, period, profile, tracking, progress, trackingGoal, todayWater, weeklyWater, goal);
        var message = generationService.generateHomeTip(context);

        homeTipRepository.deleteByUserId(userId);
        return homeTipRepository.save(new HomeTip(userId, message, period, LocalDateTime.now(), LocalDate.now()));
    }

    private <T> T fetchOrNull(Supplier<T> supplier, String resourceName, Long userId) {
        try {
            return supplier.get();
        } catch (FeignException.NotFound exception) {
            log.debug("No {} found for user {}", resourceName, userId);
            return null;
        } catch (FeignException exception) {
            log.warn("Could not fetch {} for user {}. Status: {}", resourceName, userId, exception.status());
            return null;
        }
    }
}
