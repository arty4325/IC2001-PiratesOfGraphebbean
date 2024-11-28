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

    @FXML
    private Label lblAcero;

    @FXML
    private ComboBox<String> cbxJugadores2;

    @FXML
    private Spinner<Integer> sbxPrecioAcero;

    @FXML
    private Spinner<Integer> sbxCantAcero;


    public void setGameController(Cliente _cliente, MapaDelMar mapaDelMar){ // De una ves en esta funcion voy a crear el grafo
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        this.cliente = _cliente;
        this.mapaDelMar = mapaDelMar;
        actualizarComponentesCbx();
        setCbxJugadores();
        setSpinners();
        actualizarDinero();
        actualizarAcero();
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

    public void actualizarAcero(){
        lblAcero.setText("" + cliente.getAcero());
    }

    private void setCbxJugadores(){
        for (String nombreOponente : cliente.getNombresOponentes()) {
            cbxJugadores.getItems().add(nombreOponente);
            cbxJugadores2.getItems().add(nombreOponente);
        }

    }
    private void setSpinners(){
        sbxPrecioComponente.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 100, 100));
        sbxPrecioAcero.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 100, 100));
        sbxCantAcero.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 10000, 50, 50));
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
    protected void onBtnVenderAceroClick(){
        String selectedPlayer = cbxJugadores2.getValue();
        int precio = sbxPrecioAcero.getValue();
        int cant = sbxCantAcero.getValue();
        if(selectedPlayer == null || cliente.getAcero() < cant){return;}
        try {
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.PROPONERVENTAACERO);
            cliente.getSalidaDatos().writeUTF(selectedPlayer);
            cliente.getSalidaDatos().writeInt(cant);
            cliente.getSalidaDatos().writeInt(precio);
        } catch (Exception e) {System.out.println("Error en tienda proponiendo venta");}
    }


    @FXML
    protected void btnAddEnergy(){
        int precio = getPriceComponente("Energia");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Energia");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddStore(){
        int precio = getPriceComponente("Tienda");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Tienda");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddConector(){
        int precio = getPriceComponente("Conector");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Conector");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddTemplo(){
        int precio = getPriceComponente("Templo");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Templo");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddMina(){
        int precio = getPriceComponente("Mina");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Mina");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddArmeria(){
        int precio = getPriceComponente("Armeria");
        if(cliente.tengoDineroSuficiente(precio)){
            cliente.bajarDinero(precio);
            cliente.getListaItems().add("Armeria");
            actualizarDinero();
        }
    }

    @FXML
    protected void btnAddCanon(){
        if(!tieneArmeria()){return;}
        int precio = 500;
        if(cliente.tengoAceroSuficiente(precio)){
            cliente.bajarAcero(precio);
            cliente.comprarCanon();
            actualizarAcero();
        }
    }

    @FXML
    protected void btnAddCanonMult(){
        if(!tieneArmeria()){return;}
        int precio = 1000;
        if(cliente.tengoAceroSuficiente(precio)){
            cliente.bajarAcero(precio);
            cliente.comprarCanonMult();
            actualizarAcero();
        }
    }

    @FXML
    protected void btnAddBomba(){
        if(!tieneArmeria()){return;}
        int precio = 2000;
        if(cliente.tengoAceroSuficiente(precio)){
            cliente.bajarAcero(precio);
            cliente.comprarBomba();
            actualizarAcero();
        }
    }

    @FXML
    protected void btnAddCanonBR(){
        if(!tieneArmeria()){return;}
        int precio = 5000;
        if(cliente.tengoAceroSuficiente(precio)){
            cliente.bajarAcero(precio);
            cliente.comprarCanonBR();
            actualizarAcero();
        }
    }

    private boolean tieneArmeria(){
        return mapaDelMar.getItemsInScreen().contains("Armeria");
        //TODO: DESCMENTAR LO DE ARRIBA Y  QUITAR LOD E ABAJO
//        return true;
    }

    private int getSellingPriceComponente(String item) {
        System.out.println(item);
        int number = switch (item) {
            case "Energia" -> 6000;
            case "Mina" -> 500;
            case "Templo" -> 1500;
            case "Tienda" -> 1000;
            case "Conector" -> 50;
            case "Armeria" -> 1000;
            default -> {
                System.out.println("Ítem no reconocido: " + item);
                yield -1;
            }
        };
        return number;
    }

    private int getPriceComponente(String item){
        System.out.println(item);
        int number = switch (item) {
            case "Energia" -> 12000;
            case "Mina" -> 1000;
            case "Templo" -> 3000;
            case "Tienda" -> 2000;
            case "Conector" -> 100;
            case "Armeria" -> 2000;
            default -> {
                System.out.println("Ítem no reconocido: " + item);
                yield -1;
            }
        };
        return number;
    }



}
