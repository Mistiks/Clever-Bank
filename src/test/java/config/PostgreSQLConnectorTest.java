package config;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing connection to PostgreSQL
 *
 * @author Vadim Rataiko
 */
public class PostgreSQLConnectorTest {

    /**
     * Database url
     */
    private String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /**
     * Database user
     */
    private String user = "postgres";

    /**
     * Database password
     */
    private String password = "password";
    /**
     * Instance of database connection class
     */
    private PostgreSQLConnector connector;

    /**
     * System stream for error messages
     */
    private PrintStream errorStream = System.err;

    /**
     * Tests database connection
     */
    @Test
    public void getConnectionTest() {
        connector = new PostgreSQLConnector(url, user, password);

        Connection connection = connector.getConnection();
        assertNotNull(connection);
    }

    /**
     * Tests error handling during connection
     */
    @Test
    public void getExceptionDuringConnection() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));

        connector = new PostgreSQLConnector(url, user, password + "1");
        connector.getConnection();
        assertEquals("SQL State: 28P01\n" +
                "ВАЖНО: пользователь \"postgres\" не прошёл проверку подлинности (по паролю)", outputStream.toString());
    }


    /**
     * Closes database connection
     */
    @AfterEach
    public void closeConnection() {
        System.setErr(errorStream);
    }
}