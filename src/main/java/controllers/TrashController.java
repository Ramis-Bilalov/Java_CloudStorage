package controllers;

import client.NetworkService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.scene.control.ListView;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TrashController implements Initializable {

    public ListView<String> trashList;
    public final ObservableList<String> observableTrashList = FXCollections.observableArrayList();

    public void deleteFinally(ActionEvent actionEvent) {
        NetworkService.getInstance().deleteFile(trashList.getSelectionModel().getSelectedItem(), "servertrash");
        refreshLists();
    }

    public void goBack(ActionEvent actionEvent) throws IOException {
        trashList.getScene().getWindow().hide();
    }

    public void refresh() {
        refreshLists();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshLists();
    }

    public void closeConnection() {
        Platform.exit();
    }

    private void refreshLists() {
        trashList.setItems(NetworkService.getInstance().getDirectories("servertrash"));
    }
}
