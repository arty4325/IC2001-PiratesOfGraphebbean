package Cliente;

import Cliente.Grafo.MapaDelMar;
import Modelos.CasesEnThreadServidor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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

    @FXML
    private ComboBox<String> cbxComponentes;

    @FXML
    private ComboBox<String> cbxJugadores;

    @FXML
    private Spinner<Integer> sbxPrecioComponente;

    @FXML
    private Label lblDinero;

    public void setGameController(Cliente _cliente, MapaDelMar mapaDelMar){ // De una ves en esta funcion voy a crear el grafo
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        this.cliente = _cliente;
        this.mapaDelMar = mapaDelMar;
        actualizarComponentesCbx();
        setCbxJugadores();
        setSpinners();
        actualizarDinero();
    }


    public void actualizarComponentesCbx() {
        cbxComponentes.getItems().clear();
        for(int i = 0; i < cliente.getListaItems().size(); i++) {
            cbxComponentes.getItems().add(cliente.getListaItems().get(i));
        }
    }

    public void actualizarDinero(){
        lblDinero.setText("" + cliente.getDinero());
    }

    private void setCbxJugadores(){
        for (String nombreOponente : cliente.getNombresOponentes()) {
            cbxJugadores.getItems().add(nombreOponente);
        }
    }
    private void setSpinners(){
        sbxPrecioComponente.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 3000, 100, 100));
    }

    //si no hay nada es null
    @FXML
    protected void onBtnPrecioEspecificoClick(){
        String selectedItem = cbxComponentes.getValue();
        if(selectedItem == null){return;}
        cbxComponentes.getItems().remove(selectedItem);
        cliente.getListaItems().remove(selectedItem);
        cliente.subirDinero(getSellingPriceComponente(selectedItem));
        actualizarDinero();
    }

    @FXML
    protected void onBtnPrecioAcordadoClick(){
        String selectedItem = cbxComponentes.getValue();
        String selectedPlayer = cbxJugadores.getValue();
        int precio = sbxPrecioComponente.getValue();
        if(selectedItem == null || selectedPlayer == null){return;}
        try {
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.PROPONERVENTA);
            cliente.getSalidaDatos().writeUTF(selectedItem);
            cliente.getSalidaDatos().writeUTF(selectedPlayer);
            cliente.getSalidaDatos().writeInt(precio);
        } catch (Exception e) {System.out.println("Error en tienda proponiendo venta");}
    }



    @FXML
    protected void btnAddEnergy(){
        // Primero hacemos una lista, despues en el boton de volver a la funcion llamamos ahi algo que actualice la lista
        cliente.getListaItems().add("Energia");
    }

    @FXML
    protected void btnAddStore(){
        // Primero hacemos una lista, despues en el boton de volver a la funcion llamamos ahi algo que actualice la lista
        cliente.getListaItems().add("Tienda");
    }

    @FXML
    protected void btnAddConector(){
        // Primero hacemos una lista, despues en el boton de volver a la funcion llamamos ahi algo que actualice la lista
        cliente.getListaItems().add("Conector");
    }

    private int getSellingPriceComponente(String item) {
        System.out.println(item);
        int number = switch (item) {
            case "Tienda" -> 100;
            case "Energia" -> 400;
            case "Conector" -> 500;
            default -> {
                System.out.println("Ítem no reconocido: " + item);
                yield -1;
            }
        };
        //TODO: los precios de ahorita son testing
        return number;
    }



}
