# 🚗 BFB — Gestion de Location Automobile

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Build-blue.svg)](https://www.docker.com/)

## 📖 Contexte du Projet

Ce projet a été développé suite à l'obtention du budget pour la refonte du système de gestion des locations automobiles ("BFB").

**Le besoin métier :**
L'objectif est de gérer trois entités principales : **Clients**, **Véhicules** et **Contrats**. Le système doit respecter des règles métier strictes définies par la direction :
- Unicité des clients et des véhicules.
- Gestion des états de véhicules (Disponible, En location, En panne).
- Annulation automatique des contrats si un véhicule tombe en panne.
- Gestion des retards et annulations en cascade pour les locations suivantes.

**Le défi technique :**
Le comité d'architecture a imposé une contrainte forte : **"Apporter un soin particulier à l'architecture de l'application"**. Pour répondre à cette exigence et garantir la maintenabilité, nous avons opté pour une **Architecture Hexagonale (Ports & Adapters)** stricte, isolant totalement le code métier des frameworks.

---

## 🏗️ Architecture Logicielle

L'application est structurée en **multi-modules Maven** pour forcer physiquement le respect de l'architecture hexagonale.

### 1. Le Noyau (Core Domain) - `domain`
C'est le cœur de l'application. Il contient la logique métier pure et ne dépend d'aucun framework (pas de Spring, pas de Mongo).
- **Modèles** : Objets riches (`Client`, `Vehicle`, `Contract`).
- **Ports (Interfaces)** : Définissent comment le domaine communique avec l'extérieur (ex: `ClientStorageProvider`).
- **Services** : Orchestration de la logique (`ClientsService`).

### 2. Les Adaptateurs (Adapters)
Ils font le lien entre le monde extérieur et le domaine.
- **Adapters-IN (Primaires)** : Pilotent l'application.
    - `adapters-in-rest` : Contrôleurs REST exposant l'API.
    - `adapters-in-scheduler` : Tâches planifiées (Batchs) pour la détection des retards.
- **Adapters-OUT (Secondaires)** : Pilotés par l'application.
    - `adapters-out-bdd` : Implémentation de la persistance avec MongoDB.

### 3. L'Assemblage - `application`
Le point d'entrée (`Main`) qui configure Spring Boot, scanne les modules et injecte les dépendances (Inversion de contrôle).

---

## 🛠️ Design Patterns Implémentés

L'analyse du code révèle l'utilisation de plusieurs patrons de conception pour répondre aux problèmes architecturaux :

### 1. Chain of Responsibility (Chaîne de Responsabilité)
Utilisé pour la validation complexe des objets métier avant leur persistance. Cela permet d'ajouter ou de retirer des règles de validation sans modifier le service principal.
* **Implémentation** : `AbstractValidatorStep` et `ConstraintValidatorStep`.
* **Exemple** : Dans `ClientsServiceValidator`, la création d'un client passe par une chaîne : *Validation des annotations* -> *Unicité globale* -> *Unicité du permis*.
* **Fichier clé** : `domain/.../validators/AbstractValidatorStep.java`

### 2. Builder Pattern
Utilisé pour la construction d'objets complexes, notamment les modèles du domaine et les DTOs, garantissant l'immutabilité et la lisibilité.
* **Implémentation** : Via l'annotation Lombok `@Builder`.
* **Usage** : `Client.builder().lastName("Doe").build()`.

### 3. Ports & Adapters (Architecture Hexagonale)
Le pattern architectural global.
* **Port (Interface)** : `ClientStorageProvider` (dans le Domain).
* **Adapter (Implémentation)** : `ClientsBddService` (dans adapters-out-bdd).

### 4. Data Transfer Object (DTO)
Séparation stricte entre les objets exposés via l'API, les objets du domaine et les entités de base de données.
* **API** : `ClientInput`, `ClientOutput`.
* **Domain** : `Client`.
* **Persistance** : `ClientEntity`.

### 5. Mapper Pattern
Utilisé pour convertir les objets d'une couche à l'autre (DTO <-> Domain <-> Entity).
* **Exemple** : `VehicleBddMapper` transforme un `VehicleEntity` (BDD) en `Vehicle` (Domain).

### 6. Decorator / Proxy (via héritage)
Utilisation du pattern Decorator via l'héritage dans le domaine pour ajouter la validation.
* **Exemple** : `ClientsServiceValidator` étend `ClientsService` pour ajouter la couche de validation (Chain of Responsibility) avant d'appeler les méthodes parentes.

---

## 🧪 Qualité et Tests

Le projet suit une stratégie de test rigoureuse :

- **Tests Unitaires du Domaine** : Couvrent 100% de la logique métier critique (Services, Validateurs) sans dépendance externe.
- **Tests d'Intégration (Controllers)** : Vérifient que l'API REST respecte les contrats (codes HTTP, format JSON).
- **Tests d'Intégration (BDD)** : Assurent que le mapping et les requêtes MongoDB fonctionnent correctement.

---

## 🚀 Démarrage rapide

### Prérequis
* Docker & Docker Compose (Recommandé)
* Java 21

### Lancement avec Docker Compose
L'environnement complet (App + MongoDB) se lance en une commande :

```bash
docker-compose up -d --build
```

---

## Structure 

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
