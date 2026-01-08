-- Script pour créer la base de données (à exécuter en premier)
-- Université de Maroua - Faculté des Sciences
--
-- IMPORTANT: Ce script doit être exécuté SÉPARÉMENT, pas dans une transaction
-- Exécutez-le directement dans psql ou pgAdmin Query Tool
--
-- Usage:
--   psql -U postgres -f 01_creer_base_donnees.sql
--   OU dans pgAdmin: Tools → Query Tool → Exécutez ce fichier

-- Supprimer la base si elle existe déjà (optionnel)
DROP DATABASE IF EXISTS gestion_notes_umaroua;

-- Créer la base de données
CREATE DATABASE gestion_notes_umaroua
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'French_France.1252'
    LC_CTYPE = 'French_France.1252'
    TEMPLATE = template0;

-- Message de confirmation
SELECT 'Base de données gestion_notes_umaroua créée avec succès!' as message;
