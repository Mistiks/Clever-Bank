package controller.service;

import controller.service.api.ITransactionService;
import model.dto.StatementDto;
import model.entity.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** A class for CRUD operations with table "transaction" in database */
public class TransactionService implements ITransactionService {

    /** A database connection */
    private final Connection connection;

    /** A query for inserting transaction */
    private final String insertStatement = "INSERT INTO clever_bank.transaction VALUES (?, ?, ?, ?, ?) RETURNING id";

    /** A query for inserting transaction with autogenerated id */
    private final String insertStatementWithoutId = "INSERT INTO clever_bank.transaction (amount, time, sender_id, receiver_id) " +
            "VALUES (?, ?, ?, ?) RETURNING id";

    /** A query for retrieving transaction from database based on id */
    private final String readStatement = "SELECT * FROM clever_bank.transaction WHERE id = ?";

    /** A query for updating transaction in database based on id */
    private final String updateStatement = "UPDATE clever_bank.transaction SET amount = ?, sender_id = ?, " +
            "receiver_id = ?, time = ? WHERE id = ?";

    /** A query for deleting transaction from database based on id */
    private final String deleteStatement = "DELETE FROM clever_bank.transaction WHERE id = ?";

    /** A query for retrieving information about all account transactions */
    private final String readAllTransactions =
            """
            SELECT transaction.time, COALESCE(sender.name, '') AS sender, COALESCE(receiver.name, '') AS receiver, transaction.amount
            FROM clever_bank.transaction
            LEFT JOIN clever_bank.user AS sender on transaction.sender_id = sender.id
            LEFT JOIN clever_bank.user AS receiver on transaction.receiver_id = receiver.id
            WHERE sender.id = ? OR receiver.id = ?
            """;

    /** A query for retrieving information about all account transactions during time interval */
    private final String readAllTransactionsByTime =
            """
            SELECT transaction.time, COALESCE(sender.name, '') AS sender, COALESCE(receiver.name, '') AS receiver, transaction.amount
            FROM clever_bank.transaction
            LEFT JOIN clever_bank.user AS sender on transaction.sender_id = sender.id
            LEFT JOIN clever_bank.user AS receiver on transaction.receiver_id = receiver.id
            WHERE (sender.id = ? OR receiver.id = ?)
            AND (transaction.time BETWEEN ? AND 'NOW')
            """;

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
     * @param id transaction id. Id less than zero will be used in query. Recommended for test purposes.
     *           Id bigger than zero will not be used in query and database will use self-generated value
     * @param amount transaction amount
     * @param senderId id of transaction sender. Zero in case of account replenishment
     * @param receiverId id of transaction receiver. Zero in case of money withdrawal
     *
     * @return id of created transaction or zero in case of database errors
     */
    @Override
    public long addTransaction(long id, double amount, long senderId, long receiverId) {
        PreparedStatement statement;
        ResultSet resultSet;
        long transactionId = 0;

        try {
            if (id < 0) {
                statement = connection.prepareStatement(insertStatement);
                statement.setLong(1, id);
                statement.setDouble(2, amount);
                statement.setObject(3, LocalDateTime.now());
                statement.setLong(4, senderId);
                statement.setLong(5, receiverId);
            } else {
                statement = connection.prepareStatement(insertStatementWithoutId);
                statement.setDouble(1, amount);
                statement.setObject(2, LocalDateTime.now());
                statement.setLong(3, senderId);
                statement.setLong(4, receiverId);
            }

            synchronized (connection) {
                resultSet = statement.executeQuery();
            }

            while (resultSet.next()) {
                transactionId = resultSet.getLong("id");
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return transactionId;
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

            synchronized (connection) {
                resultSet = statement.executeQuery();
            }

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
     * Retrieves all transactions with specified account for transaction statement.
     * Prints message in case of errors
     *
     * @param id account id
     *
     * @return list with all transactions with specified account
     */
    @Override
    public List<StatementDto> getTransactionList(long id) {
        StatementDto currentStatement;
        List<StatementDto> transactions = new ArrayList<>();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            synchronized (connection) {
                statement = connection.prepareStatement(readAllTransactions);
                statement.setLong(1, id);
                statement.setLong(2, id);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    currentStatement = new StatementDto(resultSet.getObject("time", LocalDateTime.class),
                            resultSet.getString("sender"), resultSet.getString("receiver"),
                            resultSet.getDouble("amount"));

                    transactions.add(currentStatement);
                }
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return transactions;
    }

    /**
     * Retrieves all transactions with specified account for transaction statement starting from specified date.
     * Prints message in case of errors
     *
     * @param id account id
     * @param intervalStart start of statement period
     *
     * @return list with all transactions with specified account during specified time period
     */
    @Override
    public List<StatementDto> getTransactionListByTime(long id, LocalDateTime intervalStart) {
        StatementDto currentStatement;
        List<StatementDto> transactions = new ArrayList<>();
        PreparedStatement statement;
        ResultSet resultSet;

        try {
            synchronized (connection) {
                statement = connection.prepareStatement(readAllTransactionsByTime);
                statement.setLong(1, id);
                statement.setLong(2, id);
                statement.setObject(3, intervalStart);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    currentStatement = new StatementDto(resultSet.getObject("time", LocalDateTime.class),
                            resultSet.getString("sender"), resultSet.getString("receiver"),
                            resultSet.getDouble("amount"));

                    transactions.add(currentStatement);
                }
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return transactions;
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

            synchronized (connection) {
                numberOfAffectedRows = statement.executeUpdate();
            }
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

            synchronized (connection) {
                numberOfAffectedRows = statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return numberOfAffectedRows;
    }
}