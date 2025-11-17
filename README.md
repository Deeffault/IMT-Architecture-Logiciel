# 🚗 IMT-Architecture-Logiciel — Gestion de location automobile

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Build-blue.svg)](https://www.docker.com/)

## 📋 Description

Projet Spring Boot de gestion de location automobile, réalisé dans le cadre du TP d'Architecture Logicielle à l'IMT.
L'objectif est de mettre en œuvre une **Architecture Hexagonale (Ports & Adapters)** pour séparer clairement la logique
métier des détails techniques et faciliter la testabilité.

## ✨ Fonctionnalités principales

- Gestion des clients : création et mise à jour (nom, prénom, date de naissance, n° de permis).
- Gestion des véhicules : parc de véhicules (immatriculation, modèle, état — disponible, en location, en panne).
- Gestion des contrats : création et suivi des contrats de location (liaison client ↔ véhicule).
- Règles métier automatisées :
    - Annulation automatique des contrats "en attente" si un véhicule est déclaré "en panne".
    - Passage automatique des contrats en "retard" si le véhicule n'est pas restitué.
    - Annulation des contrats futurs si un retard bloque une location suivante.
- Validation des DTOs via `spring-boot-starter-validation`.

---

## 🏗️ Architecture — Hexagonale (Ports & Adapters)

Le projet est organisé en trois couches principales :

1. `domain` — le cœur métier (modèles, règles, ports). Aucune dépendance technique.
2. `application` — implémentation des cas d'usage (use cases) qui orchestrent le domaine.
3. `infrastructure` — adaptateurs techniques (API REST, persistance MongoDB, tâches planifiées, configuration Spring).

### Structure conceptuelle

```plaintext
imt-architecture-logiciel/
│
├── domain/                                  # 🎯 Module Domain (Cœur métier, pur Java, SANS Spring)
│   └── src/main/java/
│       └── com.imt/
│           ├── clients/
│           │   ├── model/
│           │   │   └── Client.java                   (Le modèle métier pur, immuable)
│           │   ├── validators/
│           │   │   ├── ClientUnicityValidatorStep.java   (Règle: nom+prénom+date)
│           │   │   └── ClientUnicityLicenseValidatorStep.java (Règle: numPermis unique)
│           │   ├── ClientStorageProvider.java        (PORT DE SORTIE / Repository Interface)
│           │   ├── ClientsService.java               (Service de base, CRUD)
│           │   └── ClientsServiceValidator.java      (PORT D'ENTRÉE / Use Case + Validation)
│           │
│           ├── vehicules/
│           │   ├── model/
│           │   │   ├── Vehicule.java                  
│           │   │   └── EtatVehicule.java              
│           │   ├── port/out/
│           │   │   └── VehiculeRepository.java        (PORT DE SORTIE)
│           │   ├── service/
│           │   │   └── VehiculeService.java           (PORT D'ENTRÉE)
│           │   └── ...
│           │
│           ├── contrats/
│           │   ├── model/
│           │   │   ├── Contrat.java                   
│           │   │   └── EtatContrat.java               
│           │   ├── service/
│           │   │   └── ContratService.java            (PORT D'ENTRÉE)
│           │   └── ...
│           │
│           └── common/
│               ├── exceptions/
│               │   ├── ImtException.java            (Exception de base)
│               │   ├── BadRequestException.java     (Pour validation @Pattern)
│               │   └── ConflictException.java       (Pour unicité)
│               ├── model/
│               │   └── ValidatorResult.java         (Résultat de la chaîne)
│               └── validators/
│                   ├── AbstractValidatorStep.java   (Base de la chaîne)
│                   └── ConstraintValidatorStep.java (Validation des @NotNull, @Pattern)
│
├── adapters-in-rest/                        # 🔌 Module REST (Adaptateur primaire)
│   └── src/main/java/
│       └── com.imt.IMT_Architecture_Logiciel.rest/
│           ├── clients/
│           │   ├── ClientsController.java
│           │   └── dto/
│           │       ├── input/
│           │       └── output/
│           ├── vehicules/
│           │   ├── VehiculesController.java
│           │   └── dto/
│           ├── contrats/
│           │   ├── ContratsController.java
│           │   └── dto/
│           └── common/
│               └── GlobalExceptionHandler.java
│
├── adapters-in-scheduler/                   # 📡 Module Scheduler (Adaptateur primaire)
│   └── src/main/java/
│       └── com.imt.IMT_Architecture_Logiciel.scheduler/
│           └── ContratScheduler.java        ← Appelle VerifierContratsEnRetardUseCase 
│
├── adapters-out-bdd/                        # 💾 Module BDD (Adaptateur secondaire)
│   └── src/main/java/
│       └── com.imt.IMT_Architecture_Logiciel.bdd/
│           ├── clients/
│           │   ├── ClientMongoRepository.java     ← Implémente domain.port.out.ClientRepository
│           │   ├── repository/
│           │   │   └── ClientSpringDataRepository.java  (Interface extends MongoRepository)
│           │   ├── entity/
│           │   │   └── ClientDocument.java
│           │   └── mapper/
│           │       └── ClientBddMapper.java
│           │
│           ├── vehicules/
│           │   ├── VehiculeMongoRepository.java   ← Implémente domain.port.out.VehiculeRepository
│           │   ├── repository/
│           │   │   └── VehiculeSpringDataRepository.java
│           │   ├── entity/
│           │   │   └── VehiculeDocument.java
│           │   └── mapper/
│           │       └── VehiculeBddMapper.java
│           │
│           └── contrats/
│               ├── ContratMongoRepository.java    ← Implémente domain.port.out.ContratRepository
│               ├── repository/
│               │   └── ContratSpringDataRepository.java
│               ├── entity/
│               │   └── ContratDocument.java
│               └── mapper/
│                   └── ContratBddMapper.java
│
└── application/                             # 🚀 Module Application (Composition & Démarrage)
    └── src/main/java/
        └── com.imt.IMT_Architecture_Logiciel/
            ├── ImtArchitectureLogicielApplication.java
            └── config/
                └── BeanConfiguration.java   ← Assemble tout (Injection de dépendances)
```

---

## 🚀 Technologies

- Java 21
- Spring Boot 3.5.7
- spring-boot-starter-web
- spring-boot-starter-data-mongodb
- spring-boot-starter-validation
- MongoDB
- Lombok
- Docker & Docker Compose
- Maven

---

## 🐳 Démarrage rapide

Prérequis : Docker & Docker Compose (ou Java 21 + Maven si vous exécutez localement).

1) Lancer avec Docker Compose (application + MongoDB) :

