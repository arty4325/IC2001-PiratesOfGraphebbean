package Cliente;

import Cliente.Grafo.MapaDelMar;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainGameController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String userName;
    private Cliente cliente;
    private MapaDelMar mapaDelMar;

    @FXML
    private Button btnSend;

    @FXML
    private GridPane PantallaJugador;

    @FXML
    private TextArea txaChat;

    public void setUserData(Cliente _cliente){ // De una ves en esta funcion voy a crear el grafo
        this.userName = _cliente.getNombreCliente();
        this.cliente = _cliente;
        mapaDelMar = new MapaDelMar(PantallaJugador, 20);
        mapaDelMar.asignarTipoIsla(1, 2);
        mapaDelMar.inicializarGrid();
    }

    @FXML
    protected void onBtnSendClick(){
        cliente.mandarIniciarAServer();

    }

    public TextArea getTxaChat() {
        return txaChat;
    }
}
