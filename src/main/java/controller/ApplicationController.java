package controller;

import config.api.IDatabaseConnector;
import controller.service.AccountService;
import controller.service.BankService;
import controller.service.TransactionService;
import controller.service.UserService;
import controller.service.api.IAccountService;
import controller.service.api.IBankService;
import controller.service.api.ITransactionService;
import controller.service.api.IUserService;
import model.dto.StatementDto;
import model.entity.Account;
import model.entity.Transaction;
import utils.DocumentFileWriter;
import utils.YmlFileReader;
import view.ApplicationView;
import view.CheckView;
import view.TransactionStatementView;

import java.sql.SQLException;
import java.util.List;
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

    /** Instance of IBankService interface for operations with banks */
    private final IBankService bankService;

    /** Instance of IUserService interface for operations with users */
    private final IUserService userService;

    /** Scanner object for reading input from console */
    private final Scanner scanner;

    /** Instance of CheckView object for creating checks after transactions */
    private final CheckView checkView;

    /** Instance of TransactionStatementView object for creating account transaction statement */
    private final TransactionStatementView statementView;

    /** Instance of CheckFileWriter for saving check to file */
    private final DocumentFileWriter documentFileWriter;

    /**
     * Constructor with parameter for controller creation
     *
     * @param connector implementation of database connection interface
     * @param scanner text scanner for user console input
     */
    public ApplicationController(IDatabaseConnector connector, Scanner scanner) {
        this.connector = connector;
        this.scanner = scanner;
        userService = new UserService(this.connector.getConnection());
        accountService = new AccountService(this.connector.getConnection(), new YmlFileReader());
        transactionService = new TransactionService(this.connector.getConnection());
        bankService = new BankService(this.connector.getConnection());
        this.checkView = new CheckView(bankService);
        this.statementView = new TransactionStatementView(bankService, userService);
        this.documentFileWriter = new DocumentFileWriter();
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

        while (option != 5) {
            switch (option) {
                case 1 -> replenishAccount();
                case 2 -> withdrawAccount();
                case 3 -> transferToAnotherAccount();
                case 4 -> getAccountStatement();
            }

            option = view.chooseOperation(scanner);
        }

        interestCheck.interrupt();
        scanner.close();
    }

    /** Replenishes user account and creates transaction record in database. Print messages in case of errors */
    private void replenishAccount() {
        Account account;
        Transaction transaction;
        String check;
        long id;
        double amount;
        long transactionId;

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

        transactionId = transactionService.addTransaction(1, amount, 0, id);

        if (transactionId == 0) {
            System.out.println("\nAn error occurred while saving transaction information\n");

            account.setBalance(account.getBalance() - amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        transaction = transactionService.getTransaction(transactionId);
        check = checkView.getCheck(transaction.getId(), transaction.getTime().toLocalDate(),
                transaction.getTime().toLocalTime(), 0, account.getBankId(),
                0, transaction.getReceiver(), transaction.getAmount());

        documentFileWriter.saveCheck(check, transaction);
        System.out.println(check);
    }

    /** Withdraws money from account and creates transaction record in database. Print messages in case of errors */
    private void withdrawAccount() {
        Account account;
        Transaction transaction;
        String check;
        long id;
        long transactionId;
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

        transactionId = transactionService.addTransaction(1, amount, id, 0);

        if (transactionId == 0) {
            System.out.println("\nAn error occurred while saving transaction information\n");

            account.setBalance(account.getBalance() + amount); // rollback balance update
            accountService.updateAccount(account);

            return;
        }

        transaction = transactionService.getTransaction(transactionId);
        check = checkView.getCheck(transaction.getId(), transaction.getTime().toLocalDate(),
                transaction.getTime().toLocalTime(), account.getBankId(), 0,
                transaction.getSender(), 0, transaction.getAmount());

        documentFileWriter.saveCheck(check, transaction);
        System.out.println(check);
    }

    /**
     * Transfers money from one account to another in single transaction and creates transaction record in database.
     * Saves check in file. Prints message in case of errors
     */
    private void transferToAnotherAccount() {
        Account receiver;
        Account sender;
        Transaction transaction;
        String check;
        long senderId;
        long receiverId;
        long transactionId;
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
            synchronized (connector) {
                connector.getConnection().setAutoCommit(false);

                receiver.setBalance(receiver.getBalance() + amount);
                sender.setBalance(sender.getBalance() - amount);
                accountService.updateAccount(sender);
                accountService.updateAccount(receiver);

                transactionId = transactionService.addTransaction(1, amount, senderId, receiverId);

                if (transactionId == 0) {
                    System.out.println("\nAn error occurred while saving transaction information\n");

                    connector.getConnection().rollback();
                    connector.getConnection().setAutoCommit(true);

                    return;
                }

                connector.getConnection().commit();
                connector.getConnection().setAutoCommit(true);

                transaction = transactionService.getTransaction(transactionId);
                check = checkView.getCheck(transaction.getId(), transaction.getTime().toLocalDate(),
                        transaction.getTime().toLocalTime(), sender.getBankId(), receiver.getBankId(),
                        transaction.getSender(), transaction.getReceiver(), transaction.getAmount());
            }

            documentFileWriter.saveCheck(check, transaction);
            System.out.println(check);
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

    /** Prints account statement and saves it in file */
    private void getAccountStatement() {
        int intervalOption;
        Account account;
        long accountId;
        String statement;
        List<StatementDto> statementList;

        accountId = view.getAccountId(scanner);
        account = accountService.getAccount(accountId);

        if (account.getId() == 0) {
            System.out.println("\nAccount with entered id doesn't exist\n");

            return;
        }

        intervalOption = view.chooseInterval(scanner);
        statementList = transactionService.getTransactionList(accountId);
        statement = statementView.getStatement(account, statementList, intervalOption);

        documentFileWriter.saveAccountStatement(statement, account);
    }
}