package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * class serveur qui ouvre une connexion sur un port et attend la connexion d'un client pour ensuite executer
 * la requete envoyer par le client
 * la classe a pour role principale d'envoyer une liste des cours d'une session au client, et ensuite d'excuter
 * l'inscription a un cours demandé par le client
 */
public class Server {
    /**
     * variable qui permettra de valider la requete envoyer par le client
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * variable qui permettra de valider la requete envoyer par le client
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;


    private final ArrayList<EventHandler> handlers;

    /**
     * constructeur de la methode serveur qui va initialiser les parametres du serveur qui sont:
     * le port et le nombre de personne qui se connectera au serveur, et la liste des evenements
     * @param port port sur le quel la connexion du serveur sera ouvert.
     * @throws IOException lance une exception lorsqu'il y a une erreur d'ecriture ou de sortie
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * fonction qui ajoute un evenement a la des evenements EventHandler
     * @param h nouveau evenement qui sera ajouter a la liste
     */
    public void addEventHandler(EventHandler h) {

        this.handlers.add(h);
    }

    /**
     * boucle d'evenement, qui gere les evenements en passant a travers une liste contenant les evements
     * @param cmd la requete qui sera lancer
     * @param arg arguement envoyer avec la requete
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * fonction qui attend la connexion d'un client au serveur, pour ensuite ouvrir les canaux de communication ObjectInputStream et ObjectOutputStream entre les deux
     * apres une connexion reussite, la fonction listen() sera lancée pour le traitement des requetes du client suivi d'une deconnexion
     */
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

    /**
     * fonction qui recoit la commande envoyer dans le flux par le client avec l'object objectInputStream,la
     * transforme en paire "cle valeur",les affecter respectivement aux variables commande et argument pour ensuite envoyer en parametre au gestionnaire d'evenement pour la suite du traitement.
     * @throws IOException exception qui gere les erreurs d'entrée et sortie
     * @throws ClassNotFoundException exception qui lance une erreur si une classe n'est pas trouvé
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * lire une chaine de caractere, et la transformer en paire clé valeur
     * @param line chaine de caractere qu'on veut transformer en paire <clé valeur>
     * @return retourne une paire <clé valeur>
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * fonction qui gere la fermeture des streams et la deconnexion du client apres une requete
     * @throws IOException lancera une exception s'il ya une errreur d'entrée ou de sortie generale
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     *fonction qui recois la commande et l'argument corespondant et lance requete contenu dans la commande
     * @param cmd commande recu pour lancer la bonne requete "charger les cours ou s'inscrire"
     * @param arg argument envoyer avec la commande(la session pour la quelle on veut afficher la liste des cours/ l,enregistrement contenant les informations de l'etudiant et du cours au quel il veut s'inscrire
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
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
            BufferedReader br = new BufferedReader(new FileReader( new File("cours.txt")));
            while( (line= br.readLine())!= null){
                String [] tab = line.split("\t");

                Course course = new Course(tab[1], tab[0], tab[2]);
                listCourse.add(course);
                if(course.getSession().equals(arg)){
                    listCoursSessionDemande.add(course);
                    temp++;

                }

            }

            for(int i=0; i < temp; i++){
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
            try {
                enregistrement = (String) objectInputStream.readObject();
                BufferedWriter bw = new BufferedWriter(new FileWriter( new File("inscription.txt")));
                bw.append(enregistrement);
                bw.flush();
                String [] tab = enregistrement.split("\t");
                objectOutputStream.writeObject("Félicitation! Inscription réussie de "+ tab[3]+" au cours "+tab[1]);
            }catch(IOException | ClassNotFoundException oe){
                System.out.println("erreur lors de l'ecriture ou de l'ouverture du fichier");
            }



    }
}

