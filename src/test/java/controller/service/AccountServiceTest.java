package controller.service;

import config.PostgreSQLConnector;
import controller.service.AccountService;
import model.entity.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class for testing CRUD operations with accounts
 *
 * @author Vadim Rataiko
 */
public class AccountServiceTest {

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
     * Instance of the class implementing CRUD operations with accounts in the database
     */
    private static AccountService accountService;

    /**
     * Id that will be used in tests
     */
    private static final long TEST_ID = -5;

    /**
     * Account balance that will be used in tests
     */
    private static final double TEST_BALANCE = 5.00;

    /**
     * Value that will be used in updating account information test
     */
    private static final double TEST_BALANCE_UPDATE = 1.00;

    /**
     * Creates instance of database connection class before every test
     */
    @BeforeAll
    public static void setConnector() {
        connector = new PostgreSQLConnector(url, user, password);
        accountService = new AccountService(connector.getConnection());
    }

    /**
     * Tests creating a new account in the database
     */
    @Test
    public void addAccountTest() {
        assertEquals(1, accountService.addAccount(TEST_ID, TEST_ID, TEST_BALANCE, TEST_ID));
    }

    /**
     * Tests receiving information about account from the database
     */
    @Test
    public void getAccountTest() {
        Account expected = new Account(TEST_ID, TEST_ID, TEST_BALANCE, TEST_ID);
        Account actual = accountService.getAccount(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBankId(), actual.getBankId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getBalance(), actual.getBalance());
    }

    /**
     * Tests updating information about account in the database
     */
    @Test
    public void updateAccountTest() {
        assertEquals(1, accountService.updateAccount(TEST_ID, TEST_ID, TEST_BALANCE + TEST_BALANCE_UPDATE, TEST_ID));

        Account actual = accountService.getAccount(TEST_ID);
        assertEquals(TEST_BALANCE + TEST_BALANCE_UPDATE, actual.getBalance());
    }

    /**
     * Tests deleting account from the database
     */
    @Test
    public void deleteAccountTest() {
        assertEquals(1, accountService.deleteAccount(TEST_ID));
    }

    /**
     * Closes database connection
     */
    @AfterAll
    static void closeConnection() {
        connector.closeConnection();
    }
}