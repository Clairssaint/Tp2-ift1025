package server;

/**
 * interface fonctionnelle qui permettra d'appeler le bon evenement au bon moment
 * @void appel a la fonction de gestion des evenements
 */
@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg);
}
