package server;

import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client_simple implements Serializable {
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {
        String line;
        Server server;
        try {
            Socket client = new Socket("127.0.0.1", 1337);
            ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());

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
        String line,comm;
        Course cours;
        try {
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            int temp = 0;
            int i = 0;
            System.out.println("les cours offerts pendant la session d'"+ session+" sont:");

            do  {
                temp++;
                cours = (Course) ois.readObject();
                System.out.println(temp + ". " + cours.getCode() + "\t" + cours.getName());
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
                        System.out.println("inscription au cours");
                        break;
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
}
