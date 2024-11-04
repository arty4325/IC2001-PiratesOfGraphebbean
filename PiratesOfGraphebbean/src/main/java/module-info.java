module org.example.piratesofgraphebbean {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens org.example.piratesofgraphebbean to javafx.fxml;
    exports org.example.piratesofgraphebbean;
}