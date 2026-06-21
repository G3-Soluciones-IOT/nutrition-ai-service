FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=default
EXPOSE 8091
ENTRYPOINT ["java","-jar","/app/app.jar"]
