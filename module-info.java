module com.example.demo4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens Sample to javafx.fxml;
    exports Sample;
}