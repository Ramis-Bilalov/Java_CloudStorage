package Database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;

public class UserSQLiteDao implements UserDao, Closeable {

    private Connection connection;
    private PreparedStatement registrationStatement;
    private PreparedStatement userExistsStatement;
    private static UserSQLiteDao instance;


    public UserSQLiteDao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:cloudstorage");
        try {
            userExistsStatement = connection.prepareStatement("SELECT NAME FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;");
            registrationStatement = connection.prepareStatement("INSERT INTO USERS(name, surname, login, password, email) VALUES(?, ?, ?, ?, ?);");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
