# Étape 1 : build
FROM maven:3.9.7-eclipse-temurin-22-jdk AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : runtime
FROM eclipse-temurin:22-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose le port par défaut pour Spring Boot
EXPOSE 8080

# Démarre l’application
ENTRYPOINT ["java", "-jar", "app.jar"]
