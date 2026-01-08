package security;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * Gestionnaire sécurisé des clés de chiffrement
 * Stocke et récupère les clés dans un fichier protégé
 * 
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class KeyManager {
    
    private static final String KEY_DIRECTORY = ".keys";
    private static final String DEFAULT_KEY_FILE = "default_key.key";
    private static SecretKey cachedKey = null;
    
    /**
     * Initialise le gestionnaire de clés
     * @throws CryptoException Si l'initialisation échoue
     */
    public static void initialize() throws CryptoException {
        try {
            // Créer le répertoire des clés s'il n'existe pas
            Path keyDir = Paths.get(KEY_DIRECTORY);
            if (!Files.exists(keyDir)) {
                Files.createDirectory(keyDir);
                
                // Définir les permissions (lecture/écriture uniquement par le propriétaire)
                try {
                    Files.setPosixFilePermissions(keyDir, 
                        PosixFilePermissions.fromString("rwx------"));
                } catch (UnsupportedOperationException e) {
                    // Les permissions POSIX ne sont pas supportées sur Windows
                    // Utiliser les permissions Windows à la place
                    keyDir.toFile().setReadable(true, true);
                    keyDir.toFile().setWritable(true, true);
                    keyDir.toFile().setExecutable(true, true);
                }
            }
            
            // Charger ou créer la clé par défaut
            loadOrCreateDefaultKey();
            
        } catch (IOException e) {
            throw new CryptoException("Erreur lors de l'initialisation du gestionnaire de clés: " + e.getMessage(), e);
        }
    }
    
    /**
     * Charge ou crée la clé par défaut
     * @throws CryptoException Si l'opération échoue
     */
    private static void loadOrCreateDefaultKey() throws CryptoException {
        Path keyFile = Paths.get(KEY_DIRECTORY, DEFAULT_KEY_FILE);
        
        if (Files.exists(keyFile)) {
            cachedKey = loadKey(DEFAULT_KEY_FILE);
        } else {
            SecretKey newKey = CryptoManager.generateKey();
            saveKey(DEFAULT_KEY_FILE, newKey);
            cachedKey = newKey;
        }
    }
    
    /**
     * Récupère la clé par défaut (en cache)
     * @return La clé par défaut
     * @throws CryptoException Si la clé n'est pas disponible
     */
    public static SecretKey getDefaultKey() throws CryptoException {
        if (cachedKey == null) {
            initialize();
        }
        return cachedKey;
    }
    
    /**
     * Sauvegarde une clé dans un fichier
     * @param filename Le nom du fichier
     * @param key La clé à sauvegarder
     * @throws CryptoException Si la sauvegarde échoue
     */
    public static void saveKey(String filename, SecretKey key) throws CryptoException {
        try {
            Path keyFile = Paths.get(KEY_DIRECTORY, filename);
            String keyString = CryptoManager.keyToString(key);
            
            Files.write(keyFile, keyString.getBytes());
            
            // Définir les permissions d'accès
            try {
                Files.setPosixFilePermissions(keyFile, 
                    PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException e) {
                // Windows
                keyFile.toFile().setReadable(true, true);
                keyFile.toFile().setWritable(true, true);
            }
            
        } catch (IOException e) {
            throw new CryptoException("Erreur lors de la sauvegarde de la clé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Charge une clé depuis un fichier
     * @param filename Le nom du fichier
     * @return La clé chargée
     * @throws CryptoException Si le chargement échoue
     */
    public static SecretKey loadKey(String filename) throws CryptoException {
        try {
            Path keyFile = Paths.get(KEY_DIRECTORY, filename);
            
            if (!Files.exists(keyFile)) {
                throw new CryptoException("Fichier de clé non trouvé: " + filename);
            }
            
            String keyString = new String(Files.readAllBytes(keyFile));
            return CryptoManager.stringToKey(keyString);
            
        } catch (IOException e) {
            throw new CryptoException("Erreur lors du chargement de la clé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère une nouvelle clé par défaut
     * @throws CryptoException Si la génération échoue
     */
    public static void regenerateDefaultKey() throws CryptoException {
        SecretKey newKey = CryptoManager.generateKey();
        saveKey(DEFAULT_KEY_FILE, newKey);
        cachedKey = newKey;
    }
    
    /**
     * Exporte une clé (utile pour la sauvegarde)
     * @param filename Le nom du fichier
     * @return La clé encodée en Base64
     * @throws CryptoException Si l'export échoue
     */
    public static String exportKey(String filename) throws CryptoException {
        SecretKey key = loadKey(filename);
        return CryptoManager.keyToString(key);
    }
    
    /**
     * Importe une clé depuis une chaîne encodée
     * @param filename Le nom du fichier destination
     * @param keyString La clé encodée en Base64
     * @throws CryptoException Si l'import échoue
     */
    public static void importKey(String filename, String keyString) throws CryptoException {
        SecretKey key = CryptoManager.stringToKey(keyString);
        saveKey(filename, key);
    }
    
    /**
     * Supprime un fichier de clé
     * @param filename Le nom du fichier
     * @throws CryptoException Si la suppression échoue
     */
    public static void deleteKey(String filename) throws CryptoException {
        try {
            Path keyFile = Paths.get(KEY_DIRECTORY, filename);
            if (Files.exists(keyFile)) {
                Files.delete(keyFile);
            }
        } catch (IOException e) {
            throw new CryptoException("Erreur lors de la suppression de la clé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Liste tous les fichiers de clés disponibles
     * @return Tableau des noms de fichiers
     * @throws CryptoException Si l'opération échoue
     */
    public static String[] listKeys() throws CryptoException {
        try {
            Path keyDir = Paths.get(KEY_DIRECTORY);
            if (!Files.exists(keyDir)) {
                return new String[0];
            }
            
            return Files.list(keyDir)
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            throw new CryptoException("Erreur lors de la listage des clés: " + e.getMessage(), e);
        }
    }
}
