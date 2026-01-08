package view;

import controller.EtudiantDAO;
import controller.NoteDAO;
import model.Etudiant;
import model.Note;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Fenêtre de saisie pour ajouter des étudiants ou des notes
 */
public class FenetreSaisie extends JFrame {
    private FenetrePrincipale fenetrePrincipale;
    private boolean modeAjoutEtudiant;
    private Etudiant etudiantAModifier;
    private String matriculeInitial;
    
    private JTextField txtMatricule;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JComboBox<String> cmbDepartement;
    private JComboBox<String> cmbSpecialite;
    private JComboBox<Integer> cmbNiveau;
    private JTextField txtCodeUE;
    private JTextField txtNote;
    
    private JButton btnRechercher;
    private JButton btnEnregistrer;
    private JButton btnAnnuler;
    
    private EtudiantDAO etudiantDAO;
    private NoteDAO noteDAO;
    
    private JPanel panelNote;
    
    /**
     * Constructeur pour modification d'étudiant
     * @param fenetrePrincipale Référence à la fenêtre principale
     * @param modeAjoutEtudiant true pour ajout, false pour modification
     * @param etudiant L'étudiant à modifier (null pour ajout)
     */
    public FenetreSaisie(FenetrePrincipale fenetrePrincipale, boolean modeAjoutEtudiant, Etudiant etudiant) {
        this(fenetrePrincipale, modeAjoutEtudiant, etudiant, null);
    }
    
    /**
     * Constructeur avec matricule initial pour saisie note
     * @param fenetrePrincipale Référence à la fenêtre principale
     * @param modeAjoutEtudiant true pour ajout, false pour modification
     * @param etudiant L'étudiant à modifier (null pour ajout)
     * @param matriculeInitial Matricule à pré-remplir pour saisie note
     */
    public FenetreSaisie(FenetrePrincipale fenetrePrincipale, boolean modeAjoutEtudiant, Etudiant etudiant, String matriculeInitial) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.modeAjoutEtudiant = modeAjoutEtudiant;
        this.etudiantAModifier = etudiant;
        this.matriculeInitial = matriculeInitial;
        try {
            this.etudiantDAO = new EtudiantDAO();
            this.noteDAO = new NoteDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur d'initialisation: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            this.etudiantDAO = null;
            this.noteDAO = null;
        }

