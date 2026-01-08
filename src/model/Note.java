package model;

/**
 * Classe représentant une note d'un étudiant
 */
public class Note {
    private int id;
    private String matricule;
    private String codeUE;
    private double note;
    
    // Constructeur complet
    public Note(int id, String matricule, String codeUE, double note) {
        this.id = id;
        this.matricule = matricule;
        this.codeUE = codeUE;
        this.note = note;
    }
    
    // Constructeur sans ID (pour insertion)
    public Note(String matricule, String codeUE, double note) {
        this.matricule = matricule;
        this.codeUE = codeUE;
        this.note = note;
    }
    
    // Constructeur vide
    public Note() {
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public String getCodeUE() {
        return codeUE;
    }
    
    public void setCodeUE(String codeUE) {
        this.codeUE = codeUE;
    }
    
    public double getNote() {
        return note;
    }
    
    public void setNote(double note) {
        this.note = note;
    }
    
    /**
     * Valide que la note est dans l'intervalle [0, 20]
     * @return true si la note est valide
     */
    public boolean estValide() {
        return note >= 0 && note <= 20 &&
               matricule != null && !matricule.trim().isEmpty() &&
               codeUE != null && !codeUE.trim().isEmpty();
    }
    
    /**
     * Détermine si l'étudiant a validé l'UE (note >= 10)
     * @return true si validé
     */
    public boolean estValide10() {
        return note >= 10;
    }
    
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", matricule='" + matricule + '\'' +
                ", codeUE='" + codeUE + '\'' +
                ", note=" + note +
                '}';
    }
}