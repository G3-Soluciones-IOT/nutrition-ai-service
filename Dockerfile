FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build --chown=1001:0 /workspace/target/*.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=default
USER 1001
EXPOSE 8091
ENTRYPOINT ["java","-jar","/app/app.jar"]
