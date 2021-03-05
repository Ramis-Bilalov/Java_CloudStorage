package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public ListView<String> serverList;
    public TextField textField;
    public ListView<String> clientList;
    private final ObservableList<String> observableServerList = FXCollections.observableArrayList();
    private final ObservableList<String> observableClientList = FXCollections.observableArrayList();

    public void upload(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().sendFile(textField.getText(), "client", "server", "upload");
        textField.clear();
        refreshLists();
    }

    public void delete(ActionEvent actionEvent) {
        NetworkService.getInstance().deleteFile(serverList.getSelectionModel().getSelectedItem(), "server");
        refreshLists();
    }

    public void disconnect(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Platform.exit();
    }

    public void refreshLists() {
        clientList.setItems(NetworkService.getInstance().getFiles("client"));
        serverList.setItems(NetworkService.getInstance().getFiles("server"));
    }

    public void uploadFromClientToServer(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile((String) clientList.getSelectionModel().getSelectedItem(), "client", "server", "upload");
        refreshLists();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        observableClientList.addAll(NetworkService.getInstance().getFiles("client"));
        refreshLists();
    }

    public void uploadFromServerToDesktop(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile(serverList.getSelectionModel().getSelectedItem(), "server", "desktop", "download");
        refreshLists();
    }
}

