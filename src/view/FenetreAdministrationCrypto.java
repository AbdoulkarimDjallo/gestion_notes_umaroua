package view;

import security.CryptoException;
import security.KeyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Fenêtre d'administration du cryptosystème
 * Université de Maroua - Faculté des Sciences
 * 
 * @author Djallo - Housseini
 * @version 1.0
 */
public class FenetreAdministrationCrypto extends JFrame {
    
    private JButton btnGenererNouvelleCle;
    private JButton btnExporterCle;
    private JButton btnImporterCle;
    private JButton btnListerCles;
    private JButton btnSupprimerCle;
    private JTextArea taInformation;
    private JTextField tfNomCle;
    
    public FenetreAdministrationCrypto() {
        setTitle("Administration - Gestion des Clés de Chiffrement");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        initializeComponents();
        addListeners();
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panneau Nord - Informations sur le système
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion Sécurisée des Clés de Chiffrement");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel descriptionLabel = new JLabel(
            "<html>Ce module permet de gérer les clés de chiffrement AES-256. " +
            "Les clés sont stockées de manière sécurisée dans le dossier '.keys'.</html>"
        );
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(descriptionLabel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Panneau Centre - Zone de texte pour afficher les informations
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Informations du Système"));
        
        taInformation = new JTextArea();
        taInformation.setEditable(false);
        taInformation.setFont(new Font("Courier New", Font.PLAIN, 11));
        taInformation.setLineWrap(true);
        taInformation.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(taInformation);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Panneau Sud - Boutons d'action
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        // Sous-panneau pour le nom de la clé
        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        namePanel.add(new JLabel("Nom du fichier de clé:"), BorderLayout.WEST);
        tfNomCle = new JTextField("default_key.key");
        namePanel.add(tfNomCle, BorderLayout.CENTER);
        bottomPanel.add(namePanel, BorderLayout.NORTH);
        
        // Sous-panneau pour les boutons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        btnGenererNouvelleCle = new JButton("Générer Nouvelle Clé");
        btnExporterCle = new JButton("Exporter Clé");
        btnImporterCle = new JButton("Importer Clé");
        btnListerCles = new JButton("Lister Clés");
        btnSupprimerCle = new JButton("Supprimer Clé");
        JButton btnFermer = new JButton("Fermer");
        
        buttonPanel.add(btnGenererNouvelleCle);
        buttonPanel.add(btnExporterCle);
        buttonPanel.add(btnImporterCle);
        buttonPanel.add(btnListerCles);
        buttonPanel.add(btnSupprimerCle);
        buttonPanel.add(btnFermer);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        btnFermer.addActionListener(e -> dispose());
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Afficher les informations au démarrage
        afficherInformationsSysteme();
    }
    
