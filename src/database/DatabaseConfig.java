package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuration et gestion de la connexion à la base de données PostgreSQL
 */
public class DatabaseConfig {
    // Paramètres de connexion - MODIFIABLES
    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_notes_umaroua";
    private static final String USER = "postgres";
    private static final String PASSWORD = "5596";
    
    private static Connection connection = null;
    
    /**
     * Obtient une connexion à la base de données
     * @return Connection active
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Chargement du driver PostgreSQL
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données réussie");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL non trouvé. Assurez-vous que postgresql.jar est dans le classpath", e);
            } catch (SQLException e) {
                // Vérifier si l'erreur est liée à une base de données inexistante
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.contains("does not exist")) {
                    throw new SQLException(
                        "ERREUR: La base de données 'gestion_notes_umaroua' n'existe pas!\n\n" +
                        "SOLUTION: Créez la base de données en exécutant:\n" +
                        "  1. Ouvrez psql ou pgAdmin\n" +
                        "  2. Exécutez: CREATE DATABASE gestion_notes_umaroua;\n" +
                        "  3. Puis exécutez le script: creation_base_donnees.sql\n\n" +
                        "Ou utilisez le script: creer_base_donnees.bat\n\n" +
                        "Erreur originale: " + errorMessage, e);
                } else if (errorMessage != null && errorMessage.contains("Connection refused") || errorMessage.contains("could not connect")) {
                    throw new SQLException(
                        "ERREUR: Impossible de se connecter à PostgreSQL!\n\n" +
                        "Vérifiez que:\n" +
                        "  1. PostgreSQL est démarré\n" +
                        "  2. Le serveur écoute sur le port 5432\n" +
                        "  3. Les paramètres de connexion dans DatabaseConfig.java sont corrects\n\n" +
                        "Erreur originale: " + errorMessage, e);
                } else if (errorMessage != null && errorMessage.contains("password authentication failed")) {
                    throw new SQLException(
                        "ERREUR: Authentification échouée!\n\n" +
                        "Le mot de passe ou l'utilisateur est incorrect.\n" +
                        "Vérifiez les paramètres dans DatabaseConfig.java\n\n" +
                        "Erreur originale: " + errorMessage, e);
                } else {
                    throw new SQLException("Erreur de connexion à la base de données: " + errorMessage, e);
                }
            }
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Connexion fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    /**
     * Teste la connexion à la base de données
     * @return true si la connexion fonctionne
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isConnected = conn != null && !conn.isClosed();
            if (isConnected) {
                System.out.println("✓ Test de connexion réussi");
            }
            return isConnected;
        } catch (SQLException e) {
            System.err.println("\n❌ ERREUR DE CONNEXION:");
            System.err.println(e.getMessage());
            System.err.println();
            return false;
        }
    }
}