package controller.service;

import config.PostgreSQLConnector;
import config.api.IDatabaseConnector;
import model.entity.Transaction;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** Class for testing CRUD operations with transactions */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    /** Instance of database connection interface */
    private static IDatabaseConnector connector;

    /** Instance of the class implementing CRUD operations with transactions in the database */
    private static TransactionService transactionService;

    /** Id that will be used in tests */
    private static final long TEST_ID = -1;

    /** Amount that will be used in tests */
    private static final double TEST_AMOUNT = 5.00;

    /** Value that will be used in updating amount information */
    private static final double TEST_AMOUNT_UPDATE = 1.00;

    /** Creates instance of database connection and user service before tests */
    @BeforeAll
    public static void setConnectorAndService() {
        connector = new PostgreSQLConnector(url, user, password);
        Connection connection = connector.getConnection();

        try {
            connection.setAutoCommit(false); // operations in this test have no effect on the database
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        transactionService = new TransactionService(connection);
    }

    /** Closes database connection */
    @AfterAll
    public static void tearDown() {
        try {
            connector.getConnection().rollback();
            connector.getConnection().setAutoCommit(true);
            connector.closeConnection();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        transactionService = null;
        connector = null;
    }

    /** Tests creating a new transaction in the database */
    @Test
    @Order(1)
    public void addTransactionTest() {
        assertEquals(TEST_ID, transactionService.addTransaction(TEST_ID, TEST_AMOUNT, TEST_ID, TEST_ID * 2));
    }

    /** Tests receiving information about transaction from the database. Not checking time of transaction */
    @Test
    @Order(2)
    public void getTransactionTest() {
        Transaction expected = new Transaction(TEST_ID, TEST_AMOUNT, LocalDateTime.MIN, TEST_ID, TEST_ID * 2);
        Transaction actual = transactionService.getTransaction(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getSender(), actual.getSender());
        assertEquals(expected.getReceiver(), actual.getReceiver());
    }

    /** Tests updating information about transaction in the database */
    @Test
    @Order(3)
    public void updateTransactionTest() {
        assertEquals(1, transactionService.updateTransaction(TEST_ID, TEST_AMOUNT + TEST_AMOUNT_UPDATE,
                TEST_ID, TEST_ID * 2, LocalDateTime.now()));

        Transaction actual = transactionService.getTransaction(TEST_ID);
        assertEquals(TEST_AMOUNT + TEST_AMOUNT_UPDATE, actual.getAmount());
        assertNotEquals(actual.getTime(), LocalDateTime.MIN);
    }

    /** Tests deleting transaction from the database */
    @Test
    @Order(4)
    public void deleteTransactionTest() {
        assertEquals(1, transactionService.deleteTransaction(TEST_ID));
    }
}