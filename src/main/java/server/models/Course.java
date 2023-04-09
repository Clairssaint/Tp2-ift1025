package server.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {

    private String name;
    private String code;
    private String session;
    int nombreDeCours;

    public Course(String name, String code, String session) {
        this.name = name;
        this.code = code;
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setNombreDeCoursnombreDeCours(int nombre){
        this.nombreDeCours = nombre;
    }

    public int getNombreDeCours() {
        return nombreDeCours;
    }



    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                ", session=" + session +
                '}';
    }
}
