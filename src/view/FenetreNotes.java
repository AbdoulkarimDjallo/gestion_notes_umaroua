package view;

import controller.NoteDAO;
import model.Note;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Fenêtre affichant les notes d'un étudiant
 */
public class FenetreNotes extends JFrame {
    private String matricule;
    private String nomComplet;
    private JTable tableNotes;
    private DefaultTableModel modelTable;
    private NoteDAO noteDAO;
    private FenetrePrincipale fenetrePrincipale;
    private JLabel lblMoyenne;
    
    public FenetreNotes(String matricule, String nomComplet, FenetrePrincipale fenetrePrincipale) {
        this.matricule = matricule;
        this.nomComplet = nomComplet;
        this.fenetrePrincipale = fenetrePrincipale;
        try {
            noteDAO = new NoteDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur d'initialisation: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            noteDAO = null;
        }
    
        initialiserInterface();
        chargerNotes();
    }
    
    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        setTitle("Notes de l'étudiant - " + nomComplet);
        setSize(700, 500);
        setLocationRelativeTo(fenetrePrincipale);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // En-tête avec informations de l'étudiant
        JPanel panelEnTete = new JPanel(new BorderLayout());
        panelEnTete.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblNom = new JLabel("NOTES DE: " + nomComplet.toUpperCase());
        lblNom.setFont(new Font("Arial", Font.BOLD, 18));
        lblNom.setForeground(new Color(0, 255, 255));
        
        JLabel lblMatricule = new JLabel("Matricule: " + matricule);
        lblMatricule.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMatricule.setForeground(new Color(34, 139, 34));
        
        panelEnTete.add(lblNom, BorderLayout.NORTH);
        panelEnTete.add(lblMatricule, BorderLayout.CENTER);
        
        panelPrincipal.add(panelEnTete, BorderLayout.NORTH);
        
        // Tableau des notes
        String[] colonnes = {"Code UE", "Note", "Statut"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableNotes = new JTable(modelTable);
        tableNotes.setRowHeight(25);
        tableNotes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableNotes.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Ajuster la largeur des colonnes
        tableNotes.getColumnModel().getColumn(0).setPreferredWidth(200);
        tableNotes.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableNotes.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(tableNotes);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel du bas avec moyenne et boutons
        JPanel panelBas = new JPanel(new BorderLayout(10, 10));
        
        // Panel pour la moyenne
        JPanel panelMoyenne = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMoyenne.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        lblMoyenne = new JLabel();
        lblMoyenne.setFont(new Font("Arial", Font.BOLD, 14));
        panelMoyenne.add(lblMoyenne);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnTelechargerPDF = new JButton("Télécharger PDF");
        btnTelechargerPDF.setFont(new Font("Arial", Font.PLAIN, 14));
        btnTelechargerPDF.setBackground(new Color(255, 87, 34));
        btnTelechargerPDF.setForeground(Color.WHITE);
        btnTelechargerPDF.setFocusPainted(false);
        btnTelechargerPDF.addActionListener(e -> exporterPDF());
        
        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.setFont(new Font("Arial", Font.PLAIN, 14));
        btnActualiser.addActionListener(e -> chargerNotes());
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.setFont(new Font("Arial", Font.PLAIN, 14));
        btnRetour.setBackground(new Color(34, 139, 34));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.setFocusPainted(false);
        btnRetour.addActionListener(e -> {
            dispose();
        });
        
        panelBoutons.add(btnTelechargerPDF);
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnRetour);
        
