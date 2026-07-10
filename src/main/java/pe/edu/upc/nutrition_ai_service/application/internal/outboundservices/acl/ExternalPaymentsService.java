package pe.edu.upc.nutrition_ai_service.application.internal.outboundservices.acl;

import feign.FeignException;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutrition_ai_service.infrastructure.clients.PaymentsClient;

@Service
public class ExternalPaymentsService {
    private final PaymentsClient paymentsClient;

    public ExternalPaymentsService(PaymentsClient paymentsClient) {
        this.paymentsClient = paymentsClient;
    }

    public boolean hasPremiumAccess(Long userId) {
        try {
            var access = paymentsClient.hasPremiumAccess(userId);
            return access != null && access.premiumAccess();
        } catch (FeignException exception) {
            return false;
        }
    }
}
