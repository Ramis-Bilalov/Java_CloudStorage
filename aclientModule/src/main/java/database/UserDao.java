package database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public interface UserDao {

    boolean userExists(String login, String password) throws SQLException;

    void registrateUser(String name, String surname, String login, String password, String email) throws SQLException;

    void changePassword(String login, String oldPassword, String newPassword) throws SQLException;

    void deleteAccount(String login) throws SQLException;

}
