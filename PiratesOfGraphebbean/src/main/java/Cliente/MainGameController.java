package Cliente;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGameController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String userName;

    public void setUserData(String _name){
        this.userName = _name;
    }
}
