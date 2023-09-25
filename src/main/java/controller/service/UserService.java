package controller.service;

import controller.service.api.IUserService;
import model.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class for CRUD operations with table "user" in database.
 */
public class UserService implements IUserService {

    /**
     * A database connection
     */
    private Connection connection;

    /**
     * A query for inserting user
     */
    private final String insertStatement = "INSERT INTO clever_bank.user VALUES (?, ?)";

    /**
     * A query for retrieving user from database based on id
     */
    private final String readStatement = "SELECT * FROM clever_bank.user WHERE id = ?";

    /**
     * A query for updating name of user in database based on id
     */
    private final String updateStatement = "UPDATE clever_bank.user SET name = ? WHERE id in (?)";

    /**
     * A query for deleting user from database based on id
     */
    private final String deleteStatement = "DELETE FROM clever_bank.user WHERE id = ?";

    /**
     * A constructor with parameter
     *
     * @param connection connection with database
     */
    public UserService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new user to the database. Prints message in case of errors
     *
     * @param id user id. Id less than or equal to zero will be used in query. Recommended for test purposes.
     *           Id bigger than zero will not be used in query and database will use self-generated value
     * @param name name of user. Cannot be null
     *
     * @return number of affected by query rows
     */
    @Override
    public int addUser(long id, String name) {
        PreparedStatement statement;
        int amountOfAffectedRows = 0;

        try {
            statement = connection.prepareStatement(insertStatement);
            statement.setLong(1, id <= 0 ? id : Long.parseLong("DEFAULT"));
            statement.setString(2, name);

            amountOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return amountOfAffectedRows;
    }

    /**
     * Reads user with specified id from database. Prints message in case of errors
     *
     * @param id user id
     *
     * @return instance of User class with specified id
     * or default values if user does not exist or SQLException occurred
     */
    @Override
    public User getUser(long id) {
        User user = new User();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            statement = connection.prepareStatement(readStatement);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                user = new User(resultSet.getLong("id"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return user;
    }

    /**
     * Updates user`s name in the database. Prints message in case of errors
     *
     * @param id user id.
     * @param name name of user. Cannot be null
     *
     * @return number of affected by query rows
     */
    @Override
    public int updateUser(long id, String name) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(updateStatement);
            statement.setString(1, name);
            statement.setLong(2, id);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Deletes user from the database. Prints message in case of errors
     *
     * @param id user id.
     *
     * @return number of affected by query rows
     */
    @Override
    public int deleteUser(long id) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(deleteStatement);
            statement.setLong(1, id);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }
}