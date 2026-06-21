package pe.edu.upc.nutrition_ai_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.audit.AuditEventsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {AuditEventsEndpointAutoConfiguration.class})
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class NutritionAiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NutritionAiServiceApplication.class, args);
    }
}