```bash
# Depuis la racine du projet
docker-compose up -d --build
```

- `--build` force la reconstruction de l'image de l'application.
- `-d` lance les conteneurs en arrière-plan.

Arrêter les conteneurs :

```bash
docker-compose down
```

2) Exécution locale (sans Docker) :

```bash
# Compiler
./mvnw clean package

# Lancer l'application
./mvnw spring-boot:run
```

L'application sera accessible par défaut sur : http://localhost:8080

---

## 🔧 Configuration & connexion à la base

- Les propriétés Spring se trouvent dans `src/main/resources/application.properties`.
- Le fichier `docker-compose.yml` définit un utilisateur/MDP pour MongoDB. Exemple d'URL de connexion (utilisé par
  l'application ou un client) :

```
mongodb://user:pass@localhost:27017/carrentaldb?authSource=admin
```

Adaptez les identifiants selon votre configuration locale.

---

## 🌊 Workflow Git (conseillé)

Branche principale : `main`

Branches de travail :

- `feature/<descr>` — nouvelles fonctionnalités
- `fix/<descr>` — corrections de bugs
- `hotfix/<descr>` — corrections urgentes sur `main`
- `chore/<descr>` — tâches non-fonctionnelles
- `release/<version>` — préparation de release

Format de commit (Conventional Commits) :

- `feat:` ajout d'une fonctionnalité
- `fix:` correction d'un bug
- `docs:` modifications de documentation
- `style:` formatage (sans changement fonctionnel)
- `refactor:` refactorisation
- `perf:` amélioration de performance
- `test:` ajout/modification de tests
- `chore:` tâches de build/CI

Exemple de workflow rapide :

```bash
git checkout -b feature/ma-fonctionnalite
# travailler, commit, push
git push origin feature/ma-fonctionnalite
# ouvrir une Pull Request vers main
```

---

## 📚 Contribution

Les contributions sont bienvenues : ouvrez une issue pour discuter d'une fonctionnalité, ou envoyez une pull request
depuis une branche dédiée.

---

## 📝 Licence

Ce projet est fourni à titre pédagogique (TP). Ajoutez ici une licence si nécessaire.

---