    private void addListeners() {
        btnGenererNouvelleCle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genererNouvelleCle();
            }
        });
        
        btnExporterCle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exporterCle();
            }
        });
        
        btnImporterCle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importerCle();
            }
        });
        
        btnListerCles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listerCles();
            }
        });
        
        btnSupprimerCle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerCle();
            }
        });
    }
    
    private void afficherInformationsSysteme() {
        StringBuilder info = new StringBuilder();
        
        info.append("=== INFORMATIONS DU CRYPTOSYSTÈME ===\n\n");
        info.append("Algorithme: AES-256 (Advanced Encryption Standard)\n");
        info.append("Mode: CBC (Cipher Block Chaining)\n");
        info.append("Padding: PKCS5Padding\n");
        info.append("Taille de la clé: 256 bits (32 bytes)\n");
        info.append("Taille de l'IV: 128 bits (16 bytes)\n\n");
        
        info.append("=== RÉPERTOIRE DES CLÉS ===\n");
        info.append("Localisation: .keys/\n\n");
        
        try {
            KeyManager.initialize();
            String[] keys = KeyManager.listKeys();
            
            info.append("Clés disponibles: ").append(keys.length).append("\n");
            for (String key : keys) {
                info.append("  - ").append(key).append("\n");
            }
        } catch (CryptoException e) {
            info.append("Erreur lors de la listage des clés: ").append(e.getMessage()).append("\n");
        }
        
        info.append("\n=== SÉCURITÉ ===\n");
        info.append("✓ Clés stockées en dehors de la base de données\n");
        info.append("✓ Fichiers de clés protégés (permissions 600)\n");
        info.append("✓ Chiffrement transparent dans l'application\n");
        info.append("✓ Hachage SHA-256 pour les mots de passe\n");
        
        taInformation.setText(info.toString());
    }
    
    private void genererNouvelleCle() {
        try {
            KeyManager.initialize();
            String nomCle = tfNomCle.getText().trim();
            
            if (nomCle.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer un nom de fichier de clé", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            KeyManager.regenerateDefaultKey();
            
            JOptionPane.showMessageDialog(this, 
                "Nouvelle clé générée et sauvegardée avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            
            afficherInformationsSysteme();
            
        } catch (CryptoException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la génération de la clé: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exporterCle() {
        try {
            KeyManager.initialize();
            String nomCle = tfNomCle.getText().trim();
            
            if (nomCle.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer un nom de fichier de clé", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String keyString = KeyManager.exportKey(nomCle);
            
            // Afficher la clé dans une nouvelle fenêtre
            JFrame exportFrame = new JFrame("Export de la Clé");
            exportFrame.setSize(600, 400);
            exportFrame.setLocationRelativeTo(this);
            
            JTextArea textArea = new JTextArea(keyString);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            exportFrame.add(scrollPane);
            exportFrame.setVisible(true);
            
        } catch (CryptoException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'export de la clé: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importerCle() {
        JFrame importFrame = new JFrame("Importer une Clé");
        importFrame.setSize(600, 400);
        importFrame.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("Collez la clé encodée en Base64:");
        panel.add(label, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton btnImporter = new JButton("Importer");
        JButton btnAnnuler = new JButton("Annuler");
        
        btnImporter.addActionListener(e -> {
            try {
                String nomCle = tfNomCle.getText().trim();
                String keyString = textArea.getText().trim();
                
                if (nomCle.isEmpty() || keyString.isEmpty()) {
                    JOptionPane.showMessageDialog(importFrame, 
                        "Veuillez remplir tous les champs", 
                        "Erreur", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                KeyManager.importKey(nomCle, keyString);
                
                JOptionPane.showMessageDialog(importFrame, 
                    "Clé importée avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                importFrame.dispose();
                afficherInformationsSysteme();
                
            } catch (CryptoException ex) {
                JOptionPane.showMessageDialog(importFrame, 
                    "Erreur lors de l'import: " + ex.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> importFrame.dispose());
        
        buttonPanel.add(btnImporter);
        buttonPanel.add(btnAnnuler);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        importFrame.add(panel);
        importFrame.setVisible(true);
    }
    
    private void listerCles() {
        try {
            KeyManager.initialize();
            String[] keys = KeyManager.listKeys();
            
            StringBuilder liste = new StringBuilder("Clés disponibles:\n\n");
            if (keys.length == 0) {
                liste.append("Aucune clé trouvée.");
            } else {
                for (int i = 0; i < keys.length; i++) {
                    liste.append((i + 1)).append(". ").append(keys[i]).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                liste.toString(), 
                "Liste des Clés", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (CryptoException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la listage: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerCle() {
        try {
            String nomCle = tfNomCle.getText().trim();
            
            if (nomCle.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer un nom de fichier de clé", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer la clé '" + nomCle + "'?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                KeyManager.deleteKey(nomCle);
                
                JOptionPane.showMessageDialog(this, 
                    "Clé supprimée avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                afficherInformationsSysteme();
            }
            
        } catch (CryptoException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la suppression: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
