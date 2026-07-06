# Guia para habilitar Gemini en nutrition-ai-service

Esta guia se centra en lo necesario para que `nutrition-ai-service` use Gemini mediante Google Vertex AI cuando corre en una VM de Google Cloud.

El servicio llama a Gemini en `GeminiProactiveTipService`. Si `GCP_PROJECT_ID` esta vacio, no llama a Vertex AI y devuelve un mensaje fallback. Por eso la configuracion critica es: proyecto GCP, API habilitada, permisos, credenciales y variables de entorno.

## 1. Que hace el servicio con Gemini

Cuando se genera un tip, el servicio:

1. Recibe datos del usuario desde otros microservicios.
2. Arma un prompt nutricional.
3. Crea un cliente:

```java
new VertexAI(properties.projectId(), properties.location())
```

4. Usa el modelo configurado:

```java
new GenerativeModel(properties.model(), vertexAI)
```

5. Guarda la respuesta en `home_tips`.

Las properties vienen de:

```properties
vertex-ai.project-id=${GCP_PROJECT_ID:}
vertex-ai.location=${GCP_LOCATION:us-central1}
vertex-ai.model=${GEMINI_MODEL:gemini-2.5-flash}
```

## 2. Requisitos en Google Cloud

En el proyecto de Google Cloud debes tener:

- Vertex AI API habilitada.
- Un proyecto GCP con billing activo.
- Una service account para la VM o para el contenedor.
- Permisos suficientes para invocar modelos de Vertex AI.
- La VM con salida a internet o acceso privado configurado hacia Google APIs.

Habilitar API:

```bash
gcloud services enable aiplatform.googleapis.com --project TU_PROYECTO
```

Rol recomendado para la service account:

```text
Vertex AI User
```

En IAM aparece como:

```text
roles/aiplatform.user
```

## 3. Opcion recomendada: service account asignada a la VM

Esta es la forma mas limpia si el servicio corre en Compute Engine.

### Paso 1: Crear service account

```bash
gcloud iam service-accounts create nutrition-ai-vm \
  --display-name="Nutrition AI VM" \
  --project TU_PROYECTO
```

### Paso 2: Dar permiso de Vertex AI

```bash
gcloud projects add-iam-policy-binding TU_PROYECTO \
  --member="serviceAccount:nutrition-ai-vm@TU_PROYECTO.iam.gserviceaccount.com" \
  --role="roles/aiplatform.user"
```

### Paso 3: Asociar la service account a la VM

Si la VM ya existe:

```bash
gcloud compute instances set-service-account NOMBRE_VM \
  --zone ZONA_VM \
  --service-account nutrition-ai-vm@TU_PROYECTO.iam.gserviceaccount.com \
  --scopes=https://www.googleapis.com/auth/cloud-platform
```

Con esta opcion no necesitas montar un JSON ni configurar `GOOGLE_APPLICATION_CREDENTIALS`. La libreria de Google usa Application Default Credentials desde el metadata server de la VM.

## 4. Opcion alternativa: JSON de service account

Usala solo si no puedes asignar una service account a la VM.

### Paso 1: Crear key JSON

```bash
gcloud iam service-accounts keys create vertex-ai-service-account.json \
  --iam-account=nutrition-ai-vm@TU_PROYECTO.iam.gserviceaccount.com \
  --project TU_PROYECTO
```

### Paso 2: Guardar el JSON fuera del repo

Ejemplo en la VM:

```bash
sudo mkdir -p /opt/jameofit/secrets
sudo mv vertex-ai-service-account.json /opt/jameofit/secrets/
sudo chmod 600 /opt/jameofit/secrets/vertex-ai-service-account.json
```

### Paso 3: Montarlo en Docker

En el servicio `nutrition-ai-service` del compose:

```yaml
volumes:
  - /opt/jameofit/secrets/vertex-ai-service-account.json:/app/secrets/vertex-ai-service-account.json:ro
```

Y en el `.env`:

```env
GOOGLE_APPLICATION_CREDENTIALS=/app/secrets/vertex-ai-service-account.json
```

No subas este JSON al repositorio.

## 5. Variables necesarias para activar Gemini

En el `.env` de la VM:

```env
GCP_PROJECT_ID=tu-proyecto-gcp
GCP_LOCATION=us-central1
GEMINI_MODEL=gemini-2.5-flash
GOOGLE_CLOUD_PROJECT=tu-proyecto-gcp
```

Si usas JSON:

```env
GOOGLE_APPLICATION_CREDENTIALS=/app/secrets/vertex-ai-service-account.json
```

Si usas service account asociada a la VM, normalmente no pongas `GOOGLE_APPLICATION_CREDENTIALS`.

Importante: `GCP_PROJECT_ID` no puede estar vacio. Si esta vacio, el codigo devuelve:

```text
Registra tus comidas y agua de hoy para recibir un consejo personalizado en tu Home.
```

y no llama a Gemini.

## 6. Compose minimo para credenciales

Con service account asociada a la VM, basta con pasar variables:

