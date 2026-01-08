# 📋 Instructions pour créer la base de données

## ⚠️ Problème résolu : "CREATE DATABASE ne peut pas être exécuté dans un bloc de transaction"

Le script a été divisé en **2 fichiers séparés** pour éviter cette erreur.

## 🚀 Méthode rapide (Recommandée)

### Option 1 : Script automatique
```bash
creer_base_complete.bat
```
Ce script exécute automatiquement les 2 étapes.

### Option 2 : Avec pgAdmin

**Étape 1 : Créer la base de données**
1. Ouvrez **pgAdmin**
2. Connectez-vous au serveur PostgreSQL
3. Clic droit sur **Databases** → **Create** → **Database**
4. Nom : `gestion_notes_umaroua`
5. Encoding : `UTF8`
6. Cliquez sur **Save**

**Étape 2 : Créer les tables**
1. Sélectionnez la base `gestion_notes_umaroua`
2. **Tools** → **Query Tool**
3. Ouvrez le fichier `02_creer_tables.sql`
4. Cliquez sur **Execute** (F5)

### Option 3 : Ligne de commande (psql)

**Étape 1 : Créer la base**
```bash
psql -U postgres -f 01_creer_base_donnees.sql
```

**Étape 2 : Créer les tables**
```bash
psql -U postgres -d gestion_notes_umaroua -f 02_creer_tables.sql
```

## 📁 Fichiers créés

1. **`01_creer_base_donnees.sql`** - Crée uniquement la base de données
   - À exécuter en premier
   - Doit être exécuté séparément (pas dans une transaction)

2. **`02_creer_tables.sql`** - Crée les tables et insère les données
   - À exécuter après avoir créé la base
   - Connectez-vous d'abord à la base `gestion_notes_umaroua`

3. **`creer_base_complete.bat`** - Script automatique qui exécute les 2 étapes

## ✅ Vérification

Après création, testez :
```sql
SELECT COUNT(*) FROM etudiants;
```
Vous devriez voir : `5` (étudiants de test)

## 🔧 Pourquoi cette séparation ?

PostgreSQL ne permet pas d'exécuter `CREATE DATABASE` dans une transaction. Quand vous exécutez un script SQL complet, PostgreSQL essaie de tout mettre dans une transaction, ce qui cause l'erreur.

**Solution** : Séparer la création de la base (hors transaction) de la création des tables (dans une transaction).

## 📝 Ancien fichier

Le fichier `creation_base_donnees.sql` original a été corrigé mais il est recommandé d'utiliser les nouveaux fichiers séparés (`01_` et `02_`) pour éviter tout problème.
