module org.example.piratesofgraphebbean {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.piratesofgraphebbean to javafx.fxml;
    exports org.example.piratesofgraphebbean;
}