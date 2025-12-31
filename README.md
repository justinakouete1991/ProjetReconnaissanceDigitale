# 🔍 Fingerprint Recognition System

Projet collaboratif de *reconnaissance d'empreintes digitales* développé en *Java* et *MATLAB*.  
Ce système compare deux empreintes à partir d'une interface graphique Java connectée au moteur MATLAB pour un traitement de matching et d’analyse avancée.

---

## 🚀 Description du projet

Le projet repose sur une architecture modulaire répartie en trois parties principales :

1. *Interface graphique (GUI)* – permet de charger deux empreintes et d’afficher le résultat final.  
2. *Pré-traitement* – prépare les empreintes avant comparaison (filtrage, seuillage, détection des minuties, etc.).  
3. *Matching et comparaison (MATLAB)* – réalise la correspondance entre empreintes et calcule le taux de similarité.

---

## 🧠 Fonctionnalités principales

- 📁 Chargement de deux empreintes depuis l’interface Java ; 
- ⚙ Pré-traitement automatique des images ; 
- 🔗 Communication Java ↔ MATLAB (via MATLAB Engine for Java);  
- 📊 Calcul du taux de correspondance entre empreintes;  
- 💬 Affichage du score final dans l’interface graphique;  

---

## 🛠 Technologies utilisées

| Composant | Technologie |
|------------|--------------|
| Interface graphique | Java (Swing / JavaFX) |
| Traitement d'image | MATLAB |
| Communication | MATLAB Engine for Java |
| Gestion de version | Git & GitHub |
| IDE principal | IntelliJ IDEA |

---

## 👥 Équipe de développement

| Nom | Rôle | Responsabilité principale |
|------|------|-----------------------------|
| *Justin Akouete* | Coordinateur du projet | Supervision, intégration et organisation des branches |
| *Powell* | Développeur Java | Interface graphique de chargement et affichage des empreintes |
| *Jolidon* | Développeur Java | Module de pré-traitement des empreintes |
| *Espero* | Développeur MATLAB | Module de matching et comparaison des empreintes |

---

## 📦 Installation et utilisation

1. *Cloner le dépôt*
   ```bash
   git clone https://github.com/justinakouete1991/fingerprint-matching-project.git
   cd fingerprint-matching-project
