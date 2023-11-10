import config.PostgreSQLConnector;
import config.api.IDatabaseConnector;
import controller.ApplicationController;

import java.util.Scanner;

/** A class that represents application entry point functionality */
public class Main {

    /**
     * Application entry point. Provides an interface for interaction between the user and the program.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        IDatabaseConnector connector = new PostgreSQLConnector("jdbc:postgresql://127.0.0.1:5432/edu",
                "postgres", "password");
        ApplicationController controller = new ApplicationController(connector, scanner);

        controller.start();
    }
}