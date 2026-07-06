package pe.edu.upc.nutrition_ai_service.infrastructure.vertexai;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pe.edu.upc.nutrition_ai_service.domain.model.valueobjects.NutritionAiContext;
import pe.edu.upc.nutrition_ai_service.domain.services.ProactiveTipGenerationService;

@Service
@EnableConfigurationProperties(VertexAiProperties.class)
public class GeminiProactiveTipService implements ProactiveTipGenerationService {
    private final VertexAiProperties properties;

    public GeminiProactiveTipService(VertexAiProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateHomeTip(NutritionAiContext context) {
        if (!StringUtils.hasText(properties.projectId())) {
            return "Registra tus comidas y agua de hoy para recibir un consejo personalizado en tu Home.";
        }

        String prompt = buildPrompt(context);
        try (VertexAI vertexAI = new VertexAI(properties.projectId(), properties.location())) {
            GenerativeModel model = new GenerativeModel(properties.model(), vertexAI);
            GenerateContentResponse response = model.generateContent(prompt);
            return normalize(ResponseHandler.getText(response));
        } catch (Exception exception) {
            return "Sigue registrando tus comidas y agua; con esos datos podremos darte una guia mas precisa hoy.";
        }
    }

    private String buildPrompt(NutritionAiContext context) {
        return """
                Eres un asistente de bienestar nutricional para una app fitness.

                Genera UN SOLO mensaje proactivo para mostrar en la pantalla Home.

                Tarea:
                - Analiza el progreso del usuario comparando consumo actual vs metas.
                - Identifica el principal desbalance nutricional del dia.
                - Da una recomendacion practica, especifica y accionable.
                - Prioriza comida real, porciones simples y habitos sostenibles.
                - Si faltan proteinas, sugiere una opcion practica compatible con su dieta.
                - Si faltan carbohidratos, sugiere una fuente adecuada segun el momento del dia.
                - Si hay exceso calorico, recomienda ajustar la siguiente comida sin castigar ni restringir agresivamente.
                - Si el agua esta baja, sugiere una estrategia concreta para completarla.
                - Si no hay datos suficientes, pide registrar comida o agua.

                Reglas de respuesta:
                - Tono cercano, motivador y breve.
                - Maximo 2 frases.
                - No afirmes ser nutricionista.
                - No des diagnosticos medicos.
                - No indiques tratamientos.
                - No recomiendes alimentos incompatibles con alergias, restricciones o dieta.
                - Usa unicamente los datos proporcionados.
                - Al mediodia enfocate en avance parcial; por la noche enfocate en cierre del dia.

                Datos del usuario:
                %s

                Devuelve solo el mensaje final, sin JSON ni explicacion adicional.
                """.formatted(context.toPromptText());
    }

    private String normalize(String message) {
        if (!StringUtils.hasText(message)) {
            return "Registra tus comidas y agua de hoy para recibir un consejo personalizado en tu Home.";
        }
        String cleaned = message.trim().replaceAll("\\s+", " ");
        return cleaned.length() <= 500 ? cleaned : cleaned.substring(0, 500);
    }
}
