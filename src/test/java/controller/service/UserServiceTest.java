package controller.service;

import config.PostgreSQLConnector;

import config.api.IDatabaseConnector;
import model.entity.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/** A class for testing CRUD operations with users */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    /** Database url */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /** Database user */
    private static String user = "postgres";

    /** Database password */
    private static String password = "password";

    /** Instance of database connection interface */
    private static IDatabaseConnector connector;

    /** Instance of the class implementing CRUD operations with users in the database */
    private static UserService userService;

    /** Name of user that will be used in tests */
    private static final String TEST_NAME = "Test User";

    /** Name of user that will be used in tests */
    private static final String UPDATED_NAME = "Test User Test";

    /** Id of user that will be used in tests */
    private static final long TEST_ID = -5;

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

        userService = new UserService(connection);
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

        userService = null;
        connector = null;
    }

    /** Tests creating a new user in the database */
    @Test
    @Order(1)
    public void addUserTest() {
        assertEquals(1, userService.addUser(TEST_ID, TEST_NAME));
    }

    /** Tests receiving information about user from the database */
    @Test
    @Order(2)
    public void getUserTest() {
        User expected = new User(TEST_ID, TEST_NAME);
        User actual = userService.getUser(TEST_ID);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    /** Tests updating information about user in the database */
    @Test
    @Order(3)
    public void updateUserTest() {
        assertEquals(1, userService.updateUser(TEST_ID, UPDATED_NAME));
        User actual = userService.getUser(TEST_ID);
        assertEquals(UPDATED_NAME, actual.getName());
    }

    /** Tests deleting user from the database */
    @Test
    @Order(4)
    public void deleteUserTest() {
        assertEquals(1, userService.deleteUser(TEST_ID));
    }
}