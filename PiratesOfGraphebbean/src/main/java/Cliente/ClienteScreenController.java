package Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClienteScreenController {

    @FXML
    private TextField txfNombre;

    @FXML
    private Button btnNombre;

    @FXML
    private Button btnIniciar;

    private Cliente cliente;

    @FXML
    public void initialize(){ //es como el constructor por decir asi
        cliente = new Cliente(this);
        cliente.run();
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



}
