package config.api;

import java.sql.Connection;

/**
 * Interface for database connection
 *
 * @author Vadim Rataiko
 */
public interface IDatabaseConnector {

    /**
     * Creates database connection
     *
     * @return connection with database
     */
    Connection getConnection();

    /** Closes existing database connection */
    void closeConnection();
}