        initialiserInterface();
        if (etudiant != null) {
            chargerEtudiant(etudiant);
        }
        if (matriculeInitial != null) {
            txtMatricule.setText(matriculeInitial);
        }
    }
    
    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        String titre = etudiantAModifier != null ? "Modifier un étudiant" : (modeAjoutEtudiant ? "Ajouter un étudiant" : "Saisir une note");
        setTitle(titre);
        setSize(500, (modeAjoutEtudiant || etudiantAModifier != null) ? 450 : 600);
        setLocationRelativeTo(fenetrePrincipale);
        setResizable(false);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JLabel lblTitre = new JLabel(titre.toUpperCase());
        lblTitre.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitre.setForeground(new Color(0, 255, 255));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitre, BorderLayout.NORTH);
        
        // Panel central avec formulaire
        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Matricule avec bouton rechercher
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulaire.add(new JLabel("Matricule:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel panelMatricule = new JPanel(new BorderLayout(5, 0));
        txtMatricule = new JTextField(15);
        txtMatricule.setFont(new Font("Arial", Font.PLAIN, 13));
        panelMatricule.add(txtMatricule, BorderLayout.CENTER);
        
        btnRechercher = new JButton("Rechercher");
        btnRechercher.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRechercher.addActionListener(e -> rechercherEtudiant());
        panelMatricule.add(btnRechercher, BorderLayout.EAST);
        panelFormulaire.add(panelMatricule, gbc);
        
        // Nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Nom:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNom = new JTextField(20);
        txtNom.setFont(new Font("Arial", Font.PLAIN, 13));
        panelFormulaire.add(txtNom, gbc);
        
        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Prénom:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPrenom = new JTextField(20);
        txtPrenom.setFont(new Font("Arial", Font.PLAIN, 13));
        panelFormulaire.add(txtPrenom, gbc);
        
        // Département
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Département:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] departements = {"Maths-Info", "Physique-Chimie", "Biologie"};
        cmbDepartement = new JComboBox<>(departements);
        cmbDepartement.setFont(new Font("Arial", Font.PLAIN, 13));
        panelFormulaire.add(cmbDepartement, gbc);
        
        // Spécialité
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Spécialité:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] specialites = {"INF", "IGE", "MAT", "PHY", "CHM", "BIO", "STE"};
        cmbSpecialite = new JComboBox<>(specialites);
        cmbSpecialite.setFont(new Font("Arial", Font.PLAIN, 13));
        panelFormulaire.add(cmbSpecialite, gbc);
        
        // Niveau
        Integer[] niveaux = {1, 2, 3, 4, 5};
        cmbNiveau = new JComboBox<>(niveaux);
        cmbNiveau.setFont(new Font("Arial", Font.PLAIN, 13));
        if (modeAjoutEtudiant || etudiantAModifier != null) {
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0;
            panelFormulaire.add(new JLabel("Niveau:"), gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            panelFormulaire.add(cmbNiveau, gbc);
        }
        
        // Panel pour les champs de note (visible uniquement en mode saisie note)
        if (!modeAjoutEtudiant && etudiantAModifier == null) {
            panelNote = new JPanel(new GridBagLayout());
            GridBagConstraints gbcNote = new GridBagConstraints();
            gbcNote.insets = new Insets(5, 5, 5, 5);
            gbcNote.fill = GridBagConstraints.HORIZONTAL;
            
            // Séparateur
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            JSeparator separator = new JSeparator();
            panelFormulaire.add(separator, gbc);
            gbc.gridwidth = 1;
            
            // Code UE
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.weightx = 0;
            panelFormulaire.add(new JLabel("Code UE:"), gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            txtCodeUE = new JTextField(20);
            txtCodeUE.setFont(new Font("Arial", Font.PLAIN, 13));
            panelFormulaire.add(txtCodeUE, gbc);
            
            // Note
            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.weightx = 0;
            panelFormulaire.add(new JLabel("Note (/20):"), gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            txtNote = new JTextField(20);
            txtNote.setFont(new Font("Arial", Font.PLAIN, 13));
            panelFormulaire.add(txtNote, gbc);
        }
        
        panelPrincipal.add(panelFormulaire, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnEnregistrer = new JButton("Enregistrer");
        btnEnregistrer.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEnregistrer.setBackground(new Color(34, 139, 34));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.setFocusPainted(false);
        btnEnregistrer.addActionListener(e -> enregistrer());
        
        btnAnnuler = new JButton("Retour");
        btnAnnuler.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAnnuler.setBackground(new Color(220, 20, 60));
        btnAnnuler.setForeground(Color.WHITE);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.addActionListener(e -> dispose());
        
        panelBoutons.add(btnEnregistrer);
        panelBoutons.add(btnAnnuler);
        
        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        // En mode ajout étudiant, le matricule ne peut pas être recherché
        if (modeAjoutEtudiant) {
            btnRechercher.setEnabled(false);
        }
    }
    
    /**
     * Recherche un étudiant par son matricule
     */
    private void rechercherEtudiant() {
        String matricule = txtMatricule.getText().trim();
        
        if (matricule.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir un matricule",
                "Champ vide",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Etudiant etudiant = etudiantDAO.rechercherParMatricule(matricule);
            
            if (etudiant != null) {
                // Pré-remplir les champs
                txtNom.setText(etudiant.getNom());
                txtPrenom.setText(etudiant.getPrenom());
                cmbDepartement.setSelectedItem(etudiant.getDepartement());
                cmbSpecialite.setSelectedItem(etudiant.getSpecialite());
                cmbNiveau.setSelectedItem(etudiant.getNiveau());
                
                JOptionPane.showMessageDialog(this,
                    "Étudiant trouvé: " + etudiant.getNomComplet(),
                    "Recherche réussie",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                txtNom.setText("");
                txtPrenom.setText("");
                cmbDepartement.setSelectedIndex(0);
                cmbSpecialite.setSelectedIndex(0);
                cmbNiveau.setSelectedIndex(0);
                
                JOptionPane.showMessageDialog(this,
                    "Aucun étudiant trouvé avec ce matricule",
                    "Introuvable",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Charge les données d'un étudiant pour modification
     */
    private void chargerEtudiant(Etudiant etudiant) {
        txtMatricule.setText(etudiant.getMatricule());
        txtMatricule.setEditable(false); // Ne pas permettre la modification du matricule
        txtNom.setText(etudiant.getNom());
        txtPrenom.setText(etudiant.getPrenom());
        cmbDepartement.setSelectedItem(etudiant.getDepartement());
        cmbSpecialite.setSelectedItem(etudiant.getSpecialite());
        cmbNiveau.setSelectedItem(etudiant.getNiveau());
        btnRechercher.setEnabled(false);
    }
    
    /**
     * Enregistre l'étudiant ou la note
     */
    private void enregistrer() {
        // Si on est en mode ajout d'étudiant OU modification d'étudiant
        if (modeAjoutEtudiant || etudiantAModifier != null) {
            enregistrerEtudiant();
        } else {
            // Sinon, on est en mode saisie de note
            enregistrerNote();
        }
    }
    
    /**
     * Enregistre un étudiant (ajout ou modification)
     */
    private void enregistrerEtudiant() {
        String matricule = txtMatricule.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String departement = (String) cmbDepartement.getSelectedItem();
        String specialite = (String) cmbSpecialite.getSelectedItem();
        int niveau = (Integer) cmbNiveau.getSelectedItem();
        
        // Validation
        if (matricule.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires",
                "Champs incomplets",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Etudiant etudiant = new Etudiant(matricule, nom, prenom, departement, specialite, niveau);
        
        try {
            boolean succes;
            if (etudiantAModifier != null) {
                // Modification
                System.out.println("[DEBUG] Modification de l'étudiant: " + etudiant.getMatricule());
                succes = etudiantDAO.modifierEtudiant(etudiant);
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                        "Étudiant modifié avec succès!\n\nMatricule: " + etudiant.getMatricule() + "\nNom complet: " + etudiant.getNomComplet(),
                        "Modification réussie",
                        JOptionPane.INFORMATION_MESSAGE);
                    // Fermer la fenêtre après modification réussie
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Aucune modification effectuée.",
                        "Information",
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                // Ajout
                System.out.println("[DEBUG] Ajout de l'étudiant: " + etudiant.getMatricule());
                succes = etudiantDAO.ajouterEtudiant(etudiant);
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                        "Étudiant ajouté avec succès!\n\nMatricule: " + etudiant.getMatricule() + "\nNom complet: " + etudiant.getNomComplet(),
                        "Ajout réussi",
                        JOptionPane.INFORMATION_MESSAGE);
                    viderChamps();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur: L'étudiant n'a pas pu être ajouté.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String action = etudiantAModifier != null ? "la modification" : "l'ajout";
            JOptionPane.showMessageDialog(this,
                "Erreur lors de " + action + ":\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Enregistre une nouvelle note
     */
    private void enregistrerNote() {
        String matricule = txtMatricule.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String codeUE = txtCodeUE.getText().trim();
        String noteStr = txtNote.getText().trim();
        
        // Validation
        if (matricule.isEmpty() || codeUE.isEmpty() || noteStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le matricule, le code UE et la note sont obligatoires",
                "Champs incomplets",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double note;
        try {
            note = Double.parseDouble(noteStr.replace(',', '.'));
            
            if (note < 0 || note > 20) {
                JOptionPane.showMessageDialog(this,
                    "La note doit être comprise entre 0 et 20",
                    "Note invalide",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir une note valide",
                "Format invalide",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Vérifier si l'étudiant existe, sinon le créer
            Etudiant etudiant = etudiantDAO.rechercherParMatricule(matricule);
            
            if (etudiant == null) {
                if (nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "L'étudiant n'existe pas. Veuillez remplir le nom et le prénom",
                        "Étudiant inexistant",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Créer l'étudiant
                etudiant = new Etudiant(matricule, nom, prenom, "Maths-Info", "INF", 1);
                etudiantDAO.ajouterEtudiant(etudiant);
            }
            
            // Ajouter la note
            Note nouvelleNote = new Note(matricule, codeUE, note);
            boolean succes = noteDAO.ajouterNote(nouvelleNote);
            
            if (succes) {
                JOptionPane.showMessageDialog(this,
                    "Note enregistrée avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                viderChamps();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Vide tous les champs du formulaire
     */
    private void viderChamps() {
        txtMatricule.setText("");
        txtNom.setText("");
        txtPrenom.setText("");
        cmbDepartement.setSelectedIndex(0);
        cmbSpecialite.setSelectedIndex(0);
        cmbNiveau.setSelectedIndex(0);
        
        if (!modeAjoutEtudiant && etudiantAModifier == null) {
            txtCodeUE.setText("");
            txtNote.setText("");
        }
        
        txtMatricule.requestFocus();
    }
}