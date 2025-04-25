FROM openjdk:17-jdk-slim

# Installer curl pour les health checks
RUN apt-get update && apt-get install -y curl

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]