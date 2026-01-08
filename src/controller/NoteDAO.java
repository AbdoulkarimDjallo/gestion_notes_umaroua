package controller;

import database.DatabaseConfig;
import model.Note;
import security.CryptoException;
import security.CryptoManager;
import security.KeyManager;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des notes avec cryptage
 * Les matricules et les notes sont chiffrés lors de l'insertion et déchiffrés lors de la lecture
 */
public class NoteDAO {
    
    private SecretKey encryptionKey;
    private boolean enableEncryption = true;
    
    /**
     * Constructeur par défaut - initialise le gestionnaire de clés et charge la clé par défaut
     * @throws CryptoException Si l'initialisation échoue
     */
    public NoteDAO() throws CryptoException {
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
    public NoteDAO(SecretKey customKey) {
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
     * Récupère toutes les notes d'un étudiant
     * Les matricules et codes UE chiffrés sont déchiffrés automatiquement
     * @param matricule Le matricule de l'étudiant
     * @return Liste des notes de l'étudiant
     * @throws SQLException en cas d'erreur de base de données
     */
    public List<Note> obtenirNotesParMatricule(String matricule) throws SQLException {
        List<Note> notes = new ArrayList<>();
        
        String matriculeRecherche = matricule;
        
        // Chiffrer le matricule pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
            } catch (CryptoException e) {
                System.err.println("Erreur lors du chiffrement du matricule: " + e.getMessage());
            }
        }
        
        String sql = "SELECT * FROM notes WHERE matricule = ? ORDER BY code_ue";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String codeUE = rs.getString("code_ue");
                    
                    // Déchiffrer le code UE si le chiffrement est activé
                    if (enableEncryption && encryptionKey != null) {
                        try {
                            codeUE = CryptoManager.decrypt(codeUE, encryptionKey);
                        } catch (CryptoException e) {
                            // Données probablement en clair, utiliser telles quelles
                            System.err.println("[WARN] Code UE non chiffré détecté, utilisation en clair");
                        }
                    }
                    
                    Note note = new Note(
                        rs.getInt("id"),
                        matricule,
                        codeUE,
                        rs.getDouble("note")
                    );
                    notes.add(note);
                }
            }
        }
        
        return notes;
    }
    
    /**
     * Ajoute une nouvelle note dans la base de données
     * Les matricules et codes UE sont chiffrés avant l'insertion
     * @param note La note à ajouter
     * @return true si l'ajout a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean ajouterNote(Note note) throws SQLException {
        // Vérifier que la combinaison matricule + code_ue n'existe pas déjà
        if (rechercherNote(note.getMatricule(), note.getCodeUE()) != null) {
            throw new SQLException("Une note existe déjà pour cet étudiant et cette UE");
        }
        
        String matriculeAInserer = note.getMatricule();
        String codeUEAInserer = note.getCodeUE();
        
        // Chiffrer les données si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeAInserer = CryptoManager.encrypt(note.getMatricule(), encryptionKey);
                codeUEAInserer = CryptoManager.encrypt(note.getCodeUE(), encryptionKey);
            } catch (CryptoException e) {
                throw new SQLException("Erreur lors du chiffrement des données: " + e.getMessage(), e);
            }
        }
        
        String sql = "INSERT INTO notes (matricule, code_ue, note) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, matriculeAInserer);
            pstmt.setString(2, codeUEAInserer);
            pstmt.setDouble(3, note.getNote());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        note.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Recherche une note spécifique
     * @param matricule Le matricule de l'étudiant
     * @param codeUE Le code de l'UE
     * @return La note trouvée ou null
     * @throws SQLException en cas d'erreur de base de données
     */
    public Note rechercherNote(String matricule, String codeUE) throws SQLException {
        String matriculeRecherche = matricule;
        String codeUERecherche = codeUE;
        
        // Chiffrer les données pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
                codeUERecherche = CryptoManager.encrypt(codeUE, encryptionKey);
            } catch (CryptoException e) {
                System.err.println("Erreur lors du chiffrement pour la recherche: " + e.getMessage());
            }
        }
        
        String sql = "SELECT * FROM notes WHERE matricule = ? AND code_ue = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            pstmt.setString(2, codeUERecherche);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Note(
                        rs.getInt("id"),
                        matricule,
                        codeUE,
                        rs.getDouble("note")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Met à jour une note existante
     * @param note La note avec les nouvelles valeurs
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean modifierNote(Note note) throws SQLException {
        String sql = "UPDATE notes SET note = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, note.getNote());
            pstmt.setInt(2, note.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Supprime une note
     * @param id L'identifiant de la note
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur de base de données
     */
    public boolean supprimerNote(int id) throws SQLException {
        String sql = "DELETE FROM notes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Calcule la moyenne générale d'un étudiant
     * @param matricule Le matricule de l'étudiant
     * @return La moyenne ou 0 si aucune note
     * @throws SQLException en cas d'erreur de base de données
     */
    public double calculerMoyenne(String matricule) throws SQLException {
        String matriculeRecherche = matricule;
        
        // Chiffrer le matricule pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
            } catch (CryptoException e) {
                System.err.println("Erreur lors du chiffrement du matricule: " + e.getMessage());
            }
        }
        
        String sql = "SELECT AVG(note) as moyenne FROM notes WHERE matricule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("moyenne");
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * Compte le nombre de notes d'un étudiant
     * @param matricule Le matricule de l'étudiant
     * @return Le nombre de notes
     * @throws SQLException en cas d'erreur de base de données
     */
    public int compterNotes(String matricule) throws SQLException {
        String matriculeRecherche = matricule;
        
        // Chiffrer le matricule pour la recherche si le chiffrement est activé
        if (enableEncryption && encryptionKey != null) {
            try {
                matriculeRecherche = CryptoManager.encrypt(matricule, encryptionKey);
            } catch (CryptoException e) {
                System.err.println("Erreur lors du chiffrement du matricule: " + e.getMessage());
            }
        }
        
        String sql = "SELECT COUNT(*) FROM notes WHERE matricule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matriculeRecherche);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
}