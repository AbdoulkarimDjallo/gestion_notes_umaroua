package security;

/**
 * Exception personnalisée pour les erreurs de cryptographie
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class CryptoException extends Exception {
    
    /**
     * Constructeur avec message
     * @param message Le message d'erreur
     */
    public CryptoException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et cause
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructeur avec cause
     * @param cause La cause de l'exception
     */
    public CryptoException(Throwable cause) {
        super(cause);
    }
}
