package config;

import controller.service.AccountService;
import org.junit.jupiter.api.*;
import utils.YmlFileReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/** Class for testing connection to PostgreSQL */
public class PostgreSQLConnectorTest {

    /** Database url */
    private static String url = "jdbc:postgresql://127.0.0.1:5432/edu";

    /** Database user */
    private static String user = "postgres";

    /** Database password */
    private static String password = "password";

    /** Instance of database connection class */
    private static PostgreSQLConnector connector;

    /** System stream for error messages */
    private PrintStream errorStream = System.err;

    /** Creates instance of database connection before tests */
    @BeforeAll
    public static void setConnectorAndService() {
        connector = new PostgreSQLConnector(url, user, password);
    }

    /** Tests database connection */
    @Test
    public void getConnectionTest() {
        Connection connection = connector.getConnection();
        assertNotNull(connection);
    }

    /** Tests error handling during connection */
    @Test
    public void getExceptionDuringConnection() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));

        connector = new PostgreSQLConnector(url, user, password + "1");
        connector.getConnection();
        assertEquals("SQL State: 28P01\n" +
                "FATAL: password authentication failed for user \"postgres\"", outputStream.toString());
<<<<<<< Updated upstream
=======
    }
>>>>>>> Stashed changes

        System.setErr(errorStream);
    }


    /** Closes database connection */
    @AfterAll
    public static void tearDown() {
        connector.closeConnection();
        connector = null;
    }
}