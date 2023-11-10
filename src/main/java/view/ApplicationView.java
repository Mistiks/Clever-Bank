package view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ApplicationView {

    /** Array of main menu options */
    private final String[] mainMenu = {"1 - account replenishment", "2 - money withdrawal",
            "3 - transfer to another account", "4 - exit"};

    /** String with message after main menu options */
    private final String chooseOption = "Choose option: ";

    /** String with message before entering account id for replenishment */
    private final String replenishmentAccount = "Enter account id for replenishment: ";

    /** String with message before entering account id for money withdrawal */
    private final String withdrawalAccount = "Enter account id for withdrawal: ";

    /** String with message before entering replenishment amount */
    private final String replenishmentAmount = "Enter replenishment amount: ";

    /** String with message before entering withdrawal amount */
    private final String withdrawalAmount = "Enter withdrawal amount: ";

    /** String with message before entering sender account id for money transfer */
    private final String senderAccount = "Enter sender account id for transfer: ";

    /** String with message before entering receiver account id for money transfer */
    private final String receiverAccount = "Enter receiver account id for transfer: ";

    /** String with message before entering transfer amount */
    private final String transferAmount = "Enter transfer amount: ";

    /**
     * Prints menu and reads user input
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return number entered by user from keyboard
     */
    public int chooseOperation(Scanner scanner) {
        int option = 0;

        printMainMenu();

        while (option <= 0 || option > mainMenu.length) {
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException exception) {
                System.out.println("Please enter value between 1 and " + mainMenu.length);
                System.out.print(chooseOption);
            }
        }

        return option;
    }

    /** Prints menu before user input */
    public void printMainMenu() {
        for (String menu : mainMenu) {
            System.out.println(menu);
        }

        System.out.print(chooseOption);
    }

    /**
     * Retrieves entered account id from console
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return id entered by user
     */
    public long getIdForReplenishmentFromUser(Scanner scanner) {
        return getLong(replenishmentAccount, scanner);
    }

    /**
     * Retrieves entered account id from console for money withdrawal
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return id entered by user
     */
    public long getIdForWithdrawalFromUser(Scanner scanner) {
        return getLong(withdrawalAccount, scanner);
    }

    /**
     * Retrieves entered sender account id from console for money transfer
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return id entered by user
     */
    public long getSenderId(Scanner scanner) {
        return getLong(senderAccount, scanner);
    }

    /**
     * Retrieves entered receiver account id from console for money transfer
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return id entered by user
     */
    public long getReceiverId(Scanner scanner) {
        return getLong(receiverAccount, scanner);
    }

    /**
     * Retrieves entered money amount from console
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return amount entered by user
     */
    public double getAmountForReplenishmentFromUser(Scanner scanner) {
        return getDouble(replenishmentAmount, scanner);
    }

    /**
     * Retrieves entered money amount for transfer from console
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return amount entered by user
     */
    public double getAmountForTransfer(Scanner scanner) {
        return getDouble(transferAmount, scanner);
    }

    /**
     * Retrieves entered money amount from console for money withdrawal
     *
     * @param scanner Scanner object for reading input from console
     *
     * @return amount entered by user
     */
    public double getAmountForWithdrawalFromUser(Scanner scanner) {
        return getDouble(withdrawalAmount, scanner);
    }

    /**
     * Retrieves entered long value from console
     *
     * @param message string that will be shown to the user
     * @param scanner Scanner object for reading input from console
     *
     * @return value entered by user
     */
    public long getLong(String message, Scanner scanner) {
        long id = 0;
        boolean correctInput = false;

        System.out.print(message);

        while (!correctInput) {
            try {
                correctInput = true;
                id = scanner.nextLong();
            } catch (InputMismatchException exception) {
                correctInput = false;
                System.out.println("Please, enter correct id");
                System.out.print(message);
            }
        }

        return id;
    }

    /**
     * Retrieves entered double value from console
     *
     * @param message string that will be shown to the user
     * @param scanner Scanner object for reading input from console
     *
     * @return value entered by user
     */
    public double getDouble(String message, Scanner scanner) {
        double id = 0.0;
        boolean correctInput = false;

        System.out.print(message);

        while (!correctInput) {
            try {
                correctInput = true;
                id = scanner.nextDouble();
            } catch (InputMismatchException exception) {
                correctInput = false;
                System.out.println("Please, enter correct id");
                System.out.print(message);
            }
        }

        return id;
    }
}