package Server;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SecondWindow {
    private String name;

    @FXML
    private Text nameText;

    public void setName(String _name){
        name = _name;
        nameText.setText(name);
    }
}
