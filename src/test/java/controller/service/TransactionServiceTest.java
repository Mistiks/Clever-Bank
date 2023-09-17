package controller.service;

import config.PostgreSQLConnector;
import model.entity.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class for testing CRUD operations with transactions
 *
 * @author Vadim Rataiko
 */
public class TransactionServiceTest {

    /**
     * Database url
     */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /**
     * Database user
     */
    private static String user = "postgres";

    /**
     * Database password
     */
    private static String password = "password";

    /**
     * Instance of database connection class
     */
    private static PostgreSQLConnector connector;

    /**
     * Instance of the class implementing CRUD operations with transactions in the database
     */
    private static TransactionService transactionService;

    /**
     * Id that will be used in tests
     */
    private static final long TEST_ID = -5;

    /**
     * Amount that will be used in tests
     */
    private static final double TEST_AMOUNT = 5.00;

    /**
     * Value that will be used in updating amount information
     */
    private static final double TEST_AMOUNT_UPDATE = 1.00;

    /**
     * Creates instance of database connection class before every test
     */
    @BeforeAll
    public static void setConnector() {
        connector = new PostgreSQLConnector(url, user, password);
        transactionService = new TransactionService(connector.getConnection());
    }

    /**
     * Tests creating a new transaction in the database
     */
    @Test
    public void addAccountTest() {
        assertEquals(1, transactionService.addTransaction(TEST_ID, TEST_AMOUNT, TEST_ID, TEST_ID * 2));
    }

    /**
     * Tests receiving information about transaction from the database. Not checking time of transaction
     */
    @Test
    public void getTransactionTest() {
        Transaction expected = new Transaction(TEST_ID, TEST_AMOUNT, LocalDateTime.MIN, TEST_ID, TEST_ID * 2);
        Transaction actual = transactionService.getTransaction(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getSender(), actual.getSender());
        assertEquals(expected.getReceiver(), actual.getReceiver());
    }

    /**
     * Tests updating information about transaction in the database
     */
    @Test
    public void updateTransactionTest() {
        assertEquals(1, transactionService.updateTransaction(TEST_ID, TEST_AMOUNT + TEST_AMOUNT_UPDATE, TEST_ID, TEST_ID * 2));

        Transaction actual = transactionService.getTransaction(TEST_ID);
        assertEquals(TEST_AMOUNT + TEST_AMOUNT_UPDATE, actual.getAmount());
    }

    /**
     * Tests deleting transaction from the database
     */
    @Test
    public void deleteTransactionTest() {
        assertEquals(1, transactionService.deleteTransaction(TEST_ID));
    }

    /**
     * Closes database connection
     */
    @AfterAll
    static void closeConnection() {
        connector.closeConnection();
    }
}