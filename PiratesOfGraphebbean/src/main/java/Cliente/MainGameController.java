package Cliente;

import Cliente.Grafo.MapaDelMar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private TextField txfMensaje;

    @FXML
    private TextArea txaAcciones;

    @FXML
    private ComboBox<String> itemComboBox;

    @FXML
    private TextField coordX;

    @FXML
    private TextField coordY;



    private void addToLista(int val) {
        switch (val) {
            case 1:
                itemComboBox.getItems().add("Tienda");
                break;
            case 4:
                itemComboBox.getItems().add("Mercado");
                break;
            default:
                System.out.println("Error");
        }
    }

    private int getNumberFromString(String item) {
        System.out.println(item);
        int number = switch (item) {
            case "Tienda" -> 1;
            case "Mercado" -> 4;
            default -> {
                System.out.println("Ítem no reconocido: " + item);
                yield -1;
            }
        };
        return number;
    }



    public void setUserData(Cliente _cliente){ // De una ves en esta funcion voy a crear el grafo
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        addToLista(1);
        addToLista(4);
        this.userName = _cliente.getNombreCliente();
        this.cliente = _cliente;
        mapaDelMar = new MapaDelMar(PantallaJugador, 20);
        mapaDelMar.asignarTipoIsla(1, 5, 2);
        mapaDelMar.inicializarGrid();
    }

    @FXML
    protected void onBtnSendClick(){
        String mensaje = cliente.getNombreCliente() + ": " + txfMensaje.getText();
        cliente.mandarMensaje(mensaje);
        txfMensaje.clear();
    }

    @FXML
    protected void btnProcessData(ActionEvent event){
        String selectedItem = itemComboBox.getValue();
        int selectedInt = getNumberFromString(selectedItem);
        int coordXInt = Integer.parseInt(coordX.getText());
        int coordYInt = Integer.parseInt(coordY.getText());
        // PROCESA
        // 1. Tengo que ver si lo puedo colocar donde se quiere:

        mapaDelMar.asignarTipoIsla(coordXInt, coordYInt, selectedInt);
        mapaDelMar.inicializarGrid();


        itemComboBox.getItems().remove(selectedItem);
    }

    public TextArea getTxaChat() {
        return txaChat;
    }

    public TextArea getTxaAcciones(){
        return txaAcciones;
    }
}
