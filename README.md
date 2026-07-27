![CI](https://github.com/G1TS23/Library/actions/workflows/ci.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=G1TS23_Library&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=G1TS23_Library)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=G1TS23_Library&metric=coverage)](https://sonarcloud.io/summary/new_code?id=G1TS23_Library)
# 📚 Library

Application de gestion de bibliothèque développée avec **Quarkus**, dans le cadre de ma montée en compétence sur ce framework.

> ⚠️ **Projet d'apprentissage, pas de production.**
> L'objectif est d'explorer un maximum de concepts de l'écosystème Quarkus, parfois au-delà de ce que l'application justifierait réellement. Certains choix sont assumés comme pédagogiques plutôt qu'optimaux (voir [Choix pédagogiques](#-choix-pédagogiques)).

## 🎯 Objectif

Servir de terrain d'expérimentation pour les concepts clés de Quarkus : injection de dépendances, persistance, consommation d'API externe, validation, résilience, sécurité et programmation réactive — le tout adossé à une démarche de test rigoureuse.

## 🏗️ Architecture

- **CDI** pour l'injection de dépendances (constructor injection)
- **Panache** + **Repository pattern** pour séparer la logique métier de l'accès aux données
- **DTOs** pour découpler les modèles internes des contrats d'API
- **Bean Validation** (`@NotBlank`, `@Min`, `@Max`, `@Valid`) à la frontière, jamais dupliquée dans le service
- **Pagination** `offset`/`limit` avec une enveloppe `PagedResponse`
- **OpenLibraryClient** via `@RegisterRestClient` pour consommer l'API externe Open Library

## 🧩 Concepts explorés

- **Fault Tolerance** — `@Retry`, `@Timeout`, `@Fallback` sur le client ; une panne d'Open Library dégrade en `503` avec un en-tête `Retry-After`, distinct d'un résultat vide
- **Sécurité JWT** — `smallrye-jwt` + `@RolesAllowed`, distinction `401`/`403`, clés RSA (clé de vérification versionnée, clé privée hors dépôt, paire de test générée en mémoire)
- **Réactif avec Mutiny** — le chemin de recherche renvoie un `Uni` de bout en bout (client → service → ressource), en I/O non bloquant
- **Discipline de test** — JUnit 5, Mockito, RestAssured, `@QuarkusTest`, `@TestSecurity`/`@JwtSecurity`, et vérification systématique par *mutation testing* (casser volontairement le code pour prouver qu'un test le détecte)
- **OpenAPI/Swagger** généré via `smallrye-openapi`

## 🛠️ Stack

`Java 21` · `Quarkus` · `Hibernate ORM Panache` · `PostgreSQL` · `SmallRye (JWT · Fault Tolerance · OpenAPI)` · `Mutiny` · `Maven` · `JUnit 5` / `RestAssured` · `SonarQube`

## 🎓 Choix pédagogiques

Quelques décisions relèvent de l'exploration, pas de ce qu'exigerait une vraie mise en production :

- **Le passage à Mutiny** sur la recherche est un exercice : à cette échelle, du code bloquant — ou les *virtual threads* de Java 21 — offrirait la même robustesse pour bien moins de complexité.
- **La sécurité JWT** valide des tokens mais n'expose aucun endpoint d'émission (`/login`) ; la configuration des clés est illustrative.
- L'application reste **hybride** : le chemin de recherche est réactif, les opérations CRUD restent bloquantes sur Panache.

## 🚀 Lancer le projet

```bash
./mvnw quarkus:dev
```

L'application sera disponible sur `http://localhost:8080`, avec la Dev UI Quarkus sur `/q/dev` et Swagger UI sur `/q/swagger-ui`. Une base PostgreSQL est démarrée automatiquement via **Dev Services** (Docker requis).

## 🐳 Build natif & conteneurisation

Compilation en binaire natif via **GraalVM** — l'une des vitrines de Quarkus (démarrage en quelques dizaines de millisecondes, faible empreinte mémoire) :

```bash
# Compile dans un conteneur Linux (pas besoin de GraalVM installé localement)
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Le binaire produit est un exécutable **Linux**, destiné à tourner en conteneur — pas sur la machine de dev. Un `docker-compose.yml` orchestre l'image native (via le `Dockerfile.native` généré par Quarkus) et une base PostgreSQL :

```bash
docker compose up --build
```

> 💡 **Leçon au passage.** La datasource n'est configurée qu'en profil `%dev` ; le lancement en natif (profil `prod`) oblige à fournir explicitement l'URL, les identifiants et la génération de schéma (via variables d'environnement dans le compose). Un bon rappel que le confort des *Dev Services* masque des besoins de configuration bien réels en production. À noter aussi : le binaire natif ne se recharge pas à chaud — toute modification impose de relancer l'étape de compilation.

## ✅ Tests

```bash
./mvnw test
```
## 🔄 Intégration continue

Pipeline GitHub Actions à chaque push et pull request, en deux jobs **parallèles** :

| Job | Rôle | Sortie |
|---|---|---|
| **Build & Analyze** | Compile, lance les 43 tests (PostgreSQL éphémère via Testcontainers), mesure la couverture JaCoCo, envoie l'analyse à SonarCloud | Quality Gate |
| **Mutation Test** | Lance PIT sur la couche service | Score de mutation + rapport HTML (artefact) |

Les deux signaux sont **indépendants** : une régression Sonar et une baisse du score de mutation échouent séparément, chacun pour sa raison.

> 💡 Couverture et mutation sont **complémentaires** : la couverture dit quelles lignes sont *exécutées* par les tests ; le score de mutation (PIT tue 100 % des mutants de la couche service) prouve que les tests *détectent* les régressions, pas seulement qu'ils traversent le code. Un module peut afficher 100 % de couverture sans qu'aucun test ne vérifie réellement son comportement — c'est ce trou que le mutation testing ferme.
>
> La couverture agrège les tests `@QuarkusTest` **et** les tests unitaires purs dans un rapport JaCoCo unique (l'extension Quarkus n'instrumentant nativement que les premiers).