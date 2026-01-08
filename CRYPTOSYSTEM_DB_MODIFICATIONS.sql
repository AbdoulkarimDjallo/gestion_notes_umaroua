-- Script SQL pour les modifications de la base de données
-- Université de Maroua - Faculté des Sciences
-- Support du cryptosystème AES-256

-- ======================================
-- Modifications de la table etudiants
-- ======================================
-- Le champ matricule est dorénavant chiffré en base de données
-- Les longueurs ont été augmentées pour stocker le texte chiffré en Base64
-- Augmenter la taille de la colonne matricule pour supporter le chiffrement

ALTER TABLE etudiants 
MODIFY COLUMN matricule VARCHAR(1024) NOT NULL UNIQUE;

-- Créer un index sur le matricule chiffré pour les recherches
-- Note: Les recherches utiliseront le matricule chiffré comme clé
CREATE INDEX idx_matricule_chiffre ON etudiants(matricule);

-- ======================================
-- Modifications de la table notes
-- ======================================
-- Les champs matricule et code_ue sont dorénavant chiffrés

ALTER TABLE notes 
MODIFY COLUMN matricule VARCHAR(1024),
MODIFY COLUMN code_ue VARCHAR(1024);

-- Créer des index sur les colonnes chiffrées
CREATE INDEX idx_notes_matricule ON notes(matricule);
CREATE INDEX idx_notes_code_ue ON notes(code_ue);

-- ======================================
-- Nouvelle table pour l'audit et les logs de sécurité
-- ======================================
-- Optionnel : pour tracer les accès aux données sensibles

CREATE TABLE audit_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utilisateur VARCHAR(255),
    action VARCHAR(255),
    entite VARCHAR(255),
    description TEXT,
    adresse_ip VARCHAR(45),
    statut VARCHAR(50)
);

-- ======================================
-- Vue pour afficher les données déchiffrées (usage administrateur)
-- ======================================
-- Note: Cette vue est à titre informatif
-- En production, le déchiffrement doit se faire à l'application

CREATE VIEW v_etudiants_info AS
SELECT 
    matricule,
    nom,
    prenom,
    departement,
    specialite,
    niveau,
    'Matricule chiffré en base' as note_securite
FROM etudiants;

-- ======================================
-- Procédures stockées sécurisées
-- ======================================

-- Procédure pour compter les étudiants (sans avoir besoin de matricule)
DELIMITER //
CREATE PROCEDURE sp_compter_etudiants()
BEGIN
    SELECT COUNT(*) as total_etudiants FROM etudiants;
END//
DELIMITER ;

-- Procédure pour compter les notes par étudiant (avec matricule chiffré)
DELIMITER //
CREATE PROCEDURE sp_compter_notes(IN p_matricule_chiffre VARCHAR(1024))
BEGIN
    SELECT COUNT(*) as total_notes FROM notes WHERE matricule = p_matricule_chiffre;
END//
DELIMITER ;

-- ======================================
-- Configuration de la sécurité
-- ======================================

-- Créer un utilisateur de base de données dédié (optionnel)
-- Pour PostgreSQL:
-- CREATE USER gestion_notes WITH PASSWORD 'StrongPassword123!';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO gestion_notes;

-- Pour MySQL:
-- CREATE USER 'gestion_notes'@'localhost' IDENTIFIED BY 'StrongPassword123!';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON gestion_notes_db.* TO 'gestion_notes'@'localhost';
-- FLUSH PRIVILEGES;

-- ======================================
-- Notes de sécurité importantes
-- ======================================
/*
1. CHIFFREMENT :
   - Les matricules et codes UE sont chiffrés avec AES-256-CBC
   - L'IV (vecteur d'initialisation) est stocké avec les données chiffrées
   - Les clés de chiffrement sont stockées en dehors de la base de données

2. INTÉGRITÉ DES DONNÉES :
   - Les notes ne sont pas chiffrées (contiennent des chiffres simples)
   - Le déchiffrement est effectué au niveau applicatif

3. SAUVEGARDES :
   - Les clés de chiffrement doivent être sauvegardées séparément
   - Utiliser un système de gestion des secrets (HashiCorp Vault, etc.)

4. GESTION DES CLÉS :
   - Stocker la clé de chiffrement dans un fichier protégé (.keys/)
   - Implémenter la rotation des clés régulièrement
   - Restreindre l'accès aux clés aux administrateurs uniquement

5. AUDIT :
   - Utiliser la table audit_logs pour tracer les accès sensibles
   - Implémenter un système de logs sécurisé

6. CONFORMITÉ :
   - Respecter les normes de protection des données (RGPD, LPDP, etc.)
   - Documenter la politique de sécurité des données
   - Former les utilisateurs à la sécurité des données
*/
