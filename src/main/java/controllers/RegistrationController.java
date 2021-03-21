package controllers;

import Database.UserSQLiteDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class RegistrationController {

    public TextField name;
    public TextField surname;
    public TextField login;
    public TextField password;
    public TextField email;

    public String getName() {
        return name.getText();
    }

    public void setName(TextField name) {
        this.name = name;
    }

    public String getSurname() {
        return surname.getText();
    }

    public void setSurname(TextField surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email.getText();
    }

    public void setEmail(TextField email) {
        this.email = email;
    }

    public String getLogin() {
        return login.getText();
    }

    public void setLogin(TextField login) {
        this.login = login;
    }

    public String getPassword() {
        return password.getText();
    }

    public void setPassword(TextField password) {
        this.password = password;
    }

    public void registrate(ActionEvent actionEvent) throws IOException {
        try {
            if(!name.getText().equals("") && !surname.getText().equals("") && !login.getText().equals("") && !password.getText().equals("") && !email.getText().equals("")) {
                UserSQLiteDao.getInstance().registrateUser(name.getText(), surname.getText(), login.getText(), password.getText(), email.getText());
                Parent reg = FXMLLoader.load(getClass().getResource("auth.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Авторизация");
                stage.setScene(new Scene(reg));
                stage.setResizable(false);
                stage.show();
                login.getScene().getWindow().hide();
            }
        } catch (Exception e) {

        }
    }

    public void enter(ActionEvent actionEvent) throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("auth.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Авторизация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();
        login.getScene().getWindow().hide();
    }
}
