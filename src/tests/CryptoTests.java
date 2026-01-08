package tests;

import security.CryptoException;
import security.CryptoManager;
import security.KeyManager;

import javax.crypto.SecretKey;

/**
 * Tests unitaires pour le système de cryptographie
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class CryptoTests {
    
    public static void main(String[] args) {
        System.out.println("=== Tests du Cryptosystème ===\n");
        
        try {
            testGenerationClé();
            testChiffrementDéchiffrement();
            testHachageMotsDePasse();
            testGestionClés();
            testPerformances();
            
            System.out.println("\n✓ Tous les tests sont passés avec succès!");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 1 : Génération de clé
     */
    private static void testGenerationClé() throws CryptoException {
        System.out.println("Test 1 : Génération de clé AES-256");
        System.out.println("----------------------------------");
        
        SecretKey key = CryptoManager.generateKey();
        
        assert key != null : "La clé ne doit pas être null";
        assert key.getEncoded().length == 32 : "La clé doit faire 256 bits (32 bytes)";
        assert key.getAlgorithm().equals("AES") : "L'algorithme doit être AES";
        
        System.out.println("✓ Génération réussie");
        System.out.println("  - Taille: " + (key.getEncoded().length * 8) + " bits");
        System.out.println("  - Algorithme: " + key.getAlgorithm());
        System.out.println();
    }
    
    /**
     * Test 2 : Chiffrement et déchiffrement
     */
    private static void testChiffrementDéchiffrement() throws CryptoException {
        System.out.println("Test 2 : Chiffrement et déchiffrement");
        System.out.println("-------------------------------------");
        
        SecretKey key = CryptoManager.generateKey();
        String plaintext = "Étudiant_MAT123456";
        
        // Chiffrer
        String encrypted = CryptoManager.encrypt(plaintext, key);
        System.out.println("Texte original: " + plaintext);
        System.out.println("Texte chiffré:  " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
        
        assert !encrypted.equals(plaintext) : "Le texte chiffré doit différer du texte original";
        
        // Déchiffrer
        String decrypted = CryptoManager.decrypt(encrypted, key);
        System.out.println("Texte déchiffré: " + decrypted);
        
        assert decrypted.equals(plaintext) : "Le texte déchiffré doit correspondre à l'original";
        
        System.out.println("✓ Chiffrement/déchiffrement réussi");
        System.out.println();
    }
    
    /**
     * Test 3 : Hachage de mots de passe
     */
    private static void testHachageMotsDePasse() throws CryptoException {
        System.out.println("Test 3 : Hachage de mots de passe (SHA-256)");
        System.out.println("------------------------------------------");
        
        String password = "admin123";
        
        // Hacher
        String hash1 = CryptoManager.hashPassword(password);
        System.out.println("Mot de passe: " + password);
        System.out.println("Hash: " + hash1);
        
        // Vérifier
        boolean verified = CryptoManager.verifyPassword(password, hash1);
        System.out.println("Vérification: " + (verified ? "✓" : "✗"));
        
        assert verified : "La vérification du mot de passe doit réussir";
        
        // Test avec un mauvais mot de passe
        boolean verifiedWrong = CryptoManager.verifyPassword("wrongpassword", hash1);
        System.out.println("Vérification (mauvais mot de passe): " + (verifiedWrong ? "✗" : "✓"));
        
        assert !verifiedWrong : "La vérification du mauvais mot de passe doit échouer";
        
        System.out.println("✓ Hachage de mots de passe réussi");
        System.out.println();
    }
    
    /**
     * Test 4 : Gestion des clés
     */
    private static void testGestionClés() throws CryptoException {
        System.out.println("Test 4 : Gestion des clés");
        System.out.println("-------------------------");
        
        // Initialiser le gestionnaire de clés
        KeyManager.initialize();
        System.out.println("✓ Gestionnaire de clés initialisé");
        
        // Obtenir la clé par défaut
        SecretKey defaultKey = KeyManager.getDefaultKey();
        assert defaultKey != null : "La clé par défaut ne doit pas être null";
        System.out.println("✓ Clé par défaut obtenue");
        
        // Exporter/Importer une clé
        SecretKey newKey = CryptoManager.generateKey();
        String keyString = CryptoManager.keyToString(newKey);
        System.out.println("✓ Clé exportée (Base64): " + keyString.substring(0, 30) + "...");
        
        SecretKey reconstructedKey = CryptoManager.stringToKey(keyString);
        assert reconstructedKey.getEncoded().length == 32 : "La clé reconstruite doit faire 256 bits";
        System.out.println("✓ Clé importée et reconstruite");
        
        System.out.println();
    }
    
    /**
     * Test 5 : Performances
     */
    private static void testPerformances() throws CryptoException {
        System.out.println("Test 5 : Performances");
        System.out.println("--------------------");
        
        SecretKey key = CryptoManager.generateKey();
        String plaintext = "Étudiant_MAT123456_Note_12.5_UE_INF201";
        int iterations = 1000;
        
        // Test de chiffrement
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            CryptoManager.encrypt(plaintext, key);
        }
        long encryptTime = System.currentTimeMillis() - startTime;
        double avgEncryptTime = (double) encryptTime / iterations;
        
        System.out.println("Chiffrement de " + iterations + " éléments:");
        System.out.println("  - Temps total: " + encryptTime + "ms");
        System.out.println("  - Temps moyen: " + String.format("%.2f", avgEncryptTime) + "ms par élément");
        
        // Test de déchiffrement
        String encrypted = CryptoManager.encrypt(plaintext, key);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            CryptoManager.decrypt(encrypted, key);
        }
        long decryptTime = System.currentTimeMillis() - startTime;
        double avgDecryptTime = (double) decryptTime / iterations;
        
        System.out.println("Déchiffrement de " + iterations + " éléments:");
        System.out.println("  - Temps total: " + decryptTime + "ms");
        System.out.println("  - Temps moyen: " + String.format("%.2f", avgDecryptTime) + "ms par élément");
        
        assert encryptTime < 5000 : "Le chiffrement de 1000 éléments doit prendre moins de 5 secondes";
        assert decryptTime < 5000 : "Le déchiffrement de 1000 éléments doit prendre moins de 5 secondes";
        
        System.out.println("✓ Performances acceptables");
        System.out.println();
    }
}
