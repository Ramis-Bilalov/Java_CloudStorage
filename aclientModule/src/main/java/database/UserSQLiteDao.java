package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class UserSQLiteDao implements UserDao, Closeable {

    private Connection connection;
    private PreparedStatement registrationStatement;
    private PreparedStatement userExistsStatement;
    private PreparedStatement findEmailStatement;
    private PreparedStatement changePassStatement;
    private PreparedStatement deleteAccStatement;
    private static UserSQLiteDao instance;
    private String email;
    private String login;


    public UserSQLiteDao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:cloudstorage");
        try {
            userExistsStatement = connection.prepareStatement("SELECT NAME FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;");
            registrationStatement = connection.prepareStatement("INSERT INTO USERS(name, surname, login, password, email) VALUES(?, ?, ?, ?, ?);");
            findEmailStatement = connection.prepareStatement("SELECT EMAIL FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;");
            changePassStatement = connection.prepareStatement("UPDATE USERS SET PASSWORD = ? WHERE LOGIN = ? AND PASSWORD = ?;");
            deleteAccStatement = connection.prepareStatement("DELETE FROM USERS WHERE LOGIN = ?;");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public static UserSQLiteDao getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new UserSQLiteDao();
        }
        return instance;
    }

    @Override
    public boolean userExists(String login, String password) throws SQLException {
        userExistsStatement.setString(1, login);
        userExistsStatement.setString(2, password);
        findEmailStatement.setString(1, login);
        findEmailStatement.setString(2, password);
        ResultSet resultSet1 = findEmailStatement.executeQuery();
        this.login = login;
        email = resultSet1.getString(1);
        ResultSet resultSet = userExistsStatement.executeQuery();
        return resultSet.next();
    }

    @Override
    public void registrateUser(String name, String surname, String login, String password, String email) throws SQLException {
        registrationStatement.setString(1, name);
        registrationStatement.setString(2, surname);
        registrationStatement.setString(3, login);
        registrationStatement.setString(4, password);
        registrationStatement.setString(5, email);
        registrationStatement.execute();
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws SQLException {
        changePassStatement.setString(1, newPassword);
        changePassStatement.setString(2, login);
        changePassStatement.setString(3, oldPassword);
        changePassStatement.execute();
    }

    @Override
    public void deleteAccount(String login) throws SQLException {
        deleteAccStatement.setString(1, login);
        deleteAccStatement.execute();
    }

    @Override
    public void close() throws IOException {
        try {
            userExistsStatement.close();
            registrationStatement.close();
            findEmailStatement.close();
            changePassStatement.close();
            deleteAccStatement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}