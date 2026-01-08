package controller;

import database.DatabaseConfig;
import model.Etudiant;
import security.CryptoException;
import security.CryptoManager;
import security.KeyManager;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des étudiants avec cryptage
 * Les matricules sont chiffrés lors de l'insertion et déchiffrés lors de la lecture
 */
public class EtudiantDAO {
    
    private SecretKey encryptionKey;
    private boolean enableEncryption = true;
    
    /**
     * Constructeur par défaut - initialise le gestionnaire de clés et charge la clé par défaut
     * @throws CryptoException Si l'initialisation échoue
     */
    public EtudiantDAO() throws CryptoException {
        try {
            KeyManager.initialize();
            this.encryptionKey = KeyManager.getDefaultKey();
            // Désactiver le chiffrement par défaut pour compatibilité avec les données existantes
            this.enableEncryption = false;
            System.out.println("[INFO] Mode chiffrement désactivé pour compatibilité données existantes");
        } catch (CryptoException e) {
            System.err.println("Avertissement: Impossible de charger les clés de chiffrement: " + e.getMessage());
            this.enableEncryption = false;
        }
    }
    
    /**
     * Constructeur avec clé de chiffrement spécifique
     * @param customKey La clé à utiliser
     */
    public EtudiantDAO(SecretKey customKey) {
        this.encryptionKey = customKey;
        this.enableEncryption = true;
    }
    
    /**
     * Active ou désactive le chiffrement
     * @param enable true pour activer, false pour désactiver
     */
    public void setEncryptionEnabled(boolean enable) {
        this.enableEncryption = enable;
    }
    
    /**
     * Récupère tous les étudiants de la base de données
     * Les matricules chiffrés sont déchiffrés automatiquement
     * @return Liste de tous les étudiants
     * @throws SQLException en cas d'erreur de base de données
     */
    public List<Etudiant> obtenirTousLesEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String matricule = rs.getString("matricule");
                
                // Déchiffrer le matricule si le chiffrement est activé
                if (enableEncryption && encryptionKey != null) {
                    try {
                        matricule = CryptoManager.decrypt(matricule, encryptionKey);
                    } catch (CryptoException e) {
                        // Données probablement en clair, utiliser telles quelles
                        System.err.println("[WARN] Matricule non chiffré détecté, utilisation en clair");
                    }
                }
                