        panelBas.add(panelMoyenne, BorderLayout.NORTH);
        panelBas.add(panelBoutons, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelBas, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    /**
     * Charge les notes de l'étudiant depuis la base de données
     */
    private void chargerNotes() {
        try {
            modelTable.setRowCount(0); // Vider le tableau
            List<Note> notes = noteDAO.obtenirNotesParMatricule(matricule);
            
            if (notes.isEmpty()) {
                JLabel lblAucuneNote = new JLabel("Aucune note enregistrée pour cet étudiant");
                lblAucuneNote.setFont(new Font("Arial", Font.ITALIC, 14));
                lblAucuneNote.setForeground(Color.GRAY);
                lblAucuneNote.setHorizontalAlignment(SwingConstants.CENTER);
                
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(lblAucuneNote, BorderLayout.CENTER);
                
                lblMoyenne.setText("Moyenne générale: N/A");
                return;
            }
            
            for (Note note : notes) {
                String statut = note.getNote() >= 10 ? "Validé" : "Non validé";
                Object[] ligne = {
                    note.getCodeUE(),
                    String.format("%.2f", note.getNote()),
                    statut
                };
                modelTable.addRow(ligne);
            }
            
            // Calculer et afficher la moyenne
            double moyenne = noteDAO.calculerMoyenne(matricule);
            lblMoyenne.setText(String.format("Moyenne générale: %.2f/20", moyenne));
            
            if (moyenne >= 10) {
                lblMoyenne.setForeground(new Color(34, 139, 34));
            } else {
                lblMoyenne.setForeground(new Color(220, 20, 60));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des notes:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Exporte les notes de l'étudiant en PDF
     */
    private void exporterPDF() {
        try {
            // Récupérer les notes
            List<Note> notes = noteDAO.obtenirNotesParMatricule(matricule);
            
            if (notes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucune note à exporter pour cet étudiant.",
                    "Aucune donnée",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Ouvrir une boîte de dialogue pour choisir l'emplacement de sauvegarde
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le relevé de notes");
            
            // Nom de fichier par défaut
            String nomFichier = "Releve_Notes_" + matricule.replace("/", "_") + ".pdf";
            fileChooser.setSelectedFile(new File(nomFichier));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fichierPDF = fileChooser.getSelectedFile();
                
                // Ajouter l'extension .pdf si elle n'est pas présente
                if (!fichierPDF.getName().toLowerCase().endsWith(".pdf")) {
                    fichierPDF = new File(fichierPDF.getAbsolutePath() + ".pdf");
                }
                
                // Créer le PDF
                genererPDF(fichierPDF, notes);
                
                // Demander si l'utilisateur veut ouvrir le fichier
                int reponse = JOptionPane.showConfirmDialog(this,
                    "Relevé de notes exporté avec succès!\n\nVoulez-vous ouvrir le fichier?",
                    "Export réussi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (reponse == JOptionPane.YES_OPTION) {
                    // Ouvrir le fichier avec l'application par défaut
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(fichierPDF);
                    }
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la r\u00e9cup\u00e9ration des notes:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(this,
                "Erreur: Biblioth\u00e8que manquante pour g\u00e9n\u00e9rer le PDF!\n\n" +
                "PDFBox n\u00e9cessite des d\u00e9pendances suppl\u00e9mentaires:\n" +
                "- commons-logging.jar\n\n" +
                "Veuillez t\u00e9l\u00e9charger et placer ce fichier dans le dossier 'lib/'\n\n" +
                "T\u00e9l\u00e9chargement: https://commons.apache.org/proper/commons-logging/download_logging.cgi",
                "Biblioth\u00e8que manquante",
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la cr\u00e9ation du PDF:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue lors de l'export PDF:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Génère le document PDF avec les notes de l'étudiant
     */
    private void genererPDF(File fichier, List<Note> notes) throws IOException, SQLException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 12;
                
                // En-tête du document
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontNormal = PDType1Font.HELVETICA;
                
                contentStream.setFont(fontBold, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("UNIVERSITE DE MAROUA");
                contentStream.endText();
                yPosition -= 25;
                
                contentStream.setFont(fontNormal, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Faculte des Sciences");
                contentStream.endText();
                yPosition -= 30;
                
                // Ligne de séparation
                contentStream.setLineWidth(1);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= 30;
                
                // Titre
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RELEVE DE NOTES");
                contentStream.endText();
                yPosition -= 30;
                
                // Informations de l'étudiant
                contentStream.setFont(fontNormal, fontSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Etudiant: " + nomComplet);
                contentStream.endText();
                yPosition -= 20;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Matricule: " + matricule);
                contentStream.endText();
                yPosition -= 20;
                
                // Date d'édition
                String dateEdition = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date d'edition: " + dateEdition);
                contentStream.endText();
                yPosition -= 30;
                
                // En-tête du tableau
                contentStream.setFont(fontBold, fontSize);
                float col1X = margin;
                float col2X = margin + 300;
                float col3X = margin + 400;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X, yPosition);
                contentStream.showText("Code UE");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(col2X, yPosition);
                contentStream.showText("Note");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(col3X, yPosition);
                contentStream.showText("Statut");
                contentStream.endText();
                yPosition -= 5;
                
                // Ligne sous l'en-tête
                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= 15;
                
                // Liste des notes
                contentStream.setFont(fontNormal, fontSize);
                for (Note note : notes) {
                    // Vérifier si on a besoin d'une nouvelle page
                    if (yPosition < 100) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        PDPageContentStream newContentStream = new PDPageContentStream(document, page);
                        yPosition = page.getMediaBox().getHeight() - margin;
                        contentStream.close();
                    }
                    
                    String statut = note.getNote() >= 10 ? "Valide" : "Non valide";
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col1X, yPosition);
                    contentStream.showText(note.getCodeUE());
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col2X, yPosition);
                    contentStream.showText(String.format("%.2f / 20", note.getNote()));
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col3X, yPosition);
                    contentStream.showText(statut);
                    contentStream.endText();
                    
                    yPosition -= 20;
                }
                
                yPosition -= 10;
                
                // Ligne avant la moyenne
                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;
                
                // Moyenne générale
                double moyenne = noteDAO.calculerMoyenne(matricule);
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("Moyenne generale: %.2f / 20", moyenne));
                contentStream.endText();
                yPosition -= 20;
                
                // Mention
                String mention;
                if (moyenne >= 16) {
                    mention = "Tres Bien";
                } else if (moyenne >= 14) {
                    mention = "Bien";
                } else if (moyenne >= 12) {
                    mention = "Assez Bien";
                } else if (moyenne >= 10) {
                    mention = "Passable";
                } else {
                    mention = "Ajourne";
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Mention: " + mention);
                contentStream.endText();
            }
            
            // Sauvegarder le document
            document.save(fichier);
        }
    }
}