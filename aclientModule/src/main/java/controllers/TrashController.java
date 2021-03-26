package controllers;

import database.UserSQLiteDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TrashController implements Initializable {

    public ListView<String> trashList;
    public final ObservableList<String> observableTrashList = FXCollections.observableArrayList();
    String pathUsers = "aclientModule/src/main/resources/controllers/users";
    private final String pathNew = UserSQLiteDao.getInstance().getEmail();

    public TrashController() throws SQLException, ClassNotFoundException {
    }

    public void deleteFinally(ActionEvent actionEvent) {
        NetworkService.getInstance().deleteFile(trashList.getSelectionModel().getSelectedItem(), pathUsers + File.separator + pathNew + File.separator + "servertrash");
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
        trashList.setItems(NetworkService.getInstance().getDirectories(pathUsers + File.separator + pathNew + File.separator + "servertrash"));
    }
}