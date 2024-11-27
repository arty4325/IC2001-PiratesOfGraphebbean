package Cliente;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ClienteScreenController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField txfNombre;

    @FXML
    private Button btnNombre;

    private Cliente cliente;

    @FXML
    public void initialize(){ //es como el constructor por decir asi/\
        cliente = new Cliente(this);
        new Thread(() -> {
            cliente.run();
        }).start();
    }


    @FXML
    protected void onBtnNombreClick(ActionEvent event){
        //validaciones de nombre, si no es nombre que sirve, entonces return
        if(cliente.mandarNombreAServer(txfNombre.getText())){
            btnNombre.setDisable(true);
            txfNombre.setDisable(true);
            cliente.mandarIniciarAServer();
        }

    }

    public void moveMain() throws IOException {
            System.out.println("Todas las partidas inician");
            Platform.runLater(() -> { //para asegurar que corra en el thread de JavaFX application, sino se cae.
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/MainWindow.fxml"));
                    Parent root = loader.load();
                    MainGameController controller = loader.getController();
                    controller.setUserData(cliente);
                    cliente.setGameController(controller);
                    stage = MainCliente.getPrimaryStage();
                    scene = new Scene(root);
                    stage.setTitle( "[" + cliente.getIdCliente() + "] " + cliente.getNombreCliente());
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }



}
