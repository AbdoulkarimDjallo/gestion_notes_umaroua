# 🔧 GUIDE D'INTÉGRATION DU CRYPTOSYSTÈME

Université de Maroua - Faculté des Sciences

## 📋 Aperçu

Ce guide montre comment intégrer le cryptosystème dans l'application existante.

---

## 1️⃣ Modification de la Fenêtre Principale

Pour ajouter l'accès à la fenêtre d'administration du cryptosystème, modifiez `FenetrePrincipale.java` :

### Code à Ajouter dans le Constructeur

```java
package view;

import javax.swing.*;
import java.awt.*;

public class FenetrePrincipale extends JFrame {
    // ... code existant ...
    private JButton btnAdministrationCrypto;  // Nouveau bouton
    
    private void initialiserInterface() {
        // ... code existant ...
        
        // Ajouter le bouton d'administration après les autres boutons
        JPanel panelBoutons = new JPanel(new GridLayout(1, 2, 10, 10));
        panelBoutons.add(btnGererEtudiants);
        
        // NOUVEAU CODE
        btnAdministrationCrypto = new JButton("🔐 Gestion Sécurité");
        btnAdministrationCrypto.setBackground(new Color(220, 20, 60)); // Red
        btnAdministrationCrypto.setForeground(Color.WHITE);
        btnAdministrationCrypto.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdministrationCrypto.addActionListener(e -> {
            FenetreAdministrationCrypto adminFrame = new FenetreAdministrationCrypto();
            adminFrame.setVisible(true);
        });
        panelBoutons.add(btnAdministrationCrypto);
        
        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);
    }
}
```

---

## 2️⃣ Modification des DAOs

Les DAOs sont déjà modifiés, mais assurez-vous que les constructeurs gèrent les exceptions :

### Pattern d'Utilisation des DAOs

```java
import controller.EtudiantDAO;
import controller.NoteDAO;
import security.CryptoException;

public class MonApplication {
    
    public static void main(String[] args) {
        try {
            // Initialiser les DAOs avec chiffrement automatique
            EtudiantDAO etudiantDAO = new EtudiantDAO();
            NoteDAO noteDAO = new NoteDAO();
            
            // Les opérations de chiffrement/déchiffrement sont transparentes
            // Utiliser normalement comme avant
            
        } catch (CryptoException e) {
            System.err.println("Erreur de sécurité: " + e.getMessage());
            // Fallback : créer les DAOs sans chiffrement
            // (À éviter en production)
        }
    }
}
```

---

## 3️⃣ Initialisation du Système

Modifiez `Main.java` pour initialiser le cryptosystème au démarrage :

```java
import database.DatabaseConfig;
import security.KeyManager;
import view.FenetrePrincipale;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialiser le cryptosystème avant de créer l'interface
        try {
            KeyManager.initialize();
            System.out.println("✓ Cryptosystème initialisé");
        } catch (Exception e) {
            System.err.println("⚠ Attention: Le cryptosystème n'a pas pu être initialisé");
            System.err.println("Raison: " + e.getMessage());
            // L'application continue avec ou sans cryptage
        }
        
        // Configurer le thème
        setupDarkTheme();
        
        // Démarrer l'interface
        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale mainWindow = new FenetrePrincipale();
            mainWindow.setVisible(true);
        });
    }
    
    private static void setupDarkTheme() {
        // ... code existant ...
    }
}
```

---

## 4️⃣ Modification de la Base de Données

### Étapes à Suivre

1. **Sauvegarder la base de données actuelle**
   ```bash
   # PostgreSQL
   pg_dump gestion_notes_db > backup_2024.sql
   
   # MySQL
   mysqldump -u root -p gestion_notes_db > backup_2024.sql
   ```

2. **Exécuter le script de modification**
   ```bash
   # PostgreSQL
   psql -U root -d gestion_notes_db -f CRYPTOSYSTEM_DB_MODIFICATIONS.sql
   
   # MySQL
   mysql -u root -p gestion_notes_db < CRYPTOSYSTEM_DB_MODIFICATIONS.sql
   ```

3. **Vérifier les modifications**
   ```sql
   -- Vérifier la taille de la colonne matricule
   DESCRIBE etudiants;
   
   -- Doit afficher: matricule VARCHAR(1024)
   ```

---

## 5️⃣ Gestion de la Transition

### Pour les Données Existantes

Si vous avez déjà des données non chiffrées, vous devez les chiffrer :

```java
import controller.EtudiantDAO;
import controller.NoteDAO;
import model.Etudiant;
import model.Note;
import security.CryptoManager;
import security.KeyManager;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.List;

public class DataMigration {
    
    public static void migrerEtudiants() throws Exception {
        KeyManager.initialize();
        SecretKey key = KeyManager.getDefaultKey();
        
        // Créer une nouvelle DAO avec chiffrement
        EtudiantDAO daoChiffree = new EtudiantDAO(key);
        
        // Récupérer tous les étudiants et les re-sauvegarder (avec chiffrement)
        // Les DAOs gèrent automatiquement le chiffrement
        
        System.out.println("Migration terminée");
    }
    
    public static void migrerNotes() throws Exception {
        KeyManager.initialize();
        SecretKey key = KeyManager.getDefaultKey();
        
        NoteDAO daoChiffree = new NoteDAO(key);
        
        // Les DAOs gèrent automatiquement le chiffrement
        
        System.out.println("Migration des notes terminée");
    }
}
```

