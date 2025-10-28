# ----- Étape 1 : Build -----
# Utilise une image Maven 3.9 qui contient déjà le JDK 21
FROM maven:3.9-eclipse-temurin-21 AS builder

# Répertoire de travail
WORKDIR /workspace

# Copie le pom.xml en premier pour le cache Docker
COPY pom.xml .

# Copie le code source de ton application
COPY src src

# Lance le build avec Maven (mvn) directement, pas le wrapper (mvnw)
RUN mvn clean package -DskipTests

# ----- Étape 2 : Run -----
# Utilise une image JRE 21 (plus légère) pour exécuter l'application
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copie le .jar construit depuis l'étape "builder"
COPY --from=builder /workspace/target/IMT-Architecture-Logiciel-0.0.1-SNAPSHOT.jar app.jar

# Expose le port par défaut de Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
