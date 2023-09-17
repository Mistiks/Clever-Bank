package controller.service;

import config.PostgreSQLConnector;
import controller.service.BankService;
import model.entity.Bank;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class for testing CRUD operations with banks
 *
 * @author Vadim Rataiko
 */
public class BankServiceTest {

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
     * Instance of the class implementing CRUD operations with banks in the database
     */
    private static BankService bankService;

    /**
     * Name of bank that will be used in tests
     */
    private static final String TEST_NAME = "Test Bank";

    /**
     * Id of bank that will be used in tests
     */
    private static final long TEST_ID = -5;

    /**
     * Creates instance of database connection class before every test
     */
    @BeforeAll
    public static void setConnector() {
        connector = new PostgreSQLConnector(url, user, password);
        bankService = new BankService(connector.getConnection());
    }

    /**
     * Tests creating a new bank in the database
     */
    @Test
    public void addBankTest() {
        assertEquals(1, bankService.addBank(TEST_ID, TEST_NAME));
    }

    /**
     * Tests receiving information about bank from the database
     */
    @Test
    public void getBankTest() {
        Bank expected = new Bank(TEST_ID, TEST_NAME);
        Bank actual = bankService.getBank(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    /**
     * Tests updating information about bank in the database
     */
    @Test
    public void updateBankTest() {
        assertEquals(1, bankService.updateBank(TEST_ID, TEST_NAME));
    }

    /**
     * Tests deleting bank from the database
     */
    @Test
    public void deleteBankTest() {
        assertEquals(1, bankService.deleteBank(TEST_ID));
    }

    /**
     * Closes database connection
     */
    @AfterAll
    static void closeConnection() {
        connector.closeConnection();
    }
}