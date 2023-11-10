package controller.service;

import controller.service.api.IBankService;
import model.entity.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class for CRUD operations with table "bank" in database.
 *
 * @author Vadim Rataiko
 */
public class BankService implements IBankService {

    /**
     * A database connection
     */
    private final Connection connection;

    /**
     * A query for inserting bank without autogenerated id
     */
    private final String insertStatement = "INSERT INTO clever_bank.bank VALUES (?, ?)";

    /**
     * A query for inserting bank with autogenerated id
     */
    private final String insertStatementWithoutId = "INSERT INTO clever_bank.bank (name) VALUES (?)";

    /**
     * A query for retrieving bank from database based on id
     */
    private final String readStatement = "SELECT * FROM clever_bank.bank WHERE id = ?";

    /**
     * A query for updating name of bank in database based on id
     */
    private final String updateStatement = "UPDATE clever_bank.bank SET name = ? WHERE id in (?)";

    /**
     * A query for deleting bank from database based on id
     */
    private final String deleteStatement = "DELETE FROM clever_bank.bank WHERE id = ?";

    /**
     * A constructor with parameter
     *
     * @param connection connection with database
     */
    public BankService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new bank to the database. Prints message in case of errors
     *
     * @param id bank id. Id less than or equal to zero will be used in query. Recommended for test purposes.
     *           Id bigger than zero will not be used in query and database will use self-generated value
     * @param name name of bank. Cannot be null
     *
     * @return number of affected by query rows
     */
    public int addBank(long id, String name) {
        PreparedStatement statement;
        int numberOfAffectedRows = 0;

        try {
            if (id <= 0) {
                statement = connection.prepareStatement(insertStatement);
                statement.setLong(1, id);
                statement.setString(2, name);
            } else {
                statement = connection.prepareStatement(insertStatementWithoutId);
                statement.setString(1, name);
            }

            synchronized (connection) {
                numberOfAffectedRows = statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Reads bank with specified id from database. Prints message in case of errors
     *
     * @param id bank id
     *
     * @return instance of Bank class with specified id
     * or default values if bank does not exist or SQLException occurred
     */
    public Bank getBank(long id) {
        Bank bank = new Bank();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            statement = connection.prepareStatement(readStatement);
            statement.setLong(1, id);

            synchronized (connection) {
                resultSet = statement.executeQuery();
            }

            while (resultSet.next()) {
                bank = new Bank(resultSet.getLong("id"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return bank;
    }

    /**
     * Updates bank`s name in the database. Prints message in case of errors
     *
     * @param id bank id.
     * @param name name of bank. Cannot be null
     *
     * @return number of affected by query rows
     */
    public int updateBank(long id, String name) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(updateStatement);
            statement.setString(1, name);
            statement.setLong(2, id);

            synchronized (connection) {
                numberOfAffectedRows = statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Deletes bank from the database. Prints message in case of errors
     *
     * @param id bank id.
     *
     * @return number of affected by query rows
     */
    public int deleteBank(long id) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(deleteStatement);
            statement.setLong(1, id);

            synchronized (connection) {
                numberOfAffectedRows = statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }
}