package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public ListView<String> serverList;
    public TextField textField;
    public ListView<String> clientList;
    private final ObservableList<String> observableServerList = FXCollections.observableArrayList();
    private final ObservableList<String> observableClientList = FXCollections.observableArrayList();
    public TextField dirName;
    private String path;

    public void upload(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().sendFile(textField.getText(), "client", path);
        textField.clear();
        refreshLists();
    }

    public void delete(ActionEvent actionEvent) {
        NetworkService.getInstance().deleteFile(serverList.getSelectionModel().getSelectedItem(), path);
        refreshLists();
    }

    public void disconnect(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Platform.exit();
    }

    public void refreshLists() {
        clientList.setItems(NetworkService.getInstance().getDirectories("client"));
        serverList.setItems(NetworkService.getInstance().getDirectories(path));

    }

    public void uploadFromClientToServer(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile((String) clientList.getSelectionModel().getSelectedItem(), "client", path);
        refreshLists();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        observableClientList.addAll(NetworkService.getInstance().getFiles("client"));
        observableServerList.addAll(NetworkService.getInstance().getDirectories("server"));
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
        clientList.setItems(NetworkService.getInstance().getDirectories("client"));
        serverList.setItems(NetworkService.getInstance().getDirectories("server"));
        path = "server";
    }

    public void createDirectory(ActionEvent actionEvent) throws IOException {
        if(!Files.exists(Path.of(path, dirName.getText()))) {
            Files.createDirectories(Path.of(path, dirName.getText()));
            dirName.clear();
        }
        refreshLists();
    }
}

