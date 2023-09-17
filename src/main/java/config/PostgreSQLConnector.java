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
     * Database url
     */
    private String url;

    /**
     * Database user
     */
    private String user;

    /**
     * Database password
     */
    private String password;

    private Connection connection;

    public PostgreSQLConnector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    /**
     * Creates database connection, or returns existing connection
     *
     * @return Connection object (connection with PostgreSQL), null if connection failed
     */
    @Override
    public Connection getConnection() {
        try {
            if (connection != null) {
                return connection;
            }

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        return connection;
    }

    /**
     * Closes existing database connection
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }
}