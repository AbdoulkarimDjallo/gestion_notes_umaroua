package security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * Gestionnaire de cryptographie pour le système de gestion des notes
 * Utilise AES-256 en mode CBC pour chiffrer les données sensibles
 * 
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class CryptoManager {
    
    // Paramètres de chiffrement
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256; // bits
    private static final int IV_SIZE = 128; // bits (16 bytes)
    
    static {
        // Initialiser les fournisseurs de sécurité
        Security.setProperty("crypto.policy", "unlimited");
    }
    
    /**
     * Génère une nouvelle clé de chiffrement AES-256
     * @return Une nouvelle SecretKey
     * @throws CryptoException Si la génération échoue
     */
    public static SecretKey generateKey() throws CryptoException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom());
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new CryptoException("Erreur lors de la génération de la clé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un IV (Initialization Vector) aléatoire
     * @return Un tableau de bytes représentant l'IV
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE / 8];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    /**
     * Chiffre une chaîne de caractères
     * @param plaintext Le texte en clair
     * @param key La clé de chiffrement
     * @return Le texte chiffré et encodé en Base64
     * @throws CryptoException Si le chiffrement échoue
     */
    public static String encrypt(String plaintext, SecretKey key) throws CryptoException {
        try {
            // Générer un IV aléatoire
            byte[] iv = generateIV();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Initialiser le cipher en mode chiffrement
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            
            // Chiffrer les données
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);
            
            // Combiner IV + ciphertext
            byte[] combined = new byte[iv.length + ciphertextBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertextBytes, 0, combined, iv.length, ciphertextBytes.length);
            
            // Encoder en Base64
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            throw new CryptoException("Erreur lors du chiffrement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Déchiffre une chaîne de caractères
     * @param encryptedText Le texte chiffré (encodé en Base64)
     * @param key La clé de déchiffrement
     * @return Le texte déchiffré
     * @throws CryptoException Si le déchiffrement échoue
     */
    public static String decrypt(String encryptedText, SecretKey key) throws CryptoException {
        try {
            // Décoder depuis Base64
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // Extraire l'IV et le ciphertext
            byte[] iv = new byte[IV_SIZE / 8];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE / 8);
            
            byte[] ciphertextBytes = new byte[combined.length - IV_SIZE / 8];
            System.arraycopy(combined, IV_SIZE / 8, ciphertextBytes, 0, ciphertextBytes.length);
            
            // Initialiser le cipher en mode déchiffrement
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            
            // Déchiffrer les données
            byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
            
            return new String(plaintextBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new CryptoException("Erreur lors du déchiffrement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convertit une clé en chaîne encodée en Base64 pour le stockage
     * @param key La clé à convertir
     * @return La clé encodée en Base64
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Reconstruit une clé à partir d'une chaîne encodée en Base64
     * @param keyString La chaîne encodée
     * @return La clé reconstruite
     * @throws CryptoException Si la conversion échoue
     */
    public static SecretKey stringToKey(String keyString) throws CryptoException {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_ALGORITHM);
        } catch (Exception e) {
            throw new CryptoException("Erreur lors de la reconstruction de la clé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hache une chaîne de caractères avec SHA-256 (pour les mots de passe)
     * @param input La chaîne à hacher
     * @return Le hash encodé en Base64
     * @throws CryptoException Si le hachage échoue
     */
    public static String hashPassword(String input) throws CryptoException {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new CryptoException("Erreur lors du hachage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Vérifie qu'une chaîne correspond à un hash
     * @param input La chaîne en clair
     * @param hash Le hash à vérifier
     * @return true si les deux correspondent
     * @throws CryptoException Si la vérification échoue
     */
    public static boolean verifyPassword(String input, String hash) throws CryptoException {
        try {
            String inputHash = hashPassword(input);
            return inputHash.equals(hash);
        } catch (CryptoException e) {
            throw e;
        }
    }
    
    /**
     * Efface une clé de la mémoire (pour sécurité)
     * @param key La clé à effacer
     */
    public static void clearKey(SecretKey key) {
        if (key instanceof javax.crypto.spec.SecretKeySpec) {
            byte[] encoded = key.getEncoded();
            if (encoded != null) {
                java.util.Arrays.fill(encoded, (byte) 0);
            }
        }
    }
}
