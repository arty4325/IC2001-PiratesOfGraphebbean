package Cliente;

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

    @FXML
    private Button btnIniciar;

    @FXML
    private Text infoGame;

    private Cliente cliente;

    @FXML
    public void initialize(){ //es como el constructor por decir asi
        System.out.println("caca");
        cliente = new Cliente(this);
        new Thread(() -> {
            cliente.run();
        }).start();
    }


    @FXML
    protected void onBtnNombreClick(){
        cliente.mandarNombreAServer(txfNombre.getText());
        btnNombre.setDisable(true);
        //Aqui podría poner para enable lo que sea que vaya a usar para que hagan la matriz.
        //El enable del boton iniciar es solo para test ahorita como no está lo de la matriz.
        btnIniciar.setDisable(false);
    }

    @FXML
    protected void onBtnIniciarClick(){
        cliente.mandarIniciarAServer();
        btnIniciar.setDisable(true);
        //y ya despues de aqui ocupamos ver como podríamos hacer.
    }

    public void moveMain(ActionEvent event) throws IOException {
        boolean canMove = cliente.getCanStart();
        if(canMove) {
            System.out.println("Todas las partidas inician");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/MainWindow.fxml"));
            Parent root = loader.load();
            MainGameController controller = loader.getController();
            controller.setUserData(cliente);
            cliente.setGameController(controller);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            infoGame.setText("No todos los jugadores estan listos aún");
        }
    }



}
