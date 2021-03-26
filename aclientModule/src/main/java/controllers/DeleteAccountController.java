package controllers;

import database.UserSQLiteDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteAccountController {

    private final String login = UserSQLiteDao.getInstance().getLogin();
    public Label deleting;

    public DeleteAccountController() throws SQLException, ClassNotFoundException {
    }

    public void deleteAccount(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        UserSQLiteDao.getInstance().deleteAccount(login);
        deleting.getScene().getWindow().hide();
        Parent change = FXMLLoader.load(getClass().getResource("auth.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Авторизация");
        stage.setScene(new Scene(change));
        stage.setResizable(false);
        stage.show();
    }

    public void noDeleteAccount(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        Parent change = FXMLLoader.load(getClass().getResource("client.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Облачное хранилище данных       Аккаунт:  " + UserSQLiteDao.getInstance().getEmail());
        stage.setScene(new Scene(change));
        stage.setResizable(false);
        stage.show();
        deleting.getScene().getWindow().hide();
    }
}
