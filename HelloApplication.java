package Sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    public static void main(String[] args) {
        HelloApplication.launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));

        primaryStage.setTitle("inscriptionUdem");
        primaryStage.setScene(new Scene(root,600,400));
        primaryStage.show();

    }


}