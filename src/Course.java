import java.io.Serializable;

public class Course implements Serializable {
    String session, codeCours, nomCours;

    public Course(String codeCours, String nomCours, String session){
        this.codeCours = codeCours;
        this.nomCours = nomCours;
        this.session = session;
    }

    //getters et setters
    public String getCodeCours() {
        return codeCours;
    }

    public String getNomCours() {
        return nomCours;
    }

    public String getSession() {
        return session;
    }

    public void setCodeCours(String codeCours) {
        this.codeCours = codeCours;
    }

    public void setNomCours(String nomCours) {
        this.nomCours = nomCours;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name=" + nomCours +
                ", code=" + codeCours +
                ", session=" + session +
                '}';
    }
}
