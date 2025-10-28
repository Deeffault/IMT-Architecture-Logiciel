# IMT-Architecture-Logiciel

## Résumé

Projet Java Spring Boot. Ce fichier explique le workflow Git recommandé et comment démarrer l'application + MongoDB avec
Docker.

## Workflow Git (conseillé)

- Branche principale: `main`
- Branches de travail:
    - `feature/<descr>` pour nouvelles fonctionnalités
    - `fix/<descr>` pour corrections de bugs
    - `hotfix/<descr>` pour corrections urgentes sur `main`
    - `chore/<descr>` pour tâches non fonctionnelles (build, deps)
    - `release/<version>` pour préparation de release

### Format de commit (Conventional Commits)

Utiliser des préfixes pour clarifier l'intention:

- `feat:` ajout d'une fonctionnalité
- `fix:` correction d'un bug
- `docs:` modification de la documentation
- `style:` formatage, espaces, pas de changement fonctionnel
- `refactor:` refactorisation sans changement de comportement
- `perf:` amélioration de performance
- `test:` ajout/modification de tests
- `chore:` tâches d'infrastructure / build
- `ci:` configuration d'intégration continue

Format recommandé:
Exemples:

- `feat(auth): add JWT login endpoint`
- `fix(api): handle null pointer in UserController`
- `style: reformat code with IntelliJ formatter`
- `test(user): add unit tests for UserService`

### Processus de PR

1. Créer une branche depuis `main`:  
   `git checkout -b feature/<descr>`
2. Commits atomiques et bien nommés.
3. Push: `git push origin feature/<descr>`
4. Ouvrir une Pull Request vers `main` avec description et tickets liés.
5. Revue, tests CI. Merge via squash ou merge commit selon la politique.

## Docker — démarrer les conteneurs

Deux options: utiliser Docker Desktop localement ou Docker via CI/serveur.

### Exemple `docker-compose.yml`

- Spring Boot expose le service `app`, se connecte à `mongo` via `spring.data.mongodb.uri`.
- Le service `app` est construit depuis le repo (présence d'un `Dockerfile`).

```yaml
version: "3.8"

services:
  mongo:
    image: mongo:6
    container_name: imt_mongo
    restart: unless-stopped
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: imtdb

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: imt_app
    restart: on-failure
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/imtdb
      - SPRING_DATA_MONGODB_DATABASE=imtdb
    depends_on:
      - mongo

volumes:
  mongo_data:
```

## Démarrer les conteneurs

1. Assurez-vous que Docker est installé et en cours d'exécution.
2. Placez-vous dans le répertoire contenant `docker-compose.yml`.
3. Exécutez la commande:
    ```bash
    docker-compose up -d --build
    ```
4. Accédez à l'application via `http://localhost:8080` et MongoDB via `mongodb://localhost:27017`.
5. Pour arrêter les conteneurs:
    ```bash
    docker-compose down
    ```


Pour se connecter à la DB via IntelliJ, utilisez dans le champs URL :
`mongodb://user:pass@localhost:27017/carrentaldb?authSource=admin`