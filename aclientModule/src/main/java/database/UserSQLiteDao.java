package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class UserSQLiteDao implements UserDao, Closeable {

    private Connection connection;
    private PreparedStatement registrationStatement;
    private PreparedStatement userExistsStatement;
    private PreparedStatement findEmail;
    private static UserSQLiteDao instance;
    private String email;


    public UserSQLiteDao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:cloudstorage");
        try {
            userExistsStatement = connection.prepareStatement("SELECT NAME FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;");
            registrationStatement = connection.prepareStatement("INSERT INTO USERS(name, surname, login, password, email) VALUES(?, ?, ?, ?, ?);");
            findEmail = connection.prepareStatement("SELECT EMAIL FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
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
        findEmail.setString(1, login);
        findEmail.setString(2, password);
        ResultSet resultSet1 = findEmail.executeQuery();
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
    public void close() throws IOException {
        try {
            userExistsStatement.close();
            registrationStatement.close();
            findEmail.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}