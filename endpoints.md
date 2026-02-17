# Documentation API - SmartPulse Backend

## 📋 Table des matières
- [Informations générales](#informations-générales)
- [Authentification](#authentification)
- [Endpoints publics](#endpoints-publics)
- [Endpoints protégés](#endpoints-protégés)
- [Codes de statut](#codes-de-statut)
- [Exemples d'utilisation](#exemples-dutilisation)

---

## Informations générales

**Base URL:** `http://localhost:8080`

**Format des données:** JSON

**Authentification:** JWT (JSON Web Token) via header `Authorization: Bearer <token>`

---

## Authentification

### Comment fonctionne l'authentification ?

1. **Créer un compte** (médecin ou patient) → Recevoir un token JWT
2. **Ou se connecter** avec email/password → Recevoir un token JWT
3. **Utiliser le token** dans le header `Authorization: Bearer <token>` pour accéder aux endpoints protégés

**Durée de validité du token:** 24 heures

---

## Endpoints publics

### 1. Inscription - Médecin

Permet à un médecin de créer un compte.

**URL:** `/api/auth/register/medecin`

**Méthode:** `POST`

**Authentification requise:** ❌ Non

**Body (JSON):**
```json
{
  "mail": "medecin@example.com",
  "password": "motdepasse123",
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1980-05-15",
  "specialite": "Cardiologue"
}
```

**Champs:**
| Champ | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| mail | string | ✅ Oui | Email unique du médecin |
| password | string | ✅ Oui | Mot de passe (sera hashé) |
| nom | string | ✅ Oui | Nom de famille |
| prenom | string | ✅ Oui | Prénom |
| dateNaissance | string (ISO 8601) | ✅ Oui | Date au format YYYY-MM-DD |
| specialite | string | ✅ Oui | Spécialité médicale |

**Réponse succès (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZWRlY2luQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2MTAxMTU0LCJleHAiOjE3NjYxODc1NTR9.7RXGL9taRpdo9ZWMCASfp3x9JzFGZT9FwrtQMrrArRRsLUFyHCFqTZjzUD9DL"
}
```

**Erreurs possibles:**
- `400 Bad Request` - Email déjà utilisé
- `400 Bad Request` - Données invalides

---

### 2. Inscription - Patient

Permet à un patient de créer un compte.

**URL:** `/api/auth/register/patient`

**Méthode:** `POST`

**Authentification requise:** ❌ Non

**Body (JSON):**
```json
{
  "mail": "patient@example.com",
  "password": "motdepasse123",
  "nom": "Martin",
  "prenom": "Sophie",
  "dateNaissance": "1995-03-20",
  "medecinId": 5
}
```

**Champs:**
| Champ | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| mail | string | ✅ Oui | Email unique du patient |
| password | string | ✅ Oui | Mot de passe (sera hashé) |
| nom | string | ✅ Oui | Nom de famille |
| prenom | string | ✅ Oui | Prénom |
| dateNaissance | string (ISO 8601) | ✅ Oui | Date au format YYYY-MM-DD |
| medecinId | number | ❌ Non | ID du médecin traitant (peut être null) |

**Réponse succès (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXRpZW50QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2MTAxMTU0LCJleHAiOjE3NjYxODc1NTR9.7RXGL9taRpdo9ZWMCASfp3x9JzFGZT9FwrtQMrrArRRsLUFyHCFqTZjzUD9DL"
}
```

**Erreurs possibles:**
- `400 Bad Request` - Email déjà utilisé
- `400 Bad Request` - Médecin avec cet ID non trouvé
- `400 Bad Request` - Données invalides

---

### 3. Connexion

Permet à un utilisateur (médecin ou patient) de se connecter.

**URL:** `/api/auth/login`

**Méthode:** `POST`

**Authentification requise:** ❌ Non

**Body (JSON):**
```json
{
  "mail": "user@example.com",
  "password": "motdepasse123"
}
```

**Champs:**
| Champ | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| mail | string | ✅ Oui | Email du compte |
| password | string | ✅ Oui | Mot de passe |

**Réponse succès (200 OK):**
```json
"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2MTAxMTU0LCJleHAiOjE3NjYxODc1NTR9.7RXGL9taRpdo9ZWMCASfp3x9JzFGZT9FwrtQMrrArRRsLUFyHCFqTZjzUD9DL"
```

**Note:** Le token est retourné directement comme string (pas dans un objet JSON).

**Erreurs possibles:**
- `401 Unauthorized` - Email ou mot de passe incorrect

---

## Endpoints protégés

> ⚠️ **Important:** Tous les endpoints ci-dessous nécessitent un token JWT valide dans le header `Authorization`.

**Format du header:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29t...
```

---

### 4. Récupérer le profil de l'utilisateur connecté

Récupère toutes les informations de l'utilisateur actuellement authentifié.

**URL:** `/api/user/me`

**Méthode:** `GET`

**Authentification requise:** ✅ Oui

**Headers requis:**
```
Authorization: Bearer <votre_token>
```

**Body:** Aucun

**Réponse succès (200 OK) - Médecin:**
```json
{
  "userId": 8,
  "mail": "medecin@example.com",
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1980-05-15",
  "role": "MEDECIN",
  "specialite": "Cardiologue",
  "medecinId": null,
  "id": 7
}
```

**Réponse succès (200 OK) - Patient:**
```json
{
  "userId": 12,
  "mail": "patient@example.com",
  "nom": "Martin",
  "prenom": "Sophie",
  "dateNaissance": "1995-03-20",
  "role": "PATIENT",
  "specialite": null,
  "medecinId": 5,
  "id": 10
}
```

**Champs de la réponse:**
| Champ | Type | Description |
|-------|------|-------------|
| userId | number | ID de l'utilisateur dans la table User |
| mail | string | Email de l'utilisateur |
| nom | string | Nom de famille |
| prenom | string | Prénom |
| dateNaissance | string | Date de naissance (YYYY-MM-DD) |
| role | string | "MEDECIN" ou "PATIENT" |
| specialite | string / null | Spécialité (uniquement pour médecins) |
| medecinId | number / null | ID du médecin traitant (uniquement pour patients) |
| id | number | ID dans la table Medecin ou Patient |

**Erreurs possibles:**
- `401 Unauthorized` - Token manquant, invalide ou expiré
- `404 Not Found` - Utilisateur non trouvé

---

## Codes de statut

| Code | Signification | Description |
|------|---------------|-------------|
| 200 | OK | Requête réussie |
| 400 | Bad Request | Données invalides ou manquantes |
| 401 | Unauthorized | Token manquant, invalide ou expiré |
| 403 | Forbidden | Accès refusé |
| 404 | Not Found | Ressource non trouvée |
| 500 | Internal Server Error | Erreur serveur |

---

## Exemples d'utilisation

### Exemple complet : Inscription → Connexion → Récupération du profil

#### 1. Créer un compte médecin

```bash
curl -X POST http://localhost:8080/api/auth/register/medecin \
  -H "Content-Type: application/json" \
  -d '{
    "mail": "dr.smith@hospital.com",
    "password": "securepass123",
    "nom": "Smith",
    "prenom": "John",
    "dateNaissance": "1975-08-10",
    "specialite": "Neurologie"
  }'
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkci5zbWl0aEBob3NwaXRhbC5jb20iLCJpYXQiOjE3NjYxMDExNTQsImV4cCI6MTc2NjE4NzU1NH0.ABC123..."
}
```

#### 2. Se connecter (alternative)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "mail": "dr.smith@hospital.com",
    "password": "securepass123"
  }'
```

**Réponse:**
```
"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkci5zbWl0aEBob3NwaXRhbC5jb20iLCJpYXQiOjE3NjYxMDExNTQsImV4cCI6MTc2NjE4NzU1NH0.ABC123..."
```

#### 3. Récupérer son profil

```bash
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkci5zbWl0aEBob3NwaXRhbC5jb20iLCJpYXQiOjE3NjYxMDExNTQsImV4cCI6MTc2NjE4NzU1NH0.ABC123..."
```

**Réponse:**
```json
{
  "userId": 15,
  "mail": "dr.smith@hospital.com",
  "nom": "Smith",
  "prenom": "John",
  "dateNaissance": "1975-08-10",
  "role": "MEDECIN",
  "specialite": "Neurologie",
  "medecinId": null,
  "id": 12
}
```

---

### Exemple JavaScript (fetch)

```javascript
// 1. Inscription
async function registerMedecin() {
  const response = await fetch('http://localhost:8080/api/auth/register/medecin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      mail: 'dr.jones@clinic.com',
      password: 'mypassword',
      nom: 'Jones',
      prenom: 'Sarah',
      dateNaissance: '1982-04-25',
      specialite: 'Pédiatrie'
    })
  });
  
  const data = await response.json();
  const token = data.token;
  
  // Sauvegarder le token (localStorage, sessionStorage, context, etc.)
  localStorage.setItem('jwt_token', token);
  
  return token;
}

// 2. Connexion
async function login(email, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      mail: email,
      password: password
    })
  });
  
  const token = await response.text(); // Attention: retourne une string, pas JSON
  localStorage.setItem('jwt_token', token);
  
  return token;
}

// 3. Récupérer le profil
async function getProfile() {
  const token = localStorage.getItem('jwt_token');
  
  const response = await fetch('http://localhost:8080/api/user/me', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  if (!response.ok) {
    throw new Error('Non autorisé');
  }
  
  const profile = await response.json();
  return profile;
}

// Utilisation
async function main() {
  try {
    // S'inscrire
    const token = await registerMedecin();
    console.log('Token reçu:', token);
    
    // Ou se connecter
    // const token = await login('dr.jones@clinic.com', 'mypassword');
    
    // Récupérer le profil
    const profile = await getProfile();
    console.log('Profil:', profile);
    
  } catch (error) {
    console.error('Erreur:', error);
  }
}
```

---

### Exemple React avec Axios

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// Configuration axios avec intercepteur pour le token
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Ajouter automatiquement le token à chaque requête
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Services API
export const authService = {
  // Inscription médecin
  registerMedecin: async (data) => {
    const response = await api.post('/api/auth/register/medecin', data);
    localStorage.setItem('jwt_token', response.data.token);
    return response.data;
  },

  // Inscription patient
  registerPatient: async (data) => {
    const response = await api.post('/api/auth/register/patient', data);
    localStorage.setItem('jwt_token', response.data.token);
    return response.data;
  },

  // Connexion
  login: async (mail, password) => {
    const response = await api.post('/api/auth/login', { mail, password });
    const token = response.data; // C'est une string directement
    localStorage.setItem('jwt_token', token);
    return token;
  },

  // Déconnexion
  logout: () => {
    localStorage.removeItem('jwt_token');
  }
};

export const userService = {
  // Récupérer le profil
  getProfile: async () => {
    const response = await api.get('/api/user/me');
    return response.data;
  }
};

// Utilisation dans un composant React
import React, { useState, useEffect } from 'react';
import { authService, userService } from './api';

function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadProfile() {
      try {
        const data = await userService.getProfile();
        setProfile(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    
    loadProfile();
  }, []);

  if (loading) return <div>Chargement...</div>;
  if (error) return <div>Erreur: {error}</div>;

  return (
    <div>
      <h1>Mon Profil</h1>
      <p>Nom: {profile.nom} {profile.prenom}</p>
      <p>Email: {profile.mail}</p>
      <p>Rôle: {profile.role}</p>
      {profile.role === 'MEDECIN' && (
        <p>Spécialité: {profile.specialite}</p>
      )}
      {profile.role === 'PATIENT' && profile.medecinId && (
        <p>Médecin traitant ID: {profile.medecinId}</p>
      )}
    </div>
  );
}
```

---

## Notes importantes pour le frontend

### 1. Gestion du token
- Sauvegarder le token après connexion/inscription (localStorage, sessionStorage, ou context)
- Inclure le token dans chaque requête aux endpoints protégés
- Supprimer le token à la déconnexion
- Rediriger vers la page de login si le token est expiré (erreur 401)

### 2. Format des dates
- Les dates sont au format ISO 8601 : `YYYY-MM-DD`
- Exemple : `"1990-05-15"`

### 3. Validation côté client
- Email : format email valide
- Password : minimum 6 caractères recommandé
- Date de naissance : pas de date future

### 4. Gestion des erreurs
- Toujours gérer les erreurs 401 (token expiré/invalide)
- Afficher des messages clairs pour les erreurs 400
- Logger les erreurs 500 pour le débogage

### 5. CORS (si nécessaire)
Si votre frontend tourne sur un domaine différent, le backend doit configurer CORS. Ajoutez dans le backend :

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // URL de votre frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

## Changelog

**Version 1.0** (27/12/2024)
- Endpoints d'authentification (inscription médecin/patient, connexion)
- Endpoint de récupération du profil utilisateur
- Authentification JWT avec durée de validité de 24h