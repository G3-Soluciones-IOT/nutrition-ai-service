package pe.edu.upc.nutrition_ai_service.infrastructure.vertexai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vertex-ai")
public record VertexAiProperties(String projectId, String location, String model) {
}
