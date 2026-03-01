# 🫀 SmartPulse - Backend API

**Système de monitoring cardiaque connecté - SmartPulse**

Projet académique M1-GI (2025-2026) - École Nationale Superieure Polytechnique de Yaoundé

---

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [API Documentation](#api-documentation)
- [Authentification](#authentification)
- [Structure du projet](#structure-du-projet)
- [Technologies utilisées](#technologies-utilisées)

---

## 🎯 Vue d'ensemble

**SmartPulse** est une application de monitoring cardiaque qui permet :

- **Patients** : De mesurer leur fréquence cardiaque via une montre connectée et de recevoir des alertes
- **Médecins** : De surveiller les mesures cardiaque de leurs patients en temps réel et de gérer les seuils d'alerte
- **Communication temps réel** : Via WebSocket pour l'affichage live des mesures
- **Gestion des données** : Historique complet des mesures et sessions de monitoring

---

## 🏗️ Architecture

### Stack Technologique

**Backend :**
- **Framework** : Spring Boot 3.5.8 (Java 21)
- **Sécurité** : Spring Security + JWT (JSON Web Token)
- **Base de données** : PostgreSQL
- **Temps réel** : WebSocket (Spring Messaging)
- **ORM** : Spring Data JPA / Hibernate


---

## 📦 Prérequis

- **Java 21+** ([Télécharger](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html))
- **Maven 3.9+** (inclus avec Spring Boot)
- **PostgreSQL 13+** ([Télécharger](https://www.postgresql.org/download/))
- **Git**
- **Postman** ou **Insomnia** (pour tester l'API)

### Vérifier votre environnement

```bash
java -version          # Doit afficher Java 21+
mvn -version           # Doit afficher Maven 3.9+
psql --version         # Doit afficher PostgreSQL 13+
```

---

## 💻 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/votre-repo/smart-pulse-backend.git
cd smart-pulse-backend
```

### 2. Créer la base de données PostgreSQL

```bash
# Connexion à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE "smart-pulse";

# Quitter
\q
```

### 3. Installer les dépendances

```bash
mvn clean install
```

---

## ⚙️ Configuration

### Variables d'environnement

Créer un fichier `.env` à la racine du projet (optionnel) :

```bash
# Serveur
PORT=8080

# Base de données
DB_HOST=localhost
DB_PORT=5432
DB_NAME=smart-pulse
DB_USER=postgres
DB_PASSWORD=admin

# JWT
JWT_SECRET=votre_clé_secrète_très_longue_et_sécurisée_minimum_256_bits
JWT_EXPIRATION=86400000  # 24 heures en ms

# CORS
FRONTEND_URL=http://localhost:3000
FRONTEND_PROD_URL=https://smart-pulse-frontend.vercel.app
```

### Configuration par défaut

Le fichier `src/main/resources/application.properties` contient les valeurs par défaut :

```properties
spring.application.name=smart-pulse
server.port=${PORT:8080}

spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:smart-pulse}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:admin}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🚀 Démarrage

### Mode développement

```bash
# Démarrage du serveur
mvn spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`

### Vérifier que le serveur fonctionne

```bash
curl http://localhost:8080/api/health
# Ou tester un endpoint public
curl -X POST http://localhost:8080/api/auth/register-test
```

---

## 📡 API Documentation

### 🔑 Authentification

#### 1. **Inscription Médecin**

```
POST /api/auth/register/medecin
Content-Type: application/json
```

**Body :**
```json
{
  "mail": "medecin@example.com",
  "password": "MotDePasse123!",
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1980-05-15",
  "specialite": "Cardiologue"
}
```

**Réponse (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZWRlY2luQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2MTAxMTU0LCJleHAiOjE3NjYxODc1NTR9..."
}
```

**Erreurs :**
- `400` : Email déjà utilisé ou données invalides
- `500` : Erreur serveur

---

#### 2. **Inscription Patient**

```
POST /api/auth/register/patient
Content-Type: application/json
```

**Body :**
```json
{
  "mail": "patient@example.com",
  "password": "MotDePasse123!",
  "nom": "Martin",
  "prenom": "Sophie",
  "dateNaissance": "1995-03-20",
  "medecinId": 1
}
```

**Réponse (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

#### 3. **Connexion**

```
POST /api/auth/login
Content-Type: application/json
```

**Body :**
```json
{
  "mail": "medecin@example.com",
  "password": "MotDePasse123!"
}
```

**Réponse (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Erreurs :**
- `401` : Email ou mot de passe incorrect

---

### 👤 Gestion du Profil

Tous les endpoints ci-dessous nécessitent l'authentification via JWT :
```
Authorization: Bearer <token>
```

#### 4. **Récupérer mon profil**

```
GET /api/user/me
Authorization: Bearer <token>
```

**Réponse (200 OK) :**
```json
{
  "id": 1,
  "mail": "medecin@example.com",
  "role": "MEDECIN",
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1980-05-15",
  "specialite": "Cardiologue"
}
```

---

#### 5. **Modifier mon profil**

```
PUT /api/user/update
Content-Type: application/json
Authorization: Bearer <token>
```

**Body :**
```json
{
  "nom": "DuPont",
  "prenom": "Jean-Paul",
  "password": "NouveauMotDePasse123!"
}
```

**Réponse (200 OK) :**
```json
{
  "id": 1,
  "mail": "medecin@example.com",
  "nom": "DuPont",
  "prenom": "Jean-Paul"
}
```

---

### 👨‍⚕️ Endpoints Médecin

#### 6. **Récupérer ma liste de patients**

```
GET /api/user/my-patients
Authorization: Bearer <token>
```

**Réponse (200 OK) :**
```json
[
  {
    "id": 2,
    "mail": "patient1@example.com",
    "role": "PATIENT",
    "nom": "Martin",
    "prenom": "Sophie",
    "dateNaissance": "1995-03-20"
  },
  {
    "id": 3,
    "mail": "patient2@example.com",
    "role": "PATIENT",
    "nom": "Durand",
    "prenom": "Pierre",
    "dateNaissance": "1988-07-10"
  }
]
```

---

### 🏥 Endpoints Patient

#### 7. **Récupérer mon médecin**

```
GET /api/user/my-doctor
Authorization: Bearer <token>
```

**Réponse (200 OK) :**
```json
{
  "id": 1,
  "mail": "medecin@example.com",
  "role": "MEDECIN",
  "nom": "Dupont",
  "prenom": "Jean",
  "specialite": "Cardiologue"
}
```

---

### 💓 Mesures Cardiaques

#### 8. **Démarrer une session de monitoring**

```
POST /api/v1/cardiac/start
Content-Type: application/json
```

**Body :**
```json
{
  "patientId": "2"
}
```

**Réponse (200 OK) :**
```json
1
```
*(ID de la session créée)*

---

#### 9. **Recevoir les données du capteur**

```
POST /api/v1/cardiac/receive
Content-Type: application/json
```

**Body (depuis la montre IoT) :**
```json
{
  "bpm_current": 72,
  "status": "normal",
  "timestamp": "2025-03-01T10:30:00"
}
```

ou

```json
{
  "objectJSON": "{\"bpm_current\": 72, \"status\": \"normal\"}"
}
```

**Réponse :**
```
204 No Content
```

---

### 🔔 Notifications

#### 10. **Récupérer mes notifications**

```
GET /api/v1/notifications/user/{userId}
Authorization: Bearer <token>
```

**Réponse (200 OK) :**
```json
[
  {
    "id": 1,
    "userId": "2",
    "message": "Votre fréquence cardiaque dépasse les 120 BPM",
    "isRead": false,
    "createdAt": "2025-03-01T10:35:00"
  }
]
```

---

#### 11. **Récupérer mes notifications non lues**

```
GET /api/v1/notifications/user/{userId}/unread
Authorization: Bearer <token>
```

---

#### 12. **Marquer une notification comme lue**

```
PATCH /api/v1/notifications/{notificationId}/read
Authorization: Bearer <token>
```

**Réponse :**
```
200 OK
```

---

#### 13. **Marquer toutes les notifications comme lues**

```
POST /api/v1/notifications/user/{userId}/read-all
Authorization: Bearer <token>
```

---

## 🔐 Authentification

### Fonctionnement JWT

1. **Inscription/Login** → Recevez un token JWT
2. **Stockage** → Sauvegardez le token (localStorage/sessionStorage)
3. **Utilisation** → Ajoutez le header à chaque requête protégée :
   ```
   Authorization: Bearer <votre_token>
   ```

### Structure du token

```
Header.Payload.Signature

Header: {"alg": "HS512", "typ": "JWT"}
Payload: {"sub": "user@example.com", "iat": 1766101154, "exp": 1766187554}
Signature: 7RXGL9taRpdo9ZWMCASfp3x9JzFGZT9FwrtQMrrArRRsLUFyHCFqTZjzUD9DL
```

### Durée de validité

- **Durée par défaut** : 24 heures
- Après expiration : Refaites un login

### Exemples avec Postman

**Récupérer un token :**
```
POST http://localhost:8080/api/auth/login
Body (raw, JSON):
{
  "mail": "medecin@example.com",
  "password": "MotDePasse123!"
}
```

**Copier le token reçu dans les headers :**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZWRlY2luQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2MTAxMTU0LCJleHAiOjE3NjYxODc1NTR9...
```

---

## 📁 Structure du projet

```
smart-pulse-backend/
├── src/
│   ├── main/
│   │   ├── java/com/smartpulse/demo/
│   │   │   ├── DemoApplication.java          # Point d'entrée Spring Boot
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # Configuration Spring Security & JWT
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── WebConfig.java            # Configuration CORS
│   │   │   │   └── WebSocketConfig.java      # Configuration WebSocket
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       # Login, Register
│   │   │   │   ├── UserController.java       # Profil utilisateur
│   │   │   │   ├── CardiacController.java    # Mesures cardiaques
│   │   │   │   ├── NotificationController.java
│   │   │   │   ├── ActivityController.java
│   │   │   │   └── SeuilController.java      # Seuils d'alerte
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── MonitoringService.java    # Logique de monitoring
│   │   │   │   ├── SessionManagerService.java
│   │   │   │   └── NotificationService.java
│   │   │   ├── model/
│   │   │   │   ├── entity/                   # Entités JPA
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Patient.java
│   │   │   │   │   ├── Medecin.java
│   │   │   │   │   ├── CardiacSession.java
│   │   │   │   │   ├── HeartRateRecord.java
│   │   │   │   │   ├── Notification.java
│   │   │   │   │   ├── Activity.java
│   │   │   │   │   └── Seuil.java
│   │   │   │   ├── DTO/                      # Data Transfer Objects
│   │   │   │   └── Enum/
│   │   │   │       └── Role.java
│   │   │   └── repository/                   # Spring Data JPA
│   │   │       ├── UserRepository.java
│   │   │       ├── PatientRepository.java
│   │   │       ├── MedecinRepository.java
│   │   │       ├── CardiacSessionRepository.java
│   │   │       └── ...
│   │   └── resources/
│   │       ├── application.properties        # Configuration Spring Boot
│   │       └── simulator/
│   │           └── dispositif_simulator.py   # Simulateur IoT
│   └── test/
│       └── java/com/smartpulse/demo/
│           └── DemoApplicationTests.java
├── pom.xml                                   # Dépendances Maven
├── mvnw / mvnw.cmd                          # Maven Wrapper
├── Dockerfile                               # Configuration Docker
├── endpoints.md                             # Documentation API détaillée
└── README.md                                # Ce fichier
```

---

## 🛠️ Technologies utilisées

| Catégorie | Technologie | Version |
|-----------|-------------|---------|
| **Langage** | Java | 21 |
| **Framework** | Spring Boot | 3.5.8 |
| **Sécurité** | Spring Security | Spring Boot 3.5.8 |
| **JWT** | JJWT | 0.12.6 |
| **ORM** | Spring Data JPA / Hibernate | Inclus |
| **Base de données** | PostgreSQL | 13+ |
| **Temps réel** | Spring WebSocket / SockJS | Inclus |
| **Build** | Maven | 3.9+ |
| **Conteneurisation** | Docker | - |
| **Utilities** | Lombok | Inclus |

---

## 🔍 Troubleshooting

### Problème : Erreur "CORS policy"

**Solution :** Vérifiez que le frontend utilise `http://localhost:3000` ou mettez à jour `FRONTEND_URL` dans `SecurityConfig.java`

### Problème : "401 Unauthorized" sur endpoints protégés

**Vérifications :**
1. Avez-vous inclus le header `Authorization: Bearer <token>` ?
2. Le token n'est-il pas expiré (durée 24h) ?
3. Le token est-il valide (copié intégralement) ?

### Problème : Base de données non trouvée

```bash
# Recréer la base de données
psql -U postgres
CREATE DATABASE "smart-pulse";
\q

# Redémarrer l'application
mvn spring-boot:run
```

### Problème : Port 8080 déjà utilisé

```bash
# Utiliser un autre port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

## 📚 Documentation supplémentaire

- **Endpoints détaillés** : Voir `endpoints.md`
- **Architecture WebSocket** : Voir `WebSocketConfig.java`
- **Simulateur IoT** : Voir `simulator/dispositif_simulator.py`

---

## 🤝 Contribution

Les contributions sont bienvenues ! Veuillez :

1. Créer une branche pour votre feature (`git checkout -b feature/AmazingFeature`)
2. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
3. Push vers la branche (`git push origin feature/AmazingFeature`)
4. Ouvrir une Pull Request


## 🎓 Contexte Académique

**Formation** : Master 1 Génie Informatique (M1-GI)  
**Année académique** : 2025-2026  
**Unité d'Enseignement** : Microprocesseurs  
**Établissement** : École Nationale Superieure Polytechnique de Yaoundé

---

**Dernière mise à jour :** Mars 2025  
**Version API :** v1.0.0

