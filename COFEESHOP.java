package cofeeshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class COFEESHOP extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("skuwerongcha.png"));
        stage.getIcons().add(icon);
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("Ong Cha-À Cafe");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
