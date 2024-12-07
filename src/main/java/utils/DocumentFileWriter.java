package utils;

import model.entity.Account;
import model.entity.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** A class for saving check in file */
public class DocumentFileWriter {

    /** A path for creating directory for saving checks */
    private final String checkPath = System.getProperty("user.dir") + File.separator + "check";

    /** A path for creating directory for saving account transaction statements */
    private final String accountStatementPath = System.getProperty("user.dir") + File.separator + "account-statement";

    /** A path for creating directory for saving account money statements */
    private final String moneyStatementPath = System.getProperty("user.dir") + File.separator + "money-statement";

    /** A constructor without parameters for creating directories where files will be saved */
    public DocumentFileWriter() {
        createDirectories();
    }

    /** Creates directories for saving files. Path of the directory stored in path variables */
    private void createDirectories() {
        File file = new File(checkPath);
        file.mkdir();

        file = new File(accountStatementPath);
        file.mkdir();
    }

    /**
     * Saves check in file. File name depends on transaction id
     *
     * @param check data which will be saved in file
     * @param transaction transaction for which the check was generated. Used to name a file
     */
    public void saveCheck(String check, Transaction transaction) {
        File file = new File(checkPath + File.separator + "check_" + transaction.getId() + ".txt");

        saveFile(check, file);
    }

    /**
     * Saves account statement in file. File name depends on account id
     *
     * @param accountStatement data which will be saved in file
     * @param account account for which statement was generated. Used to name a file
     */
    public void saveAccountStatement(String accountStatement, Account account) {
        File file = new File(accountStatementPath + File.separator + "account_statement_" + account.getId() + ".txt");

        saveFile(accountStatement, file);
    }

    /**
     * Saves account`s money statement in file. File name depends on account id
     *
     * @param accountStatement data which will be saved in file
     * @param account account for which statement was generated. Used to name a file
     */
    public void saveMoneyStatement(String accountStatement, Account account) {
        File file = new File(moneyStatementPath + File.separator + "money_statement_" + account.getId() + ".txt");

        saveFile(accountStatement, file);
    }

    /**
     * Saves file, according to its pathname
     *
     * @param data information that will be saved in file
     * @param file File object with defined save pathname
     */
    private void saveFile(String data, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}