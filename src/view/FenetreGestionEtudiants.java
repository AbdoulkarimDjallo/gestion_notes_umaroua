package view;

import controller.EtudiantDAO;
import model.Etudiant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Fenêtre affichant la liste des étudiants filtrés
 */
public class FenetreGestionEtudiants extends JFrame {
    private JTable tableEtudiants;
    private DefaultTableModel modelTable;
    private EtudiantDAO etudiantDAO;
    private JButton btnVoirNotes;
    private JButton btnAjouterEtudiant;
    private JButton btnModifierEtudiant;
    private JButton btnSupprimerEtudiant;
    private JButton btnSaisirNote;
    private JButton btnActualiser;
    private JButton btnRetour;
    private String departement;
    private String specialite;
    private int niveau;
    private FenetrePrincipale fenetrePrincipale;
    
    public FenetreGestionEtudiants(FenetrePrincipale fenetrePrincipale, String departement, String specialite, int niveau) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.departement = departement;
        this.specialite = specialite;
        this.niveau = niveau;
        try {
            etudiantDAO = new EtudiantDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur d'initialisation: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            etudiantDAO = null;
        }
        initialiserInterface();
        chargerEtudiants();
    }
    
    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        setTitle("Gestion des Étudiants - Université de Maroua");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // En-tête
        JPanel panelEnTete = new JPanel();
        String titre = "LISTE DES ÉTUDIANTS";
        if (departement != null || specialite != null || niveau > 0) {
            titre += " (Filtré";
            if (departement != null) titre += " - " + departement;
            if (specialite != null) titre += " - " + specialite;
            if (niveau > 0) titre += " - Niveau " + niveau;
            titre += ")";
        }
        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitre.setForeground(new Color(0, 255, 255));
        panelEnTete.add(lblTitre);
        panelPrincipal.add(panelEnTete, BorderLayout.NORTH);
        
        // Tableau des étudiants
        String[] colonnes = {"Matricule", "Nom", "Prénom", "Département", "Spécialité", "Niveau"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre le tableau non éditable
            }
        };
        
        tableEtudiants = new JTable(modelTable);
        tableEtudiants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEtudiants.setRowHeight(25);
        tableEtudiants.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableEtudiants.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Ajuster la largeur des colonnes
        tableEtudiants.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableEtudiants.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableEtudiants.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableEtudiants.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableEtudiants.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableEtudiants.getColumnModel().getColumn(5).setPreferredWidth(60);
        
        JScrollPane scrollPane = new JScrollPane(tableEtudiants);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnVoirNotes = new JButton("Voir les notes");
        btnVoirNotes.setFont(new Font("Arial", Font.PLAIN, 14));
        btnVoirNotes.setBackground(new Color(0, 102, 204));
        btnVoirNotes.setForeground(Color.WHITE);
        btnVoirNotes.addActionListener(e -> voirNotes());
        panelBoutons.add(btnVoirNotes);
        
        btnAjouterEtudiant = new JButton("Ajouter étudiant");
        btnAjouterEtudiant.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAjouterEtudiant.setBackground(new Color(34, 139, 34));
        btnAjouterEtudiant.setForeground(Color.WHITE);
        btnAjouterEtudiant.addActionListener(e -> ajouterEtudiant());
        panelBoutons.add(btnAjouterEtudiant);
        
        btnModifierEtudiant = new JButton("Modifier étudiant");
        btnModifierEtudiant.setFont(new Font("Arial", Font.PLAIN, 14));
        btnModifierEtudiant.setBackground(new Color(255, 215, 0));
        btnModifierEtudiant.setForeground(Color.WHITE);
        btnModifierEtudiant.addActionListener(e -> modifierEtudiant());
        panelBoutons.add(btnModifierEtudiant);
        
        btnSupprimerEtudiant = new JButton("Supprimer étudiant");
        btnSupprimerEtudiant.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSupprimerEtudiant.setBackground(new Color(220, 20, 60));
        btnSupprimerEtudiant.setForeground(Color.WHITE);
        btnSupprimerEtudiant.addActionListener(e -> supprimerEtudiant());
        panelBoutons.add(btnSupprimerEtudiant);
        
        btnSaisirNote = new JButton("Saisir note");
        btnSaisirNote.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSaisirNote.setBackground(new Color(255, 140, 0));
        btnSaisirNote.setForeground(Color.WHITE);
        btnSaisirNote.addActionListener(e -> saisirNote());
        panelBoutons.add(btnSaisirNote);
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.setFont(new Font("Arial", Font.PLAIN, 14));
        btnActualiser.setBackground(new Color(128, 128, 128));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.addActionListener(e -> chargerEtudiants());
        panelBoutons.add(btnActualiser);
        
        btnRetour = new JButton("Retour");
        btnRetour.setFont(new Font("Arial", Font.PLAIN, 14));
        btnRetour.setBackground(new Color(34, 139, 34));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.addActionListener(e -> dispose());
        panelBoutons.add(btnRetour);
        
        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    /**
     * Charge la liste des étudiants filtrés depuis la base de données
     */
    public void chargerEtudiants() {
        try {
            modelTable.setRowCount(0); // Vider le tableau
            List<Etudiant> etudiants = etudiantDAO.obtenirEtudiantsFiltres(departement, specialite, niveau == -1 ? 0 : niveau);
            
            for (Etudiant etudiant : etudiants) {
                Object[] ligne = {
                    etudiant.getMatricule(),
                    etudiant.getNom(),
                    etudiant.getPrenom(),
                    etudiant.getDepartement(),
                    etudiant.getSpecialite(),
                    etudiant.getNiveau()
                };
                modelTable.addRow(ligne);
            }
            
            // Mettre à jour le titre avec le nombre d'étudiants
            setTitle("Gestion des Étudiants (" + etudiants.size() + " étudiants)");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des étudiants:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Voir les notes de l'étudiant sélectionné
     */
    private void voirNotes() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String matricule = (String) modelTable.getValueAt(selectedRow, 0);
        String nomComplet = (String) modelTable.getValueAt(selectedRow, 1) + " " + (String) modelTable.getValueAt(selectedRow, 2);
        
        new FenetreNotes(matricule, nomComplet, fenetrePrincipale).setVisible(true);
    }
    
    /**
     * Ajouter un nouvel étudiant
     */
    private void ajouterEtudiant() {
        new FenetreSaisie(fenetrePrincipale, true, null).setVisible(true);
        chargerEtudiants(); // Actualiser après ajout
    }
    
    /**
     * Modifier l'étudiant sélectionné
     */
    private void modifierEtudiant() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un étudiant dans le tableau.", 
                "Aucune sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String matricule = (String) modelTable.getValueAt(selectedRow, 0);
        System.out.println("[DEBUG] Tentative de modification de l'étudiant: " + matricule);
        
        try {
            Etudiant etudiant = etudiantDAO.rechercherParMatricule(matricule);
            if (etudiant != null) {
                System.out.println("[DEBUG] Étudiant trouvé: " + etudiant.getNomComplet());
                FenetreSaisie fenetreSaisie = new FenetreSaisie(fenetrePrincipale, false, etudiant);
                fenetreSaisie.setVisible(true);
                // Actualiser après la fermeture de la fenêtre
                fenetreSaisie.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        chargerEtudiants();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de trouver l'étudiant avec le matricule: " + matricule, 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la recherche de l'étudiant:\n" + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Supprimer l'étudiant sélectionné
     */
    private void supprimerEtudiant() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String matricule = (String) modelTable.getValueAt(selectedRow, 0);
        String nom = (String) modelTable.getValueAt(selectedRow, 1);
        String prenom = (String) modelTable.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'étudiant " + nom + " " + prenom + " (" + matricule + ") ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (etudiantDAO.supprimerEtudiant(matricule)) {
                    JOptionPane.showMessageDialog(this, "Étudiant supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerEtudiants();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Saisir une note pour l'étudiant sélectionné
     */
    private void saisirNote() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String matricule = (String) modelTable.getValueAt(selectedRow, 0);
        new FenetreSaisie(fenetrePrincipale, false, null, matricule).setVisible(true);
    }
}