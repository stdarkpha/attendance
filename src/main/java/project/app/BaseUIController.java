package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;


public class BaseUIController {
    private final MainApp mainApp;
    private Parent root;

    public BaseUIController(MainApp mainApp) {
        this.mainApp = mainApp;
        loadFXML();
    }

    public void showScene() {
        if (root != null) {
            mainApp.setRoot(root);
        } else {
            System.out.println("Root is null");
        }
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("list-user-view.fxml"));
            loader.setController(this);
            root = loader.load();

        } catch (IOException e) {
            System.out.println("Failed to load FXML file: admin-view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {

    }
}
