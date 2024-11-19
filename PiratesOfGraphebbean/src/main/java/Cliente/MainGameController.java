package Cliente;

import Cliente.Grafo.MapaDelMar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.mail.Store;
import java.io.IOException;
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

    public void loadAccordion(List<String> items) {
        for(int i = 0; i < items.size(); i++) {
            itemComboBox.getItems().add(items.get(i));
        }
        items.clear();
    }



    public void setUserData(Cliente _cliente){ // De una ves en esta funcion voy a crear el grafo
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        itemComboBox.getItems().add("Tienda");
        itemComboBox.getItems().add("Mercado");
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
    protected void btnGoStore(){
        Platform.runLater(() -> { //para asegurar que corra en el thread de JavaFX application, sino se cae.
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/StoreScreen.fxml"));
                Parent root = loader.load();
                StoreController controller = loader.getController();
                controller.setGameController(cliente);
                //cliente.setGameController(controller);
                stage = MainCliente.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Log or handle the exception appropriately
            }
        });
    }

    @FXML
    protected void btnGoMain(){
        Platform.runLater(() -> { //para asegurar que corra en el thread de JavaFX application, sino se cae.
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/StoreScreen.fxml"));
                Parent root = loader.load();
                MainGameController controller = loader.getController();
                controller.setUserData(cliente);
                cliente.setGameController(controller);
                stage = MainCliente.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Log or handle the exception appropriately
            }
        });
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
