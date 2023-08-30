package config;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Class for testing connection to PostgreSQL
 *
 * @author Vadim Rataiko
 */
public class PostgreSQLConnectorTest {
    /**
     * Instance of database connection class
     */
    PostgreSQLConnector connector;

    /**
     * Creates instance of database connection class before every test
     */
    @BeforeEach
    void setConnector() {
        connector = new PostgreSQLConnector();
    }

    /**
     * Tests database connection
     */
    @Test
    void getConnectionTest() {
        assertNotEquals(null, connector.getConnection());
    }

    /**
     * Closes database connection
     */
    @AfterEach
    void closeConnection() {
        connector.closeConnection();
    }
}