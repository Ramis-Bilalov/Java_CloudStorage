package controllers;

import database.UserSQLiteDao;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    public TextField login;
    public PasswordField password;
    TrashController trashController = new TrashController();

    public AuthController() throws SQLException, ClassNotFoundException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // Вход в аккаунт, если пароль и логин совпадают с данными в БД

    public void enter(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        boolean auth = false;
        auth = UserSQLiteDao.getInstance().userExists(login.getText(), password.getText());
        if (auth) {
            System.out.println("правильный пароль");
            Parent client = FXMLLoader.load(getClass().getResource("client.fxml"));
            Stage stage = new Stage();
            stage.setTitle("CloudStorage - Облачное хранилище данных       Аккаунт:  " + UserSQLiteDao.getInstance().getEmail());
            stage.setScene(new Scene(client));
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    trashController.closeConnection();
                }
            });
            login.getScene().getWindow().hide();
        } else {
            System.out.println("неправильный пароль");
            login.clear();
            login.setPromptText("WRONG LOGIN");
            password.clear();
            password.setPromptText("WRONG PASSWORD");
        }
    }

    // Вход в регистрацию

    public void register(ActionEvent actionEvent) throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Регистрация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();

        login.getScene().getWindow().hide();
    }
}