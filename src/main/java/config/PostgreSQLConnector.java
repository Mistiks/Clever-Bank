package config;

import config.api.IDatabaseConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that provides connection to database
 *
 * @author Vadim Rataiko
 */
public class PostgreSQLConnector implements IDatabaseConnector {
    /**
     * database url
     */
    private final String URL = "jdbc:postgresql://127.0.0.1:5432/edu";

    /**
     * database user
     */
    private final String USER = "postgres";

    /**
     * database password
     */
    private final String PASSWORD = "password";

    private Connection connection;

    /**
     * Creates database connection, or returns existing connection
     *
     * @return Connection object (connection with PostgreSQL), null if connection failed
     */
    @Override
    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return null;
        }

        return connection;
    }

    /**
     * Closes existing database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }
}