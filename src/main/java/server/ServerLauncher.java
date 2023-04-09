package server;

/**
 * classe principale qui etablira la connexion du serveur sur un port
 */
public class ServerLauncher {
    public final static int PORT = 1337;

    /**
     * fonction principale qui va cree un nouveau serveur sur le port 1337 et qui lance une fonction run() qui gerera l'interaction entre le client et le serveur
     * @param args ligne de commande passer en parametre lors de l'execussion du code
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}