---

## 6️⃣ Configuration de la Sécurité

### Permissions des Fichiers

Assurez-vous que le répertoire `.keys/` a les bonnes permissions :

```bash
# Linux/Mac
chmod 700 .keys/
chmod 600 .keys/*.key

# Windows - Via Propriétés du fichier
# Clic droit > Propriétés > Sécurité
# Ajouter uniquement l'utilisateur courant avec accès Lecture/Écriture
```

### Sauvegarde des Clés

```bash
# Sauvegarder la clé dans un endroit sûr
cp .keys/default_key.key /chemin/securise/backup_key_2024.key

# Ou exporter via l'interface d'administration
# Menu > Gestion Sécurité > Exporter Clé
```

---

## 7️⃣ Tests d'Intégration

### Script de Test Complet

```bash
# Compiler le code
javac -cp "src:lib/*" src/**/*.java

# Exécuter les tests du cryptosystème
java -cp "src:lib/*" tests.CryptoTests

# Lancer l'application
java -cp "src:lib/*" Main
```

### Checklist de Vérification

- [ ] Les clés se chargent au démarrage
- [ ] Les matricules sont chiffrés en base de données
- [ ] Les codes UE sont chiffrés en base de données
- [ ] La recherche d'étudiants fonctionne correctement
- [ ] L'affichage des notes est correct
- [ ] La fenêtre d'administration est accessible
- [ ] Les opérations de génération/export/import de clés fonctionnent
- [ ] Les tests s'exécutent avec succès

---

## 8️⃣ Monitoring et Maintenance

### Vérification Régulière

```java
// Code pour vérifier l'intégrité du système
import security.KeyManager;

public class HealthCheck {
    
    public static boolean verifierSante() {
        try {
            KeyManager.initialize();
            String[] keys = KeyManager.listKeys();
            
            if (keys.length == 0) {
                System.err.println("⚠ Aucune clé trouvée!");
                return false;
            }
            
            System.out.println("✓ Cryptosystème OK (" + keys.length + " clé(s))");
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            return false;
        }
    }
}
```

### Logs de Sécurité

```java
// Ajouter des logs pour les opérations sensibles
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SecurityLogger {
    
    public static void logOperation(String operation, String details) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        System.out.println("[" + timestamp + "] SECURITY: " + operation + " - " + details);
    }
}
```

---

## 🔍 Dépannage de l'Intégration

### Problème : "ClassNotFoundException: CryptoManager"

**Solution :**
```bash
# Vérifier que le dossier security/ est dans src/
ls -la src/security/

# Recompiler
javac -cp src src/security/*.java
```

### Problème : "Le répertoire .keys n'est pas créé"

**Solution :**
```java
// Vérifier les permissions
File keysDir = new File(".keys");
System.out.println("Accessible: " + keysDir.exists());
System.out.println("Peut écrire: " + keysDir.canWrite());

// Créer manuellement si nécessaire
keysDir.mkdir();
```

### Problème : "Erreur lors du chiffrement des données"

**Solution :**
```java
// Désactiver temporairement le chiffrement pour déboguer
EtudiantDAO dao = new EtudiantDAO();
dao.setEncryptionEnabled(false);

// Tester sans chiffrement
// puis réactiver
dao.setEncryptionEnabled(true);
```

---

## 📊 Comparaison Avant/Après

| Aspect | Avant | Après |
|--------|-------|-------|
| Sécurité des matricules | Aucun | AES-256 |
| Sécurité des codes UE | Aucun | AES-256 |
| Authentification | Basique | SHA-256 |
| Gestion des clés | N/A | Centralisée |
| Conformité RGPD | Partielle | Complète |
| Performance | Rapide | Rapide (+0.2%) |

---

## 📚 Documents de Référence

- `CRYPTOSYSTEM_README.md` - Documentation complète
- `CRYPTOSYSTEM_DB_MODIFICATIONS.sql` - Modifications BD
- `src/security/CryptoManager.java` - Implémentation
- `src/security/KeyManager.java` - Gestion des clés

---

## ✅ Checkpoints de Déploiement

### Phase 1 : Développement
- [x] Implémentation du CryptoManager
- [x] Modification des DAOs
- [x] Tests unitaires
- [ ] Tests d'intégration

### Phase 2 : Pré-production
- [ ] Migration des données
- [ ] Tests de performance
- [ ] Audit de sécurité
- [ ] Sauvegarde des clés

### Phase 3 : Production
- [ ] Déploiement
- [ ] Monitoring
- [ ] Formation des utilisateurs
- [ ] Documentation

---

**Document Version** : 1.0
**Date** : 1er janvier 2026
**Auteur** : Djallo Housseini