```yaml
environment:
  GCP_PROJECT_ID: ${GCP_PROJECT_ID}
  GCP_LOCATION: ${GCP_LOCATION:-us-central1}
  GEMINI_MODEL: ${GEMINI_MODEL:-gemini-2.5-flash}
  GOOGLE_CLOUD_PROJECT: ${GOOGLE_CLOUD_PROJECT}
```

Con JSON:

```yaml
environment:
  GCP_PROJECT_ID: ${GCP_PROJECT_ID}
  GCP_LOCATION: ${GCP_LOCATION:-us-central1}
  GEMINI_MODEL: ${GEMINI_MODEL:-gemini-2.5-flash}
  GOOGLE_CLOUD_PROJECT: ${GOOGLE_CLOUD_PROJECT}
  GOOGLE_APPLICATION_CREDENTIALS: /app/secrets/vertex-ai-service-account.json
volumes:
  - /opt/jameofit/secrets/vertex-ai-service-account.json:/app/secrets/vertex-ai-service-account.json:ro
```

## 7. Verificar credenciales desde la VM

Si usas service account de la VM:

```bash
gcloud auth list
gcloud config set project TU_PROYECTO
gcloud ai models list --region=us-central1
```

Si usas JSON:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/opt/jameofit/secrets/vertex-ai-service-account.json
gcloud auth application-default print-access-token
```

Si el token se genera, las credenciales existen. Si Vertex AI falla, revisa permisos o API habilitada.

## 8. Probar desde el contenedor

Levanta el servicio:

```bash
docker compose -f docker-compose.local.yml --env-file .env up -d --build
```

Revisa que las variables llegaron al contenedor:

```bash
docker exec nutrition-ai-service printenv GCP_PROJECT_ID
docker exec nutrition-ai-service printenv GCP_LOCATION
docker exec nutrition-ai-service printenv GEMINI_MODEL
```

Si usas JSON:

```bash
docker exec nutrition-ai-service ls -l /app/secrets/vertex-ai-service-account.json
docker exec nutrition-ai-service printenv GOOGLE_APPLICATION_CREDENTIALS
```

## 9. Probar generacion real

Dispara generacion:

```bash
curl -X POST "http://localhost:8091/internal/api/v1/ai/proactive-tips/run?period=NOON" \
  -H "X-Internal-Token: TU_TOKEN_INTERNO"
```

Luego consulta el tip:

```bash
curl "http://localhost:8091/api/v1/ai/home-tip/1" \
  -H "Authorization: Bearer TU_JWT"
```

Para saber si Gemini esta funcionando, el mensaje deberia ser especifico al contexto del usuario. Si ves textos genericos como "Registra tus comidas y agua..." o "Sigue registrando tus comidas y agua...", probablemente esta usando fallback.

## 10. Logs utiles

Ver logs:

```bash
docker logs -f nutrition-ai-service
```

Actualmente el codigo atrapa excepciones de Vertex AI y devuelve fallback sin imprimir el detalle. Si necesitas diagnostico fino en nube, conviene agregar un log en el `catch` de `GeminiProactiveTipService.generateHomeTip`.

Ejemplo recomendado:

```java
log.warn("Could not generate Gemini home tip for user {}", context.userId(), exception);
```

Para eso habria que agregar un `Logger` en la clase.

## 11. Errores comunes

### Siempre devuelve fallback de registro de comida/agua

Revisa:

- `GCP_PROJECT_ID` esta vacio.
- La variable no llego al contenedor.
- Estas usando otro `.env` al levantar Docker.

### Devuelve fallback de "guia mas precisa"

Ese fallback ocurre cuando intento llamar a Vertex AI, pero fallo. Revisa:

- Vertex AI API habilitada.
- Service account con `roles/aiplatform.user`.
- Credenciales disponibles para el contenedor.
- `GCP_LOCATION` soporta el modelo elegido.
- La VM tiene salida a Google APIs.

### Error de credenciales

Si usas service account de VM:

- Verifica que la VM tenga la service account correcta.
- Verifica el scope `cloud-platform`.
- Reinicia el contenedor despues de cambiar la VM.

Si usas JSON:

- Verifica que el archivo exista dentro del contenedor.
- Verifica `GOOGLE_APPLICATION_CREDENTIALS`.
- Verifica permisos del archivo en el host.

### Modelo no disponible

Revisa:

```env
GEMINI_MODEL=gemini-2.5-flash
GCP_LOCATION=us-central1
```

Si cambias de modelo, valida que este disponible en la region configurada.

## 12. Checklist Gemini

Antes de probar:

- `aiplatform.googleapis.com` esta habilitada.
- Billing esta activo en el proyecto.
- `GCP_PROJECT_ID` tiene el ID real del proyecto.
- `GCP_LOCATION` esta configurado.
- `GEMINI_MODEL` esta configurado.
- La VM o el contenedor tienen credenciales.
- La service account tiene `roles/aiplatform.user`.
- El contenedor recibe las variables.
- El servicio puede generar tips con datos de los otros microservicios.

Con todo eso, `nutrition-ai-service` deberia dejar de usar fallback y empezar a guardar respuestas generadas por Gemini.
