package controller;

import config.api.IDatabaseConnector;
import controller.service.AccountService;
import controller.service.TransactionService;
import controller.service.api.IAccountService;
import controller.service.api.ITransactionService;
import model.entity.Account;
import view.ApplicationView;

import java.sql.SQLException;

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

    /**
     * Constructor with parameter for controller creation
     *
     * @param connector implementation of database connection interface
     */
    public ApplicationController(IDatabaseConnector connector) {
        this.connector = connector;
        accountService = new AccountService(this.connector.getConnection());
        transactionService = new TransactionService(this.connector.getConnection());
    }

    /** Starts the service based on user input */
    public void start() {
        int option = view.chooseOperation();

        while (option != 4) {
            switch (option) {
                case 1 -> replenishAccount();
                case 2 -> withdrawAccount();
                case 3 -> transferToAnotherAccount();

                default -> System.out.println("Wrong input. Try again.");
            }

            option = view.chooseOperation();
        }
    }

    /** Replenishes user account and creates transaction record in database. Print messages in case of errors */
    private void replenishAccount() {
        Account account;
        long id;
        double amount;

        id = view.getIdForReplenishmentFromUser();
        amount = view.getAmountForReplenishmentFromUser();
        account = accountService.getAccount(id);

        if (account.getId() == 0) {
            System.out.println("Account with entered id doesn't exist");
            return;
        }

        account.setBalance(account.getBalance() + amount);

        if (accountService.updateAccount(account) != 1) {
            System.out.println("An error occurred while replenishing your account");
            return;
        }

        if (transactionService.addTransaction(1, amount, 0, id) != 1) {
            System.out.println("An error occurred while saving transaction information");

            account.setBalance(account.getBalance() - amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        System.out.println("Operation completed successfully");
    }

    /** Withdraws money from account and creates transaction record in database. Print messages in case of errors */
    private void withdrawAccount() {
        Account account;
        long id;
        double amount;

        id = view.getIdForWithdrawalFromUser();
        amount = view.getAmountForWithdrawalFromUser();
        account = accountService.getAccount(id);

        if (account.getId() == 0) {
            System.out.println("Account with entered id doesn't exist");
            return;
        }

        if (account.getBalance() < amount) {
            System.out.println("There are not enough money on account balance");
            return;
        }

        account.setBalance(account.getBalance() - amount);

        if (accountService.updateAccount(account) != 1) {
            System.out.println("An error occurred during cash withdrawal");
            return;
        }

        if (transactionService.addTransaction(1, amount, 0, id) != 1) {
            System.out.println("An error occurred while saving transaction information");

            account.setBalance(account.getBalance() + amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        System.out.println("Operation completed successfully");
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

        senderId = view.getSenderId();
        receiverId = view.getReceiverId();
        amount = view.getAmountForTransfer();
        receiver = accountService.getAccount(receiverId);
        sender = accountService.getAccount(senderId);

        if (receiver.getId() == 0) {
            System.out.println("Receiver account with entered id doesn't exist");
            return;
        }

        if (sender.getId() == 0) {
            System.out.println("Sender account with entered id doesn't exist");
            return;
        }

        if (sender.getBalance() < amount) {
            System.out.println("There are not enough money on account balance");
            return;
        }

        try {
            connector.getConnection().setAutoCommit(false);

            receiver.setBalance(receiver.getBalance() + amount);
            sender.setBalance(sender.getBalance() - amount);
            accountService.updateAccount(sender);
            accountService.updateAccount(receiver);

            if (transactionService.addTransaction(1, amount, senderId, receiverId) != 1) {
                System.out.println("An error occurred while saving transaction information");
                connector.getConnection().rollback();
                connector.getConnection().setAutoCommit(true);
                return;
            }

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            try {
                System.out.println("Transfer failed due to an error");

                connector.getConnection().rollback();
                connector.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            }
        }
    }
}