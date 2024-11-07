package Server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerScreenController {
    private Stage stage;
    private Scene scene;
    private Parent root;




    @FXML
    private TextArea txaConsolaServidor;

    private Server servidor;

    @FXML
    public void initialize() {
        servidor = new Server(this);
        Thread serverThread = new Thread(() -> servidor.run());
        serverThread.setDaemon(true);
        serverThread.start();
    }


    @FXML
    public void write(String texto){
        txaConsolaServidor.appendText(texto);
    }











/*
    @FXML
    protected void switchToSecondWindow(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Server/SecondWindow.fxml"));
        Parent root = loader.load();

        // Obtén el controlador del nuevo FXML
        SecondWindow controller = loader.getController();

        // Pasa el valor (por ejemplo, el idUsuario)
        controller.setName("Arturo");

        // Cambia la escena
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

*/

}
