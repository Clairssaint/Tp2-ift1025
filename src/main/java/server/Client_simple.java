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
    static ObjectOutputStream os;
    static ObjectInputStream ois;
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
                    case "2":
                        os.writeObject("CHARGER Hiver");
                        os.flush();
                        chargerCours(client, "hiver");
                    case"3":
                        os.writeObject("CHARGER Ete");
                        os.flush();
                        chargerCours(client, "ete");
                    default:
                        System.out.println("commande inconnue");
                }




            }



        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void chargerCours(Socket client, String session) {
        String line;
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
                        break;
                    case "2":
                        os.writeObject("INSCRIRE session");
                        inscriptionAuCours(client);
                    default:
                        System.out.println("commande inconnue");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void inscriptionAuCours(Socket client) {
        String confirmation;
        try {

            String prenom, nom, email, matricule, codeCours;
            System.out.print("Veuillez saisir votre prenom: ");
            prenom = scan.nextLine();
            System.out.print("Veuillez saisir votre nom: ");
            nom = scan.nextLine();
            System.out.print("Veuillez saisir votre email: ");
            email = scan.nextLine();
            System.out.print("Veuillez saisir votre matricule: ");
            matricule = scan.nextLine();
            System.out.print("Veuillez saisir le code du cours: ");
            codeCours = scan.nextLine();
            for (int i = 0; i < temp; i++) {
                if (codeCours.equals(listDesCoursDeLaSession.get(i).getCode())) {
                    coursAAjouter = listDesCoursDeLaSession.get(i);
                }
            }
            RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, coursAAjouter);
            os.writeObject(registrationForm);
            confirmation = ois.readLine();
            System.out.println(confirmation);
            os.close();
        }catch (IOException oi){
            System.out.println("erreur lors de L'envoie de l'object");
        }
    }

}
