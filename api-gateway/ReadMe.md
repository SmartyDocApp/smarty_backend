# Service API Gateway (api-gateway)

## Description
Le Service API Gateway est le point d'entrée centralisé de l'écosystème SGDI,
agissant comme façade unifiée pour tous les microservices. 
Il gère le routage des requêtes vers les services appropriés, 
simplifiant ainsi l'architecture pour les clients externes. 
Ce service est essentiel car il offre une couche de sécurité commune et des fonctionnalités transversales pour l'ensemble de la plateforme.

## Fonctions dans le service

Le Service API Gateway est responsable de plusieurs fonctions critiques incluant :
- Routage des requêtes vers les microservices appropriés
- Authentification et autorisation centralisées via JWT
- Load balancing des requêtes
- Mise en œuvre de circuit breakers pour la résilience
- Monitoring et métriques globales
- Filtrage et transformation des requêtes/réponses
- Rate limiting pour prévenir les abus

## Interactions avec les autres services
L'API Gateway interagit avec tous les services du système :
- Service d'Authentification (auth-service) : Validation des tokens et délégation des opérations d'authentification
- Service Utilisateur (user-service) : Routage des requêtes de gestion d'utilisateurs
- Service Workspace (workspace-service) : Routage des requêtes liées aux espaces de travail
- Service Document (document-service) : Routage des requêtes de gestion documentaire
- Service Note (note-service) : Routage des requêtes liées aux notes
- Service Chat (chat-service) : Routage des requêtes de messagerie et WebSockets

## Dépendances dans le service
- Spring Cloud Gateway
- Spring WebFlux
- Spring Security
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator
- Resilience4j (Circuit Breaker)
- JWT pour validation des tokens
- Spring Boot Admin Client (monitoring)
- Micrometer (métriques)

## Routes principales

- `/api/auth/**` → Service d'Authentification
- `/api/users/**` → Service Utilisateur
- `/api/workspaces/**` → Service Workspace
- `/api/documents/**` → Service Document
- `/api/notes/**` → Service Note
- `/api/chat/**` → Service Chat
- `/ws/**` → WebSockets pour le Chat
- `/actuator/**` → Endpoints de monitoring
- `/fallback/**` → Routes de fallback en cas d'échec

## Faits
- ✅ Structure de base du service Spring Cloud Gateway
- ✅ Configuration des routes vers les microservices
- ✅ Intégration avec Eureka pour la découverte de services
- ✅ Implémentation des circuit breakers
- ✅ Filtre d'authentification JWT
- ✅ Métriques via Spring Boot Actuator
- ✅ Dockerfile pour la conteneurisation

## À faire
- ⬜ Tests unitaires et d'intégration

## Notes
- Le service API Gateway est conçu pour fonctionner avec Spring Boot 3.4.4
- La configuration des routes peut être effectuée par fichier YAML ou programmatiquement en Java
- Pour les flux WebSocket, des configurations spéciales sont nécessaires pour garantir la réplication des sessions
- Il est recommandé d'isoler les mécanismes d'authentification dans leurs propres filtres
- Les circuit breakers doivent être configurés avec des paramètres adaptés à chaque service
- Pour une haute disponibilité, déployer plusieurs instances de la gateway avec un load balancer externe
- Surveiller attentivement les métriques de performance pour optimiser les timeouts et circuits breakers