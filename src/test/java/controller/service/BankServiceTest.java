package controller.service;

import config.PostgreSQLConnector;
import config.api.IDatabaseConnector;
import model.entity.Bank;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Class for testing CRUD operations with banks */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTest {

    /** Database url */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /** Database user */
    private static String user = "postgres";

    /** Database password */
    private static String password = "password";

    /** Instance of database connection interface */
    private static IDatabaseConnector connector;

    /** Instance of the class implementing CRUD operations with banks in the database */
    private static BankService bankService;

    /** Name of bank that will be used in tests */
    private static final String TEST_NAME = "Test Bank";

    /** Name of bank that will be used in update test */
    private static final String NEW_NAME = " New Test Bank";

    /** Id of bank that will be used in tests */
    private static final long TEST_ID = -5;

    /** Creates instance of database connection and bank service before tests */
    @BeforeAll
    public static void setConnectorAndService() {
        connector = new PostgreSQLConnector(url, user, password);
        Connection connection = connector.getConnection();

        try {
            connection.setAutoCommit(false); // operations in this test have no effect on the database
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        bankService = new BankService(connection);
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

        bankService = null;
        connector = null;
    }

    /** Tests creating a new bank in the database */
    @Test
    @Order(1)
    public void addBankTest() {
        assertEquals(1, bankService.addBank(TEST_ID, TEST_NAME));
    }

    /** Tests receiving information about bank from the database */
    @Test
    @Order(2)
    public void getBankTest() {
        Bank expected = new Bank(TEST_ID, TEST_NAME);
        Bank actual = bankService.getBank(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    /** Tests updating information about bank in the database */
    @Test
    @Order(3)
    public void updateBankTest() {
        assertEquals(1, bankService.updateBank(TEST_ID, NEW_NAME));
        Bank updated = bankService.getBank(TEST_ID);
        assertEquals(NEW_NAME, updated.getName());
    }

    /** Tests deleting bank from the database */
    @Test
    @Order(4)
    public void deleteBankTest() {
        assertEquals(1, bankService.deleteBank(TEST_ID));
    }
}