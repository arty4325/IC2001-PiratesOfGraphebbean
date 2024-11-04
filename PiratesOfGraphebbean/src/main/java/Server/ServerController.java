package Server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String name;


    @FXML
    private TextArea serverTextArea;

    @FXML
    private TextField colocarNombre;

    @FXML
    protected void onHelloButtonClick() {
        serverTextArea.setWrapText(true);
        serverTextArea.appendText("Estoy usando el servidor!");
    }

    @FXML
    protected void changeAviable() {
        colocarNombre.setDisable(!colocarNombre.isDisabled());
    }

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

}
