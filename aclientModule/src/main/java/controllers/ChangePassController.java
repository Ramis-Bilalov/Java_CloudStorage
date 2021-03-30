package controllers;

import database.UserSQLiteDao;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ChangePassController {

    public TextField oldPass;
    public TextField newPass;
    public TextField repeatNewPass;
    private final String login = UserSQLiteDao.getInstance().getLogin();

    public ChangePassController() throws SQLException, ClassNotFoundException {
    }


// Изменение пароля действующего аккаунта

    public void changePassword(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(newPass.getText().equals(repeatNewPass.getText())) {
            UserSQLiteDao.getInstance().changePassword(login, oldPass.getText(), newPass.getText());
            oldPass.getScene().getWindow().hide();
        }
    }
}
