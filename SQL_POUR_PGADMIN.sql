-- =====================================================
-- SCRIPT SQL COMPLET POUR PGADMIN QUERY TOOL
-- Université de Maroua - Faculté des Sciences
-- =====================================================
-- 
-- INSTRUCTIONS :
-- 1. Dans pgAdmin, créez d'abord la base de données manuellement :
--    - Clic droit sur "Databases" → Create → Database
--    - Nom : gestion_notes_umaroua
--    - Encoding : UTF8
--    - Cliquez sur Save
--
-- 2. Sélectionnez la base "gestion_notes_umaroua"
--
-- 3. Ouvrez Query Tool (Tools → Query Tool)
--
-- 4. Copiez-collez TOUT le contenu ci-dessous et exécutez (F5)
--
-- =====================================================

-- Supprimer les tables si elles existent (respecter les dépendances)
DROP TABLE IF EXISTS logs_acces CASCADE;
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS etudiants CASCADE;
DROP TABLE IF EXISTS crypto_keys CASCADE;

-- =====================================================
-- Table de gestion des clés de chiffrement
-- =====================================================
CREATE TABLE crypto_keys (
    id SERIAL PRIMARY KEY,
    key_name VARCHAR(100) NOT NULL UNIQUE,
    key_data BYTEA NOT NULL,
    key_algorithm VARCHAR(50) NOT NULL DEFAULT 'AES',
    key_size INT NOT NULL DEFAULT 256,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT
);

-- =====================================================
-- Table des étudiants (avec matricule chiffré)
-- =====================================================
CREATE TABLE etudiants (
    id SERIAL PRIMARY KEY,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    matricule_clair VARCHAR(20),  -- Optionnel, pour les recherches optimisées
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    departement VARCHAR(20) NOT NULL CHECK (departement IN ('Maths-Info', 'Physique-Chimie', 'Biologie')),
    specialite VARCHAR(10) NOT NULL CHECK (specialite IN ('INF', 'IGE', 'MAT', 'PHY', 'CHM', 'BIO', 'STE')),
    niveau INT NOT NULL CHECK (niveau BETWEEN 1 AND 5),
    date_inscription TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    est_actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- =====================================================
-- Table des notes (avec matricule et code_ue chiffrés)
-- =====================================================
CREATE TABLE notes (
    id SERIAL PRIMARY KEY,
    matricule VARCHAR(255) NOT NULL,
    matricule_clair VARCHAR(20),  -- Optionnel, pour les recherches optimisées
    code_ue VARCHAR(255) NOT NULL,
    code_ue_clair VARCHAR(20),    -- Optionnel, pour les recherches optimisées
    note NUMERIC(4,2) NOT NULL CHECK (note >= 0 AND note <= 20),
    date_saisie TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (matricule) REFERENCES etudiants(matricule) ON DELETE CASCADE,
    UNIQUE (matricule, code_ue)
);

-- =====================================================
-- Table de logs d'accès pour l'audit de sécurité
-- =====================================================
CREATE TABLE logs_acces (
    id SERIAL PRIMARY KEY,
    utilisateur VARCHAR(100),
    action VARCHAR(50) NOT NULL,
    table_affectee VARCHAR(50) NOT NULL,
    donnees_affectees TEXT,
    statut VARCHAR(20) NOT NULL DEFAULT 'SUCCES' CHECK (statut IN ('SUCCES', 'ERREUR', 'TENTATIVE_NON_AUTORISEE')),
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresse_ip VARCHAR(50),
    details_erreur TEXT
);

-- =====================================================
-- Créer les index pour optimiser les performances
-- =====================================================

-- Index sur la table des étudiants
CREATE INDEX idx_etudiants_matricule_clair ON etudiants(matricule_clair);
CREATE INDEX idx_etudiants_departement ON etudiants(departement);
CREATE INDEX idx_etudiants_specialite ON etudiants(specialite);
CREATE INDEX idx_etudiants_niveau ON etudiants(niveau);
CREATE INDEX idx_etudiants_actif ON etudiants(est_actif);

-- Index sur la table des notes
CREATE INDEX idx_notes_matricule ON notes(matricule);
CREATE INDEX idx_notes_matricule_clair ON notes(matricule_clair);
CREATE INDEX idx_notes_code_ue_clair ON notes(code_ue_clair);
CREATE INDEX idx_notes_date_saisie ON notes(date_saisie);

-- Index sur la table des logs
CREATE INDEX idx_logs_action ON logs_acces(action);
CREATE INDEX idx_logs_date ON logs_acces(date_action);
CREATE INDEX idx_logs_utilisateur ON logs_acces(utilisateur);

-- Index sur la table des clés
CREATE INDEX idx_crypto_keys_actif ON crypto_keys(is_active);
CREATE INDEX idx_crypto_keys_creation ON crypto_keys(creation_date);

-- =====================================================
-- Insérer la clé de chiffrement par défaut
-- =====================================================
INSERT INTO crypto_keys (key_name, key_data, key_algorithm, key_size, description)
VALUES (
    'default_key',
    E'\\x0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef',
    'AES',
    256,
    'Clé de chiffrement par défaut pour le système'
);

-- =====================================================
-- Insérer les données de test
-- =====================================================

-- Données d'étudiants en clair pour tester
INSERT INTO etudiants (matricule, matricule_clair, nom, prenom, departement, specialite, niveau) VALUES
('UMA_20FS001', '20FS001', 'NGONO', 'Jean Paul', 'Maths-Info', 'INF', 1),
('UMA_20FS002', '20FS002', 'ATEBA', 'Marie Louise', 'Maths-Info', 'MAT', 2),
('UMA_20FS003', '20FS003', 'KAMGA', 'Patrick', 'Physique-Chimie', 'PHY', 3),
('UMA_20FS004', '20FS004', 'MBALLA', 'Sophie', 'Biologie', 'BIO', 1),
('UMA_20FS005', '20FS005', 'TCHOUPE', 'André', 'Physique-Chimie', 'CHM', 2);

-- Données de notes en clair pour tester
INSERT INTO notes (matricule, matricule_clair, code_ue, code_ue_clair, note) VALUES
('UMA_20FS001', '20FS001', 'UMA_INF111', 'INF111', 15.50),
('UMA_20FS001', '20FS001', 'UMA_INF121', 'INF121', 17.00),
('UMA_20FS001', '20FS001', 'UMA_MAT111', 'MAT111', 14.25),
('UMA_20FS002', '20FS002', 'UMA_MAT111', 'MAT111', 18.00),
('UMA_20FS002', '20FS002', 'UMA_MAT121', 'MAT121', 16.50),
('UMA_20FS003', '20FS003', 'UMA_PHY111', 'PHY111', 13.75),
('UMA_20FS004', '20FS004', 'UMA_BIO111', 'BIO111', 15.00),
('UMA_20FS005', '20FS005', 'UMA_CHI111', 'CHI111', 12.50);

-- =====================================================
-- Vérification : Afficher un résumé
-- =====================================================
SELECT '✓ Base de données créée avec succès!' as message;
SELECT COUNT(*) as nombre_etudiants FROM etudiants;
SELECT COUNT(*) as nombre_notes FROM notes;
SELECT COUNT(*) as nombre_cles FROM crypto_keys;
SELECT COUNT(*) as nombre_logs FROM logs_acces;
