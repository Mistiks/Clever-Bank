package controller.service;

import model.entity.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class for CRUD operations with table "account" in database
 *
 * @author Vadim Rataiko
 */
public class AccountService {

    /**
     * A database connection
     */
    private final Connection connection;

    /**
     * A query for inserting account
     */
    private final String insertStatement = "INSERT INTO clever_bank.account VALUES (?, ?, ?, ?)";

    /**
     * A query for retrieving account from database based on id
     */
    private final String readStatement = "SELECT * FROM clever_bank.account WHERE id = ?";

    /**
     * A query for updating account in database based on id
     */
    private final String updateStatement = "UPDATE clever_bank.account SET bank_id = ?, balance = ?, user_id = ? WHERE id = ?";

    /**
     * A query for deleting account from database based on id
     */
    private final String deleteStatement = "DELETE FROM clever_bank.account WHERE id = ?";

    /**
     * A constructor with parameter
     *
     * @param connection connection with database
     */
    public AccountService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new account to the database. Prints message in case of errors
     *
     * @param id account id. Id less than or equal to zero will be used in query. Recommended for test purposes.
     *           Id bigger than zero will not be used in query and database will use self-generated value
     * @param bankId id of bank. Cannot be null
     * @param balance account balance
     * @param userId id of account owner
     *
     * @return number of affected by query rows
     */
    public int addAccount(long id, long bankId, double balance, long userId) {
        PreparedStatement statement;
        int numberOfAffectedRows = 0;

        try {
            statement = connection.prepareStatement(insertStatement);
            statement.setLong(1, id <= 0 ? id : Long.parseLong("DEFAULT"));
            statement.setLong(2, bankId);
            statement.setDouble(3, balance);
            statement.setLong(4, userId);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Reads account with specified id from database. Prints message in case of errors
     *
     * @param id account id
     *
     * @return instance of Account class with specified id
     * or default values if account does not exist or SQLException occurred
     */
    public Account getAccount(long id) {
        Account account = new Account();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            statement = connection.prepareStatement(readStatement);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                account = new Account(resultSet.getLong("id"), resultSet.getLong("bank_id"),
                        resultSet.getDouble("balance"), resultSet.getLong("user_id"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return account;
    }

    /**
     * Updates account`s info in the database. Prints message in case of errors
     *
     * @param id account id
     * @param bankId id of bank. Cannot be null
     * @param balance account balance. Cannot be null
     * @param userId id of account owner. Cannot be null
     *
     * @return number of affected by query rows
     */
    public int updateAccount(long id, long bankId, double balance, long userId) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(updateStatement);
            statement.setLong(1, bankId);
            statement.setDouble(2, balance);
            statement.setLong(3, userId);
            statement.setLong(4, id);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Deletes account from the database. Prints message in case of errors
     *
     * @param id account id.
     *
     * @return number of affected by query rows
     */
    public int deleteAccount(long id) {
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