package controller.service;

import config.PostgreSQLConnector;

import controller.service.UserService;
import model.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing CRUD operations with users
 *
 * @author Vadim Rataiko
 */
public class UserServiceTest {

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
     * Instance of the class implementing CRUD operations with users in the database
     */
    private static UserService userService;

    /**
     * Name of user that will be used in tests
     */
    private static final String TEST_NAME = "Test User";

    /**
     * Id of user that will be used in tests
     */
    private static final long TEST_ID = -5;

    /**
     * Creates instance of database connection class before every test
     */
    @BeforeAll
    public static void setConnector() {
        connector = new PostgreSQLConnector(url, user, password);
        userService = new UserService(connector.getConnection());
    }

    /**
     * Tests creating a new user in the database
     */
    @Test
    public void addUserTest() {
        assertEquals(1, userService.addUser(TEST_ID, TEST_NAME));
    }

    /**
     * Tests receiving information about user from the database
     */
    @Test
    public void getUserTest() {
        User expected = new User(TEST_ID, TEST_NAME);
        User actual = userService.getUser(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    /**
     * Tests updating information about user in the database
     */
    @Test
    public void updateUserTest() {
        assertEquals(1, userService.updateUser(TEST_ID, TEST_NAME));
    }

    /**
     * Tests deleting user from the database
     */
    @Test
    public void deleteUserTest() {
        assertEquals(1, userService.deleteUser(TEST_ID));
    }

    /**
     * Closes database connection
     */
    @AfterAll
    static void closeConnection() {
        connector.closeConnection();
    }
}