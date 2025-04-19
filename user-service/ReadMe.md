# Service Utilisateur (user-service)

## Description
Le Service Utilisateur est le composant central  
gérant toutes les données et opérations liées aux utilisateurs. 
Il fournit une API complète pour la création, modification et suppression des comptes utilisateurs, ainsi que la gestion des relations entre utilisateurs (contacts).
Ce service est essentiel car il soutient les fonctionnalités de collaboration et de partage présentes dans l'écosystème SGDI, 
en servant de référentiel d'identités pour tous les

## Fonction dans le service

Le Service Utilisateur est responsable de la gestion complète des utilisateurs incluant :
- Gestion des profils utilisateur (création, modification, suppression)
- Gestion des contacts entre utilisateurs
- Validation et modification des informations de profil
- Contrôle des accès basés sur les rôles utilisateur
- Récupération des métadonnées utilisateur pour d'autres services

## Interactions avec les autres services et base de données
### Base de données
MySQL pour le stockage relationnel des données utilisateur :
- Informations de profil
- Contacts
- Données d'authentification liées

## Interactions avec d'autres services

- Service d'Authentification (auth-service) : Vérifie les tokens JWT et les autorisations
- Service Workspace (workspace-service) : Fournit des informations utilisateur pour la gestion des membres dans les workspaces
- Service Document (document-service) : Fournit le contexte utilisateur pour les opérations sur les documents
- Service Note (note-service) : Fournit le contexte utilisateur pour les notes
- Service Chat (chat-service) : Fournit les informations utilisateur pour les messages et notifications

## Déoendances dans le service
- Spring Boot Starter Web
- Spring Data JPA
- Spring Security
- Spring Cloud Eureka Client
- Spring Cloud Config Client
- MySQL Connector
- Lombok
- JWT Security Library (bibliothèque commune)
- ModelMapper / MapStruct (mapping d'objets)
- Validation API
- Spring Boot Actuator (monitoring)

## Endpoints

- GET /api/users - Récupérer la liste des utilisateurs (admin)
- GET /api/users/{id} - Récupérer un utilisateur spécifique
- GET /api/users/me - Récupérer le profil de l'utilisateur connecté
- POST /api/users - Créer un nouvel utilisateur
- PUT /api/users/{id} - Mettre à jour un utilisateur
- DELETE /api/users/{id} - Supprimer un utilisateur
- GET /api/users/{id}/contacts - Récupérer la liste des contacts d'un utilisateur
- POST /api/users/{id}/contacts - Ajouter un contact
- DELETE /api/users/{id}/contacts/{contactId} - Supprimer un contact
- GET /api/users/search?query= - Rechercher des utilisateurs par nom/email

## Faits
- ✅ Structure de base du service Spring Boot
- ✅ Modèles de données utilisateur et contacts
- ✅ Repositories JPA pour accès aux données
- ✅ Configuration de connexion à MySQL
- ✅ Configuration d'enregistrement auprès d'Eureka
- ✅ Endpoints REST fondamentaux (CRUD utilisateur)
- ✅ Dockerfile pour la conteneurisation
- ✅ Mise en place des mécanismes de sécurité avec JWT

## À faire
- ⬜ Implémentation complète des contacts utilisateur
- ⬜ Récupération des configurations depuis Config Server
- ⬜ Pagination et tri pour les listes d'utilisateurs
- ⬜ Validation avancée des données utilisateur
- ⬜ Tests unitaires et d'intégration

## Notes
- Le service doit être conçu avec une séparation claire entre les couches (contrôleur, service, repository)
- Des DTOs (Data Transfer Objects) doivent être utilisés pour découpler les modèles internes des représentations API
- Les validations doivent être implémentées à la fois côté API et côté service
- Pour la sécurité, utiliser la bibliothèque commune de validation JWT partagée entre les services
- La pagination doit être implémentée pour les endpoints retournant des listes d'utilisateurs
- Les contacts doivent être gérés de manière bidirectionnelle (acceptation requise)
- Implémenter un mécanisme de cache pour les données utilisateur fréquemment accédées