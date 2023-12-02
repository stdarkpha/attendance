package project.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class AdminListUserController {
    private final MainApp mainApp;

    @FXML
    private AnchorPane BaseContainer;

    public AdminListUserController(MainApp mainApp) {
        this.mainApp = mainApp;
        loadFXML();
    }

    public void showScene() {
        mainApp.getPrimaryStage().setScene(new Scene(BaseContainer));
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            loader.setController(this);
            BaseContainer = loader.load();
        } catch (Exception e) {
            System.out.println("Tidak dapat memuat scene");
        }
    }

    @FXML
    private void initialize() {

    }
}
