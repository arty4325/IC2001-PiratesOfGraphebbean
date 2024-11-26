module org.example.piratesofgraphebbean {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
//    requires mail; TODO DESCOMENTAR ESTO


    opens org.example.piratesofgraphebbean to javafx.fxml;
    opens Server to javafx.fxml;
    opens Cliente to javafx.fxml;
    exports org.example.piratesofgraphebbean;
    exports Server;
    exports Cliente;
}