                Etudiant etudiant = new Etudiant(
                    matricule,
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("departement"),
                    rs.getString("specialite"),
                    rs.getInt("niveau")
                );
                etudiants.add(etudiant);
            }
        }
        
        return etudiants;
    }
    
    /**
     * Récupère les étudiants filtrés par département, spécialité et niveau
     * @param departement Le département (peut être null pour tous)
     * @param specialite La spécialité (peut être null pour tous)
     * @param niveau Le niveau (0 pour tous)
     * @return Liste des étudiants filtrés
     * @throws SQLException en cas d'erreur de base de données
     */
    public List<Etudiant> obtenirEtudiantsFiltres(String departement, String specialite, int niveau) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM etudiants WHERE 1=1");
        
        if (departement != null && !departement.trim().isEmpty()) {
            sql.append(" AND departement = ?");
        }
        if (specialite != null && !specialite.trim().isEmpty()) {
            sql.append(" AND specialite = ?");
        }
        if (niveau > 0) {
            sql.append(" AND niveau = ?");
        }
        sql.append(" ORDER BY nom, prenom");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (departement != null && !departement.trim().isEmpty()) {
                pstmt.setString(paramIndex++, departement);
            }
            if (specialite != null && !specialite.trim().isEmpty()) {
                pstmt.setString(paramIndex++, specialite);
            }
            if (niveau > 0) {
                pstmt.setInt(paramIndex++, niveau);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String matricule = rs.getString("matricule");
                    
                    // Déchiffrer le matricule si le chiffrement est activé
                    if (enableEncryption && encryptionKey != null) {
                        try {
                            matricule = CryptoManager.decrypt(matricule, encryptionKey);
                        } catch (CryptoException e) {
                            // Données probablement en clair, utiliser telles quelles
                            System.err.println("[WARN] Matricule non chiffré détecté, utilisation en clair");
                        }
                    }
                    
                    Etudiant etudiant = new Etudiant(
                        matricule,
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("departement"),
                        rs.getString("specialite"),
                        rs.getInt("niveau")
                    );
                    etudiants.add(etudiant);
                }
            }
        }
        
        return etudiants;
    }
    
    /**
     * Recherche un étudiant par son matricule
     * @param matricule Le matricule à rechercher
     * @return L'étudiant trouvé ou null
     * @throws SQLException en cas d'erreur de base de données
     */
    public Etudiant rechercherParMatricule(String matricule) throws SQLException {
        String matriculeRecherche = matricule;
        
        // Chiffrer le matricule pour la recherche en base si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
            } catch (CryptoException e) {
                System.err.println("Erreur lors du chiffrement du matricule: " + e.getMessage());
            }
        }
        
        String sql = "SELECT * FROM etudiants WHERE matricule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Etudiant(
                        matricule,
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("departement"),
                        rs.getString("specialite"),
                        rs.getInt("niveau")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Ajoute un nouvel étudiant dans la base de données
     * Le matricule est chiffré avant l'insertion
     * @param etudiant L'étudiant à ajouter
     * @return true si l'ajout a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean ajouterEtudiant(Etudiant etudiant) throws SQLException {
        // Vérifier que l'étudiant n'existe pas déjà
        if (rechercherParMatricule(etudiant.getMatricule()) != null) {
            throw new SQLException("Un étudiant avec ce matricule existe déjà");
        }
        
        String matriculeAInserer = etudiant.getMatricule();
        
        // Chiffrer le matricule si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeAInserer = CryptoManager.encrypt(etudiant.getMatricule(), encryptionKey);
            } catch (CryptoException e) {
                throw new SQLException("Erreur lors du chiffrement du matricule: " + e.getMessage(), e);
            }
        }
        
        String sql = "INSERT INTO etudiants (matricule, nom, prenom, departement, specialite, niveau) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeAInserer);
            pstmt.setString(2, etudiant.getNom());
            pstmt.setString(3, etudiant.getPrenom());
            pstmt.setString(4, etudiant.getDepartement());
            pstmt.setString(5, etudiant.getSpecialite());
            pstmt.setInt(6, etudiant.getNiveau());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Met à jour les informations d'un étudiant
     * @param etudiant L'étudiant avec les nouvelles informations
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean modifierEtudiant(Etudiant etudiant) throws SQLException {
        String matriculeRecherche = etudiant.getMatricule();
        
        // Chiffrer le matricule pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(etudiant.getMatricule(), encryptionKey);
            } catch (CryptoException e) {
                throw new SQLException("Erreur lors du chiffrement du matricule: " + e.getMessage(), e);
            }
        }
        
        String sql = "UPDATE etudiants SET nom = ?, prenom = ?, departement = ?, specialite = ?, niveau = ? WHERE matricule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getPrenom());
            pstmt.setString(3, etudiant.getDepartement());
            pstmt.setString(4, etudiant.getSpecialite());
            pstmt.setInt(5, etudiant.getNiveau());
            pstmt.setString(6, matriculeRecherche);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Supprime un étudiant de la base de données
     * @param matricule Le matricule de l'étudiant à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean supprimerEtudiant(String matricule) throws SQLException {
        String matriculeRecherche = matricule;
        
        // Chiffrer le matricule pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
            } catch (CryptoException e) {
                throw new SQLException("Erreur lors du chiffrement du matricule: " + e.getMessage(), e);
            }
        }
        
        String sql = "DELETE FROM etudiants WHERE matricule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Compte le nombre total d'étudiants
     * @return Le nombre d'étudiants
     * @throws SQLException en cas d'erreur de base de données
     */
    public int compterEtudiants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM etudiants";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
}