# 📚 Library

Application de gestion de bibliothèque développée avec **Quarkus**, dans le cadre de ma montée en compétence sur ce framework.

## 🎯 Objectif

Ce projet sert de terrain d'apprentissage pour les concepts clés de l'écosystème Quarkus : injection de dépendances, persistance simplifiée, et consommation d'API externes.

## 🏗️ Architecture

- **CDI** pour l'injection de dépendances (constructor injection)
- **Panache** pour la couche de persistance
- **Repository pattern** pour séparer la logique métier de l'accès aux données
- **DTOs** pour découpler les modèles internes des contrats d'API
- **OpenLibraryClient** via `@RegisterRestClient` pour la consommation de l'API externe Open Library

## 🛠️ Stack

`Java` · `Quarkus` · `Panache` · `Maven` · `SonarQube`

## 🚀 Lancer le projet

\`\`\`bash
./mvnw quarkus:dev
\`\`\`

L'application sera disponible sur `http://localhost:8080`, avec la Dev UI Quarkus sur `/q/dev`.
