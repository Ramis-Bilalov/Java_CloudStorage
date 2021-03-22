package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public ListView<String> serverList;
    public TextField uploadFile;
    public ListView<String> clientList;
    public final ObservableList<String> observableServerList = FXCollections.observableArrayList();
    public final ObservableList<String> observableClientList = FXCollections.observableArrayList();
    public TextField dirName;
    public String path;
    public TextField searchServer;
    public TextField searchClient;
    Stage trashStage = new Stage();


    public void upload(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().sendFile(uploadFile.getText(), "client", path);
        uploadFile.clear();
        refreshLists();
    }

    public void delete(ActionEvent actionEvent) throws IOException, InterruptedException {
        NetworkService.getInstance().sendFile(serverList.getSelectionModel().getSelectedItem(), path, "servertrash");
        NetworkService.getInstance().deleteFile(serverList.getSelectionModel().getSelectedItem(), path);
        refreshLists();
        if(trashStage.isShowing()) {
            toTrash();
        }
    }

    public void disconnect(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Parent reg = FXMLLoader.load(getClass().getResource("auth.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Авторизация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();
        dirName.getScene().getWindow().hide();
        this.trashStage.close();
    }

    public void refreshLists() {
        try {
            clientList.setItems(NetworkService.getInstance().getDirectories("client"));
            serverList.setItems(NetworkService.getInstance().getDirectories(path));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void uploadFromClientToServer(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile((String) clientList.getSelectionModel().getSelectedItem(), "client", path);
        refreshLists();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Files.exists(Path.of("client"))) {
            try {
                Files.createDirectories(Path.of("client"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(Path.of("server"))) {
            try {
                Files.createDirectories(Path.of("server"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(Path.of("desktop"))) {
            try {
                Files.createDirectories(Path.of("desktop"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!Files.exists(Path.of("servertrash"))) {
            try {
                Files.createDirectories(Path.of("servertrash"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            observableClientList.addAll(NetworkService.getInstance().getFiles("client"));
            observableServerList.addAll(NetworkService.getInstance().getDirectories("server"));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        path = "server";
        serverList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                try {
                    String dir = serverList.getSelectionModel().getSelectedItem();
                    if (event.getClickCount() == 2) {
                        if (serverList.getSelectionModel().getSelectedItem().equals(dir)) {
                            serverList.setItems(NetworkService.getInstance().getDirectories(String.valueOf(Path.of(path, dir))));
                            path = String.valueOf(Path.of(path, dir));
                        }
                    }
                } catch (NullPointerException n) {
                    System.out.println("Not a directory");
                }
            }
        });
        refreshLists();

    }

    public void uploadFromServerToDesktop(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile(serverList.getSelectionModel().getSelectedItem(), path, "desktop");
        refreshLists();
    }

    public void returnFromDirectory(ActionEvent actionEvent) {
        try {
            clientList.setItems(NetworkService.getInstance().getDirectories("client"));
            serverList.setItems(NetworkService.getInstance().getDirectories("server"));
            path = "server";
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void createDirectory(ActionEvent actionEvent) throws IOException {
        if(!Files.exists(Path.of(path, dirName.getText()))) {
            Files.createDirectories(Path.of(path, dirName.getText()));
            dirName.clear();
        }
        refreshLists();
    }

    public void toTrash() throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("trash.fxml"));
        trashStage.setTitle("CloudStorage - Корзина");
        trashStage.setX(1100);
        trashStage.setY(96);
        trashStage.setScene(new Scene(reg));
        trashStage.setResizable(false);
        trashStage.show();
    }

    public void closeConnection(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Platform.exit();
    }

    public void registrate(ActionEvent actionEvent) throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Регистрация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();
        dirName.getScene().getWindow().hide();
    }
}