# 🚗 IMT-Architecture-Logiciel — Gestion de location automobile

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Build-blue.svg)](https://www.docker.com/)

## 📋 Description

Projet Spring Boot de gestion de location automobile, réalisé dans le cadre du TP d'Architecture Logicielle à l'IMT.
L'objectif est de mettre en œuvre une **Architecture Hexagonale (Ports & Adapters)** stricte via une approche *
*Multi-Modules Maven** pour garantir l'isolation du domaine métier.

## ✨ Fonctionnalités principales

- **Clients** : Création et gestion (Validation d'unicité, formats de permis/nom/prénom).
- **Véhicules** : Gestion du parc (Plaque d'immatriculation, état, motorisation).
- **Contrats** : Cycle de vie de la location (Création, validation, clôture).
- **Règles métier** :
    - Validation en chaîne (Chain of Responsibility) pour les invariants et les règles métier complexes.
    - Gestion des états de véhicules et d'annulations automatiques.

---

## 🏗️ Architecture — Hexagonale (Ports & Adapters)

Le projet est divisé en modules Maven distincts pour forcer le respect des dépendances :

1. `domain` : Le cœur pur. Contient les modèles, les règles et les interfaces (Ports). **Aucune dépendance Spring.**
2. `adapters-in-rest` : L'API Web. Convertit les JSON en objets métier.
3. `adapters-out-bdd` : La persistance. Implémente les interfaces de stockage du domaine.
4. `adapters-in-scheduler` : Les tâches planifiées (Batchs).
5. `application` : Le point d'entrée. Assemble et configure l'application.

```plaintext
            ┌─────────────────────────────────────────────────┐
            │           ADAPTATEURS PRIMAIRES                 │
            ├─────────────────────────────────────────────────┤
            │  adapters-in-rest      │  adapters-in-scheduler │
            │  (API REST)            │  (Tâches planifiées)   │
            └──────────────┬─────────┴───────────┬────────────┘
                           │                     │
                           ▼                     ▼
                     ┌─────────────────────────────────┐
                     │          APPLICATION            │
                     │    (Composition & Config)       │
                     └────────────┬────────────────────┘
                                  │
                                  ▼
                     ┌─────────────────────────────────┐
                     │           DOMAIN                │
                     │    (Logique métier pure)        │
                     │    - Pas de dépendances         │
                     │    - Java pur                   │
                     └────────────┬────────────────────┘
                                  │
                                  ▼
                     ┌─────────────────────────────────┐
                     │   ADAPTATEURS SECONDAIRES       │
                     ├─────────────────────────────────┤
                     │      adapters-out-bdd           │
                     │      (Persistance MongoDB)      │
                     └─────────────────────────────────┘
```

### Structure conceptuelle

```plaintext
imt-architecture-logiciel/
│
├── domain/                                      # 🎯 COEUR MÉTIER (Java Pur)
│   └── src/main/java/com/imt/
│       ├── clients/
│       │   ├── model/                           # Modèles immuables & riches
│       │   │   └── Client.java
│       │   ├── validators/                      # Règles métier (Chain of Responsibility)
│       │   │   ├── ClientUnicityValidatorStep.java
│       │   │   └── ClientUnicityLicenseValidatorStep.java
│       │   ├── ClientStorageProvider.java       # [PORT OUT] Interface Repository
│       │   ├── ClientsService.java              # Logique métier de base (CRUD)
│       │   └── ClientsServiceValidator.java     # [PORT IN] Point d'entrée avec validation
│       │
│       ├── vehicle/
│       │   ├── model/
│       │   │   ├── Vehicle.java
│       │   │   ├── EngineTypeEnum.java
│       │   │   └── VehicleStateEnum.java
│       │   ├── validators/
│       │   │   ├── VehicleAlreadyExistValidatorStep.java
│       │   │   ├── VehicleEngineTypeValidatorStep.java
│       │   │   └── VehicleStateValidatorStep.java
│       │   ├── VehicleStorageProvider.java      # [PORT OUT]
│       │   ├── VehicleService.java
│       │   └── VehicleServiceValidator.java     # [PORT IN]
│       │
│       └── common/                              # Briques partagées du domaine
│           ├── exceptions/                      # Exceptions métier (ResourceNotFound, Conflict...)
│           │   ├── BadRequestException.java
│           │   ├── ConflictException.java
│           │   ├── ImtException.java
│           │   └── ResourceNotFoundException.java
│           ├── model/                           # Objets transverses
│           │   └── ValidatorResult.java
│           └── validators/                      # Moteur de validation
│               ├── AbstractValidatorStep.java
│               └── ConstraintValidatorStep.java
│
├── adapters-in-rest/                            # 🔌 ADAPTATEUR PRIMAIRE (REST)
│   └── src/main/java/com/imt/adaptersinrest/
│       ├── clients/
│       │   ├── mapper/                          # Conversion DTO <-> Domain
│       │   │   └── ClientApiMapper.java
│       │   ├── model/                           # DTOs (Data Transfer Objects)
│       │   │   ├── input/                       # JSON reçus
│       │   │   │   ├── ClientInput.java
│       │   │   │   └── ClientUpdateInput.java
│       │   │   └── output/                      # JSON renvoyés
│       │   │       └── ClientOutput.java
│       │   └── ClientsController.java           # Appelle le Domain (ClientsServiceValidator)
│       │
│       └── common/                              # Gestion globale des erreurs et formats
│           └── model/
│               ├── input/
│               │   ├── AbstractInput.java
│               │   ├── AbstractUpdateInput.java
│               │   └── UpdatableProperty.java   # Wrapper pour le PATCH (Gestion des nulls)
│               └── output/
│                   ├── AbstractOutput.java
│                   ├── ExceptionOutput.java
│                   └── ControllerExceptionHandler.java
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

## 📚 Principes architecturaux

### Architecture Hexagonale

- Indépendance du domaine : Aucune dépendance vers les frameworks
- Ports & Adapters : Interfaces claires entre les couches
- Inversion de dépendance : Le domaine définit ses contrats
- Testabilité : Le cœur métier est facilement testable

### Design Patterns utilisés

- Chain of Responsibility : Validation en chaîne
- Repository Pattern : Abstraction de la persistance
- Service Layer : Orchestration métier
- DTO Pattern : Séparation modèle métier/API

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
