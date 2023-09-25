package controller.service;

import controller.service.api.ITransactionService;
import model.entity.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * A class for CRUD operations with table "transaction" in database
 *
 * @author Vadim Rataiko
 */
public class TransactionService implements ITransactionService {

    /**
     * A database connection
     */
    private Connection connection;

    /**
     * A query for inserting transaction
     */
    private final String insertStatement = "INSERT INTO clever_bank.transaction VALUES (?, ?, ?, ?, ?)";

    /**
     * A query for retrieving transaction from database based on id
     */
    private final String readStatement = "SELECT * FROM clever_bank.transaction WHERE id = ?";

    /**
     * A query for updating transaction in database based on id
     */
    private final String updateStatement = "UPDATE clever_bank.transaction SET amount = ?, sender_id = ?, " +
            "receiver_id = ?, time = ? WHERE id = ?";

    /** A query for deleting transaction from database based on id */
    private final String deleteStatement = "DELETE FROM clever_bank.transaction WHERE id = ?";

    /**
     * A constructor with parameter
     *
     * @param connection connection with database
     */
    public TransactionService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new transaction to the database. Time of transaction added automatically. Prints message in case of errors
     *
     * @param id transaction id. Id less than or equal to zero will be used in query. Recommended for test purposes.
     *           Id bigger than zero will not be used in query and database will use self-generated value
     * @param amount transaction amount
     * @param senderId id of transaction sender. Zero in case of account replenishment
     * @param receiverId id of transaction receiver. Zero in case of money withdrawal
     *
     * @return number of affected by query rows
     */
    @Override
    public int addTransaction(long id, double amount, long senderId, long receiverId) {
        PreparedStatement statement;
        int numberOfAffectedRows = 0;

        try {
            statement = connection.prepareStatement(insertStatement);
            statement.setLong(1, id <= 0 ? id : Long.parseLong("DEFAULT"));
            statement.setDouble(2, amount);
            statement.setObject(3, LocalDateTime.now());
            statement.setLong(4, senderId);
            statement.setLong(5, receiverId);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Reads transaction with specified id from database. Prints message in case of errors
     *
     * @param id transaction id
     *
     * @return instance of Transaction class with specified id
     * or default values if account does not exist or SQLException occurred
     */
    @Override
    public Transaction getTransaction(long id) {
        Transaction transaction = new Transaction();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            statement = connection.prepareStatement(readStatement);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                transaction = new Transaction(resultSet.getLong("id"), resultSet.getDouble("amount"),
                        resultSet.getObject("time", LocalDateTime.class),
                        resultSet.getLong("sender_id"), resultSet.getLong("receiver_id"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return transaction;
    }

    /**
     * Updates transaction`s info in the database. Prints message in case of errors
     *
     * @param id transaction id
     * @param amount transaction amount
     * @param senderId id of transaction sender. Zero in case of account replenishment
     * @param receiverId id of transaction receiver. Zero in case of money withdrawal
     * @param time new time of transaction
     *
     * @return number of affected by query rows
     */
    @Override
    public int updateTransaction(long id, double amount, long senderId, long receiverId, LocalDateTime time) {
        int numberOfAffectedRows = 0;
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(updateStatement);
            statement.setDouble(1, amount);
            statement.setLong(2, senderId);
            statement.setLong(3, receiverId);
            statement.setObject(4, time);
            statement.setLong(5, id);

            numberOfAffectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }

    /**
     * Deletes transaction from the database. Prints message in case of errors
     *
     * @param id transaction id
     *
     * @return number of affected by query rows
     */
    @Override
    public int deleteTransaction(long id) {
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