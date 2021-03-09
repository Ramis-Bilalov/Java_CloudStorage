package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudStorageApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent auth = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("CloudStorage");
        primaryStage.setScene(new Scene(auth));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
