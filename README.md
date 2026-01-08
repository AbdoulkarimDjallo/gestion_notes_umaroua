# 🎓 Gestion des Notes - Université de Maroua

Système de gestion des notes avec cryptosystème AES-256 pour la Faculté des Sciences.

## 📋 Structure du projet

```
GestionNotesUMaroua/
├── src/                    # Code source Java
│   ├── Main.java          # Point d'entrée de l'application
│   ├── model/             # Modèles de données (Etudiant, Note)
│   ├── controller/        # DAO (EtudiantDAO, NoteDAO)
│   ├── database/          # Configuration base de données
│   ├── security/          # Cryptographie (CryptoManager, KeyManager)
│   ├── view/              # Interfaces graphiques
│   └── tests/             # Tests unitaires
├── lib/                   # Bibliothèques externes
│   ├── flatlaf-*.jar      # Interface graphique moderne
│   └── postgresql-*.jar   # Driver PostgreSQL
├── out/                   # Fichiers compilés (.class)
├── 01_creer_base_donnees.sql    # Script SQL : Créer la base
├── 02_creer_tables.sql          # Script SQL : Créer les tables
├── creer_base_complete.bat      # Script automatique de création
├── compile.bat                  # Script de compilation
└── run.bat                      # Script d'exécution
```

## 🚀 Démarrage rapide

### 1. Créer la base de données

**Option A : Script automatique (Recommandé)**
```bash
creer_base_complete.bat
```

**Option B : Manuel avec pgAdmin**
1. Ouvrez pgAdmin
2. Créez la base : `gestion_notes_umaroua` (Encoding: UTF8)
3. Sélectionnez la base → Tools → Query Tool
4. Exécutez `02_creer_tables.sql`

### 2. Compiler l'application

```bash
compile.bat
```

### 3. Lancer l'application

```bash
run.bat
```

## ⚙️ Configuration

### Base de données

Modifiez les paramètres dans `src/database/DatabaseConfig.java` :
- **URL** : `jdbc:postgresql://localhost:5432/gestion_notes_umaroua`
- **USER** : `postgres`
- **PASSWORD** : `5596` (modifiez si nécessaire)

### Cryptographie

Les clés de chiffrement sont stockées dans le dossier `.keys/` (créé automatiquement).

## 📚 Documentation

- `INSTRUCTIONS_CREATION_DB.md` - Guide de création de la base de données
- `CRYPTOSYSTEM_README.md` - Documentation du système de cryptographie
- `CRYPTOSYSTEM_INTEGRATION_GUIDE.md` - Guide d'intégration

## 🔐 Sécurité

- **Chiffrement** : AES-256-CBC pour les matricules et codes UE
- **Clés** : Stockées dans `.keys/` (protégé)
- **Audit** : Table `logs_acces` pour tracer les accès

## 📝 Fonctionnalités

- ✅ Gestion des étudiants (CRUD)
- ✅ Gestion des notes
- ✅ Chiffrement des données sensibles
- ✅ Interface graphique moderne (FlatLaf)
- ✅ Filtrage par département, spécialité, niveau

## 🛠️ Technologies

- **Java** : Langage de programmation
- **PostgreSQL** : Base de données
- **FlatLaf** : Interface graphique moderne
- **AES-256** : Cryptographie

## 👥 Auteurs

Djallo - Housseini  
Université de Maroua - Faculté des Sciences
