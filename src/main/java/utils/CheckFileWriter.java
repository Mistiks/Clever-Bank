package utils;

import model.entity.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** A class for saving check in file */
public class CheckFileWriter {

    /** A path for creating directory for saving checks */
    private final String directoryPath = System.getProperty("user.dir") + File.separator + "check";

    /** A constructor without parameters for creating directory where checks will be kept */
    public CheckFileWriter() {
        createCheckDirectory();
    }

    /** Creates directory. Path of the directory stored in directoryPath variable */
    private void createCheckDirectory() {
        File file = new File(directoryPath);
        file.mkdir();
    }

    /**
     * Saves check in file. File name depends on transaction id
     *
     * @param check data which will be saved in file
     * @param transaction transaction for which the check was generated. Used to name a file
     */
    public void saveCheck(String check, Transaction transaction) {
        File file = new File(directoryPath + File.separator + "check_" + transaction.getId() + ".txt");

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(check);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}