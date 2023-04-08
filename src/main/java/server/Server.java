package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;


    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {

        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            System.out.println("recu commande");
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {

        ArrayList<Course> listCourse = new ArrayList<>();
        ArrayList<Course> listCoursSessionDemande = new ArrayList<>();
        try {
            String line;
            int temp=0;
            BufferedReader br = new BufferedReader(new FileReader( new File("src/main/java/server/data/cours.txt")));
            System.out.println("lecture du fichier cours");
            while( (line= br.readLine())!= null){
                String [] tab = line.split("\t");

                Course course = new Course(tab[1], tab[0], tab[2]);
                System.out.println("cours :"+course.getName());
                listCourse.add(course);
                if(course.getSession().equals(arg)){
                    listCoursSessionDemande.add(course);
                    temp++;

                }

            }

            for(int i=0; i < temp; i++){
                System.out.println("envoie cours :"+listCoursSessionDemande.get(i).getName());
                listCoursSessionDemande.get(i).setNombreDeCoursnombreDeCours(temp);
                objectOutputStream.writeObject(listCoursSessionDemande.get(i));
            }
            objectOutputStream.flush();


        } catch (IOException ex) {
            System.out.println("erreur lors de l'ouverture ou de l'ecriture du ficihier");
        }


    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration()  {
        String enregistrement;
            System.out.println("accès 1");
            try {
                enregistrement = (String) objectInputStream.readObject();
                System.out.println("voici lenregis: "+enregistrement+ "taille: "+enregistrement.length());
                BufferedWriter bw = new BufferedWriter(new FileWriter( new File("src/main/java/server/data/inscription.txt")));
                bw.append(enregistrement);
                bw.flush();
                String [] tab = enregistrement.split("\t");
                objectOutputStream.writeObject("Félicitation! Inscription réussie de "+ tab[3]+" au cours "+tab[1]);
            }catch(IOException | ClassNotFoundException oe){
                System.out.println("pas d'objet");
            }



    }
}
