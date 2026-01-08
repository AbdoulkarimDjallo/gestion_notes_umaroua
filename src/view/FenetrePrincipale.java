package view;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale pour la sélection des filtres et gestion des étudiants
 */
public class FenetrePrincipale extends JFrame {
    private JComboBox<String> comboDepartement;
    private JComboBox<String> comboSpecialite;
    private JComboBox<Integer> comboNiveau;
    private JButton btnGererEtudiants;
    
    public FenetrePrincipale() {
        initialiserInterface();
    }
    
    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        setTitle("Gestion des Notes - Université de Maroua - Faculté des Sciences");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JPanel panelEnTete = new JPanel();
        JLabel lblTitre = new JLabel("SÉLECTION DES ÉTUDIANTS");
        lblTitre.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitre.setForeground(new Color(0, 255, 255));
        panelEnTete.add(lblTitre);
        panelPrincipal.add(panelEnTete, BorderLayout.NORTH);
        
        // Panel de sélection
        JPanel panelSelection = new JPanel(new GridBagLayout());
        panelSelection.setBorder(BorderFactory.createTitledBorder("Filtres"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Département
        gbc.gridx = 0; gbc.gridy = 0;
        panelSelection.add(new JLabel("Département:"), gbc);
        gbc.gridx = 1;
        comboDepartement = new JComboBox<>(new String[]{"Tous", "Maths-Info", "Physique-Chimie", "Biologie"});
        panelSelection.add(comboDepartement, gbc);
        
        // Spécialité
        gbc.gridx = 0; gbc.gridy = 1;
        panelSelection.add(new JLabel("Spécialité:"), gbc);
        gbc.gridx = 1;
        comboSpecialite = new JComboBox<>(new String[]{"Toutes", "INF", "IGE", "MAT", "PHY", "CHM", "BIO", "STE"});
        panelSelection.add(comboSpecialite, gbc);
        
        // Niveau
        gbc.gridx = 0; gbc.gridy = 2;
        panelSelection.add(new JLabel("Niveau:"), gbc);
        gbc.gridx = 1;
        comboNiveau = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5});
        comboNiveau.setSelectedItem(0); // Tous
        panelSelection.add(comboNiveau, gbc);
        
        panelPrincipal.add(panelSelection, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnGererEtudiants = new JButton("Gérer les étudiants");
        btnGererEtudiants.setFont(new Font("Arial", Font.PLAIN, 14));
        btnGererEtudiants.setBackground(new Color(0, 102, 204));
        btnGererEtudiants.setForeground(Color.WHITE);
        btnGererEtudiants.addActionListener(e -> ouvrirGestionEtudiants());
        panelBoutons.add(btnGererEtudiants);
        
        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    /**
     * Ouvre la fenêtre de gestion des étudiants filtrés
     */
    private void ouvrirGestionEtudiants() {
        String departement = comboDepartement.getSelectedItem().equals("Tous") ? null : (String) comboDepartement.getSelectedItem();
        String specialite = comboSpecialite.getSelectedItem().equals("Toutes") ? null : (String) comboSpecialite.getSelectedItem();
        int niveau = (Integer) comboNiveau.getSelectedItem();
        if (niveau == 0) niveau = -1; // Pour indiquer tous
        
        new FenetreGestionEtudiants(this, departement, specialite, niveau).setVisible(true);
    }
}
