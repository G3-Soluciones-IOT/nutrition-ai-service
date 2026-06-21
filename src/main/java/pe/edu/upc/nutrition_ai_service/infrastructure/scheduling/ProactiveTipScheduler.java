package pe.edu.upc.nutrition_ai_service.infrastructure.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.upc.nutrition_ai_service.application.internal.commandservices.ProactiveTipCommandService;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.ProactiveTipPeriod;

@Component
public class ProactiveTipScheduler {
    private static final Logger log = LoggerFactory.getLogger(ProactiveTipScheduler.class);

    private final ProactiveTipCommandService proactiveTipCommandService;

    public ProactiveTipScheduler(ProactiveTipCommandService proactiveTipCommandService) {
        this.proactiveTipCommandService = proactiveTipCommandService;
    }

    @Scheduled(cron = "${nutrition-ai.noon-cron}", zone = "${nutrition-ai.timezone}")
    public void generateNoonTips() {
        log.info("Generating noon proactive nutrition tips");
        proactiveTipCommandService.generateTipsForAllUsers(ProactiveTipPeriod.NOON);
    }

    @Scheduled(cron = "${nutrition-ai.night-cron}", zone = "${nutrition-ai.timezone}")
    public void generateNightTips() {
        log.info("Generating night proactive nutrition tips");
        proactiveTipCommandService.generateTipsForAllUsers(ProactiveTipPeriod.NIGHT);
    }
}
