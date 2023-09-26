package controller.service;

import config.PostgreSQLConnector;
import config.api.IDatabaseConnector;
import controller.service.api.IAccountService;
import model.entity.Account;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Class for testing CRUD operations with accounts */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceTest {

    /** Database url */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /** Database user */
    private static String user = "postgres";

    /** Database password */
    private static String password = "password";

    /** Instance of database connection interface */
    private static IDatabaseConnector connector;

    /** Instance of the class implementing CRUD operations with accounts in the database */
    private static IAccountService accountService;

    /** Id that will be used in tests */
    private final long testId = -5;

    /** Bank id that will be used in tests */
    private final long testBankId = 1;

    /** User id that will be used in tests */
    private final long testUserId = -1;

    /** Account balance that will be used in tests */
    private final double testBalance = 5.00;

    /** Value that will be used in updating account information test */
    private final double testBalanceUpdate = 2.00;

    /** Creates instance of database connection and account service before tests */
    @BeforeAll
    public static void setConnectorAndService() {
        connector = new PostgreSQLConnector(url, user, password);
        Connection connection = connector.getConnection();

        try {
            connection.setAutoCommit(false); // operations in this test have no effect on the database
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        accountService = new AccountService(connection);
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

        accountService = null;
        connector = null;
    }

    /** Tests creating a new account in the database */
    @Test
    @Order(1)
    public void addAccountTest() {
        assertEquals(1, accountService.addAccount(testId, testBankId, testBalance, testUserId));
    }

    /** Tests receiving information about account from the database */
    @Test
    @Order(2)
    public void getAccountTest() {
        Account expected = new Account(testId, testBankId, testBalance, testUserId);
        Account actual = accountService.getAccount(testId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBankId(), actual.getBankId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getBalance(), actual.getBalance());
    }

    /** Tests updating information about account in the database */
    @Test
    @Order(3)
    public void updateAccountTest() {
        assertEquals(1, accountService.updateAccount(new Account(testId, testBankId,
                testBalance + testBalanceUpdate, testUserId)));
        Account updated = accountService.getAccount(testId);
        assertEquals(testBalance + testBalanceUpdate, updated.getBalance());
    }

    /** Tests deleting account from the database */
    @Test
    @Order(4)
    public void deleteAccountTest() {
        assertEquals(1, accountService.deleteAccount(testId));
    }
}