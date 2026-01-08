import view.FenetrePrincipale;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 * Classe principale de l'application de gestion des notes
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Configurer le thème sombre AVANT de créer l'interface
        setupDarkTheme();

        // Afficher le splash screen au démarrage
        // L'interface principale sera lancée automatiquement après la fermeture du splash
        afficherSplashScreen();
    }

    private static void setupDarkTheme() {
        try {
            // Option 1 : Thème sombre simple
            FlatDarkLaf.setup();

            // Option 2 : Thème macOS sombre
            // FlatMacDarkLaf.setup();

            // Option 3 : Personnalisation avancée
            // setupCustomDarkTheme();

        } catch (Exception e) {
            System.err.println("Erreur avec FlatLaf: " + e.getMessage());
            // Fallback au thème système
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Personnaliser certaines couleurs
        customizeUI();
    }

    private static void customizeUI() {
        // Personnalisation optionnelle des couleurs
        UIManager.put("Panel.background", new Color(30, 30, 35));
        UIManager.put("Button.background", new Color(60, 63, 65));
        UIManager.put("Button.foreground", new Color(220, 220, 220));
        UIManager.put("Label.foreground", new Color(220, 220, 220));
        UIManager.put("TextField.background", new Color(43, 43, 43));
        UIManager.put("TextField.foreground", new Color(220, 220, 220));
        UIManager.put("Table.background", new Color(43, 43, 43));
        UIManager.put("Table.foreground", new Color(220, 220, 220));
        UIManager.put("Table.gridColor", new Color(80, 80, 80));
        UIManager.put("Table.selectionBackground", new Color(60, 90, 150));
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }

    /**
     * Affiche un splash screen au démarrage de l'application
     */
    private static void afficherSplashScreen() {
        JWindow splash = new JWindow();
        JPanel content = new JPanel(new BorderLayout());
        // Nouveau design avec dégradé de couleurs moderne
        content.setBackground(new Color(30, 30, 35)); // Fond sombre élégant
        content.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 3));
        
        JLabel lblTitre = new JLabel("<html><center>" +
            "<h1 style='color: #4CAF50; font-family: Arial, sans-serif; font-weight: bold;'>Université de Maroua</h1>" +
            "<h2 style='color: #2196F3; font-family: Arial, sans-serif;'>Faculté des Sciences</h2>" +
            "<p style='color: #FFFFFF; font-size: 16px; margin-top: 10px;'>Système de Gestion des Notes</p>" +
            "<p style='color: #B0BEC5; font-size: 11px; margin-top: 5px;'>Version 1.0</p>" +
            "</center></html>");
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitre.setBorder(BorderFactory.createEmptyBorder(30, 20, 15, 20));
        
        content.add(lblTitre, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(76, 175, 80)); // Vert moderne
        progressBar.setBackground(new Color(50, 50, 55));
        progressBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 25, 20));
        content.add(progressBar, BorderLayout.SOUTH);
        
        splash.setContentPane(content);
        splash.setSize(450, 280);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        // Durée d'affichage du splash screen (en millisecondes)
        final int DUREE_SPLASH = 3000; // 3 secondes
        
        // Fermer le splash screen après la durée définie dans un thread séparé
        new Thread(() -> {
            try {
                Thread.sleep(DUREE_SPLASH);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    splash.dispose();
                    // Démarrer l'interface graphique principale après le splash
                    FenetrePrincipale mainWindow = new FenetrePrincipale();
                    mainWindow.setVisible(true);
                });
            }
        }).start();
    }
}