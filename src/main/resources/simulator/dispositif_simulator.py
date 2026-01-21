import requests
import time
import random

# Configuration - Remplacez par votre IP si le backend n'est pas sur la même machine
BASE_URL = "http://localhost:8080/api/v1/cardiac"
PATIENT_ID = "patient_test_01"

def start_session():
    """Initialise une session pour obtenir un activeSessionId côté serveur"""
    print(f"--- Démarrage d'une session pour {PATIENT_ID} ---")
    payload = {"patientId": PATIENT_ID}
    try:
        response = requests.post(f"{BASE_URL}/start", json=payload)
        if response.status_code == 200:
            print(f"Session démarrée avec succès. ID: {response.text}")
            return True
        else:
            print(f"Erreur lors du démarrage: {response.status_code}")
            return False
    except Exception as e:
        print(f"Erreur de connexion : {e}")
        return False

def simulate_pulse():
    """Simule l'envoi de données de pouls en continu"""
    print("--- Début de la simulation du capteur ---")
    print("Appuyez sur Ctrl+C pour arrêter.")

    # Valeur de base pour le BPM
    current_bpm = 75

    try:
        while True:
            # Variation aléatoire légère pour simuler un vrai rythme cardiaque
            current_bpm += random.randint(-2, 2)
            # On reste dans des bornes réalistes (60-120)
            current_bpm = max(60, min(current_bpm, 120))

            # Construction du DTO correspondant à PulseDataDTO(int bpm, String status)
            payload = {
                "bpm": current_bpm,
                "status": "calculating" if random.random() < 0.1 else "stable"
            }

            # Envoi au contrôleur
            response = requests.post(f"{BASE_URL}/receive", json=payload)

            if response.status_code == 200:
                print(f"[SEND] BPM: {current_bpm} | Status: {payload['status']}")
            else:
                print(f"[ERROR] Code: {response.status_code}")

            # Attendre 2 secondes entre chaque mesure (comme le ferait l'Arduino)
            time.sleep(2)

    except KeyboardInterrupt:
        print("\nSimulation arrêtée par l'utilisateur.")

if __name__ == "__main__":
    #if start_session():
        simulate_pulse()