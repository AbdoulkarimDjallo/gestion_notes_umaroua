package model;

/**
 * Classe représentant un étudiant de la Faculté des Sciences
 */
public class Etudiant {
    private String matricule;
    private String nom;
    private String prenom;
    private String departement;
    private String specialite;
    private int niveau;
    
    // Constructeur complet
    public Etudiant(String matricule, String nom, String prenom, String departement, String specialite, int niveau) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.departement = departement;
        this.specialite = specialite;
        this.niveau = niveau;
    }
    
    // Constructeur vide
    public Etudiant() {
    }
    
    // Getters et Setters
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getDepartement() {
        return departement;
    }
    
    public void setDepartement(String departement) {
        this.departement = departement;
    }
    
    public String getSpecialite() {
        return specialite;
    }
    
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
    
    public int getNiveau() {
        return niveau;
    }
    
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
    
    /**
     * Retourne le nom complet de l'étudiant
     * @return nom et prénom
     */
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    
    @Override
    public String toString() {
        return "Etudiant{" +
                "matricule='" + matricule + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", departement='" + departement + '\'' +
                ", specialite='" + specialite + '\'' +
                ", niveau=" + niveau +
                '}';
    }
    
    /**
     * Valide les données de l'étudiant
     * @return true si les données sont valides
     */
    public boolean estValide() {
        return matricule != null && !matricule.trim().isEmpty() &&
               nom != null && !nom.trim().isEmpty() &&
               prenom != null && !prenom.trim().isEmpty() &&
               departement != null && !departement.trim().isEmpty() &&
               specialite != null && !specialite.trim().isEmpty() &&
               niveau >= 1 && niveau <= 5;
    }
}