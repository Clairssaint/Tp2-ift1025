package server;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client_simple implements Serializable {
    static Scanner scan = new Scanner(System.in);
    static int temp = 0;
    static ArrayList<Course> listDesCoursDeLaSession = new ArrayList<>();
    static Course coursAAjouter;
    static RegistrationForm registrationForm;
    static ObjectOutputStream os,oos;
    static ObjectInputStream ois, is;
    static String enregistrement;
    public static void main(String[] args) {
        String line;

        try {
            Socket client = new Socket("127.0.0.1", 1337);
            os = new ObjectOutputStream(client.getOutputStream());

            System.out.println("***Bienvenue au portail d'inscription de cours de l'UDEM***");
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            System.out.println("1. Autnomne"+"\n"+"2. Hiver"+"\n"+"3. Ete");
            System.out.print("choix: ");


            if((line = scan.nextLine()) != null){
                switch(line){
                    case "1":
                        os.writeObject("CHARGER Automne");
                        os.flush();

                        chargerCours(client, "automne");
                        break;
                    case "2":
                        os.writeObject("CHARGER Hiver");
                        os.flush();
                        chargerCours(client, "hiver");
                        break;
                    case "3":
                        os.writeObject("CHARGER Ete");
                        os.flush();
                        chargerCours(client, "ete");
                        break;
                    default :
                        System.out.println("commande inconnue");
                        break;
                }




            }



        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void chargerCours(Socket client, String session) {
        String line, confirmation;
        Course cours;
        try {
             ois = new ObjectInputStream(client.getInputStream());

            int i = 0;
            System.out.println("les cours offerts pendant la session d'"+ session+" sont:");

            do  {
                temp++;
                cours = (Course) ois.readObject();
                System.out.println(temp + ". " + cours.getCode() + "\t" + cours.getName());
                listDesCoursDeLaSession.add(cours);
                }while(i++ < cours.getNombreDeCours()-1);

            System.out.println("choix:");
            System.out.println("1. Consulter les cours offerts pour une autre session" + "\n"+"2. Inscription");
            System.out.print("choix: ");
            if(( line = scan.nextLine()) != null){
                switch (line){
                    case "1":
                        System.out.println("choix d'une autre session");
                        main(new String[]{" "});
                        break;
                    case "2":
                        Socket cl = new Socket("127.0.0.1", 1337);
                        oos = new ObjectOutputStream(cl.getOutputStream());
                        is = new ObjectInputStream(cl.getInputStream());

                        inscriptionAuCours();
                        oos.writeObject("INSCRIRE "  );
                        oos.writeObject(enregistrement);
                        oos.flush();
                        confirmation = (String) is.readObject();
                        System.out.println(confirmation);
                        is.close();
                        oos.close();
                        break;
                    default:
                        System.out.println("commande inconnue");
                }
            }

        } catch (IOException e) {
            System.out.println("erreur lors de l'ouverture ou de l'ecriture du fichier");
        } catch (ClassNotFoundException e) {
            System.out.println("classe non trouver");
        }
    }
    public static void inscriptionAuCours(){

        String prenom, nom, email, codeCours;
        int  matricule, compteur=0;
        System.out.print("Veuillez saisir votre prenom: ");
        prenom = scan.nextLine();

        System.out.print("Veuillez saisir votre nom: ");
        nom = scan.nextLine();

        System.out.print("Veuillez saisir votre email: ");
        email = scan.nextLine();
        validateurDeCourriel(email);

        System.out.print("Veuillez saisir votre matricule: ");
        matricule = Integer.parseInt(scan.nextLine());
        validateurDeMatricule(matricule);

        System.out.print("Veuillez saisir le code du cours: ");
        codeCours = scan.nextLine();
        for (int i = 0; i < temp; i++) {
            if (codeCours.equals(listDesCoursDeLaSession.get(i).getCode())) {
                coursAAjouter = listDesCoursDeLaSession.get(i);
                compteur++;
            }

        }if(compteur==0){
            throw new IllegalArgumentException("vous ne pouvez pas vous incrire a ce cours car il n'est pas offert pour cette session");
        }
        registrationForm = new RegistrationForm(prenom, nom, email, matricule, coursAAjouter);
        enregistrement = registrationForm.getCourse().getSession() +"\t"+ registrationForm.getCourse().getCode() +"\t"+ registrationForm.getMatricule() +"\t"+ registrationForm.getPrenom() +"\t"+ registrationForm.getNom()+"\t"+registrationForm.getEmail();


    }

    /**
     * fonction qui recoit en parametre un courriel et vefirie si elle respect le formant string@domain
     * @param email pour la quelle le format sera verifier
     */
    public static void validateurDeCourriel (String email){
        String validaeurEmail = ".+@.+\\.[a-z]+";
        if(email.matches(validaeurEmail) != true){
            throw new IllegalArgumentException("adresse email incorrect");
        }
    }

    /**
     * fonction recupere la matricule, la convertie en string et verifi si elle contient 8 chiffres sinon lance une exception
     * @param matricule pour la quelle on va verifier le format
     */
    public static void validateurDeMatricule(int matricule){
        String validateurMatricule = String.valueOf(matricule);
        if(validateurMatricule.length()!=8){
            throw new IllegalArgumentException("matricule invalide");
        }
    }

}
