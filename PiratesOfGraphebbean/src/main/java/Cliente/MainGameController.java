package Cliente;

import Cliente.Grafo.MapaDelMar;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGameController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String userName;
    private MapaDelMar mapaDelMar;

    public void setUserData(String _name){ // De una ves en esta funcion voy a crear el grafo
        this.userName = _name;
        mapaDelMar = new MapaDelMar(20);
    }
}
