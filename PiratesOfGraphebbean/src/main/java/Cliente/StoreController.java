package Cliente;

import Cliente.Grafo.MapaDelMar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Cliente cliente;
    private MapaDelMar mapaDelMar;
    private List<String> listaItems = new ArrayList<String>();


    public void setGameController(Cliente _cliente, MapaDelMar mapaDelMar){ // De una ves en esta funcion voy a crear el grafo
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        this.cliente = _cliente;
        this.mapaDelMar = mapaDelMar;

    }


    @FXML
    protected void btnGoGame(){
        System.out.println("Todas las partidas inician");
        Platform.runLater(() -> { //para asegurar que corra en el thread de JavaFX application, sino se cae.
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/MainWindow.fxml"));
                Parent root = loader.load();
                MainGameController controller = loader.getController();
                controller.backFromStore(this.cliente, this.mapaDelMar);
                controller.loadAccordion(listaItems);
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
    protected void btnAddEnergy(){
        // Primero hacemos una lista, despues en el boton de volver a la funcion llamamos ahi algo que actualice la lista
        listaItems.add("Tienda");
    }


}
