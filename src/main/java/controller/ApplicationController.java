package controller;

import config.api.IDatabaseConnector;
import controller.service.AccountService;
import controller.service.TransactionService;
import controller.service.api.IAccountService;
import controller.service.api.ITransactionService;
import model.entity.Account;
import utils.YmlFileReader;
import view.ApplicationView;

import java.sql.SQLException;
import java.util.Scanner;

/** Class that controls the work of the application depending on user input */
public class ApplicationController {

    /** Instance of ApplicationView class for showing menu and reading user input */
    private final ApplicationView view = new ApplicationView();

    /** Instance of IDatabaseConnector interface for database operations */
    private final IDatabaseConnector connector;

    /** Instance of IAccountService interface for operations with accounts */
    private final IAccountService accountService;

    /** Instance of ITransactionService interface for operations with transactions */
    private final ITransactionService transactionService;

    /** Scanner object for reading input from console */
    private final Scanner scanner;

    /**
     * Constructor with parameter for controller creation
     *
     * @param connector implementation of database connection interface
     */
    public ApplicationController(IDatabaseConnector connector, Scanner scanner) {
        this.connector = connector;
        this.scanner = scanner;
        accountService = new AccountService(this.connector.getConnection(), new YmlFileReader());
        transactionService = new TransactionService(this.connector.getConnection());
    }

    /**
     * Starts the service based on user input.
     * Checks in the second thread whether interest should be accrued to Clever-Bank users
     */
    public void start() {
        int option;
        Thread interestCheck = new Thread(accountService);
        interestCheck.start();

        option = view.chooseOperation(scanner);

        while (option != 4) {
            switch (option) {
                case 1 -> replenishAccount();
                case 2 -> withdrawAccount();
                case 3 -> transferToAnotherAccount();
            }

            option = view.chooseOperation(scanner);
        }

        interestCheck.interrupt();
    }

    /** Replenishes user account and creates transaction record in database. Print messages in case of errors */
    private void replenishAccount() {
        Account account;
        long id;
        double amount;

        id = view.getIdForReplenishmentFromUser(scanner);
        amount = view.getAmountForReplenishmentFromUser(scanner);
        account = accountService.getAccount(id);

        if (account.getId() == 0) {
            System.out.println("\nAccount with entered id doesn't exist\n");
            return;
        }

        account.setBalance(account.getBalance() + amount);

        if (accountService.updateAccount(account) != 1) {
            System.out.println("\nAn error occurred while replenishing your account\n");
            return;
        }

        if (transactionService.addTransaction(1, amount, 0, id) != 1) {
            System.out.println("\nAn error occurred while saving transaction information\n");

            account.setBalance(account.getBalance() - amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        System.out.println("Operation completed successfully\n");
    }

    /** Withdraws money from account and creates transaction record in database. Print messages in case of errors */
    private void withdrawAccount() {
        Account account;
        long id;
        double amount;

        id = view.getIdForWithdrawalFromUser(scanner);
        amount = view.getAmountForWithdrawalFromUser(scanner);
        account = accountService.getAccount(id);

        if (account.getId() == 0) {
            System.out.println("\nAccount with entered id doesn't exist\n");

            return;
        }

        if (account.getBalance() < amount) {
            System.out.println("\nThere are not enough money on account balance\n");

            return;
        }

        account.setBalance(account.getBalance() - amount);

        if (accountService.updateAccount(account) != 1) {
            System.out.println("\nAn error occurred during cash withdrawal\n");

            return;
        }

        if (transactionService.addTransaction(1, amount, 0, id) != 1) {
            System.out.println("\nAn error occurred while saving transaction information\n");

            account.setBalance(account.getBalance() + amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        System.out.println("Operation completed successfully\n");
    }

    /**
     * Transfers money from one account to another in single transaction and creates transaction record in database.
     * Print messages in case of errors
     */
    private void transferToAnotherAccount() {
        Account receiver;
        Account sender;
        long senderId;
        long receiverId;
        double amount;

        senderId = view.getSenderId(scanner);
        receiverId = view.getReceiverId(scanner);
        amount = view.getAmountForTransfer(scanner);
        receiver = accountService.getAccount(receiverId);
        sender = accountService.getAccount(senderId);

        if (receiver.getId() == 0) {
            System.out.println("\nReceiver account with entered id doesn't exist\n");

            return;
        }

        if (sender.getId() == 0) {
            System.out.println("\nSender account with entered id doesn't exist\n");

            return;
        }

        if (sender.getBalance() < amount) {
            System.out.println("\nThere are not enough money on account balance\n");

            return;
        }

        try {
            connector.getConnection().setAutoCommit(false);

            receiver.setBalance(receiver.getBalance() + amount);
            sender.setBalance(sender.getBalance() - amount);
            accountService.updateAccount(sender);
            accountService.updateAccount(receiver);

            if (transactionService.addTransaction(1, amount, senderId, receiverId) != 1) {
                System.out.println("\nAn error occurred while saving transaction information\n");

                connector.getConnection().rollback();
                connector.getConnection().setAutoCommit(true);

                return;
            }

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            try {
                System.out.println("\nTransfer failed due to an error\n");

                connector.getConnection().rollback();
                connector.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            }
        }
    }
}