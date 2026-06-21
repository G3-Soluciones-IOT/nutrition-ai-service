# nutrition-ai-service

Microservicio Spring Boot para generar tips nutricionales proactivos para el Home de JameoFit.

## Stack

- Java 25
- Spring Boot 3.5.7
- Spring Cloud 2025.0.0
- PostgreSQL 16
- Eureka Client
- Spring Cloud Config Client
- OpenFeign + LoadBalancer
- Spring Security OAuth2 Resource Server
- Vertex AI Gemini

## Endpoints

- `GET /api/v1/ai/home-tip/{userId}`: obtiene el ultimo tip generado para un usuario. Requiere JWT.
- `POST /internal/api/v1/ai/proactive-tips/run?period=NOON`: dispara generacion interna para mediodia.
- `POST /internal/api/v1/ai/proactive-tips/run?period=NIGHT`: dispara generacion interna para noche.
- `GET /actuator/health`: healthcheck.
- `GET /v3/api-docs`: OpenAPI.

Los endpoints internos requieren el header:

```http
X-Internal-Token: internal-service-secret-key
```

## Ejecucion local

Levantar solo PostgreSQL del servicio:

```bash
docker compose -f docker-compose.local.yml --env-file .env.example up -d
```

Compilar:

```bash
./mvnw clean package
```

Ejecutar:

```bash
./mvnw spring-boot:run
```

Sin `GCP_PROJECT_ID`, el servicio usa mensajes fallback y no llama a Vertex AI.

## Variables principales

Ver `.env.example` para los valores esperados. Las mas importantes son:

- `NUTRITION_AI_DB_HOST`
- `NUTRITION_AI_DB_PORT`
- `NUTRITION_AI_DB_NAME`
- `NUTRITION_AI_DB_USER`
- `NUTRITION_AI_DB_PASSWORD`
- `CONFIG_SERVER_URI`
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
- `IAM_JWKS_URI`
- `NUTRITION_AI_INTERNAL_TOKEN`
- `GCP_PROJECT_ID`
- `GCP_LOCATION`
- `GEMINI_MODEL`
