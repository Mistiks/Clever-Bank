package controller.service.api;

import model.entity.Transaction;

import java.time.LocalDateTime;

/**
 * An interface that can be used for CRUD operations with transactions in application
 */
public interface ITransactionService {

    /**
     * Adds a new transaction to the database
     *
     * @param id transaction id
     * @param amount transaction amount
     * @param senderId id of transaction sender
     * @param receiverId id of transaction receiver
     *
     * @return id of created transaction
     */
    long addTransaction(long id, double amount, long senderId, long receiverId);

    /**
     * Reads transaction with specified id from database
     *
     * @param id transaction id
     *
     * @return instance of Transaction class with specified id
     */
    Transaction getTransaction(long id);

    /**
     * Updates transaction`s info in the database
     *
     * @param id user id
     * @param amount new amount
     * @param senderId new id of transaction sender
     * @param receiverId new id of transaction receiver
     * @param time new time of transaction
     *
     * @return number of affected by query rows
     */
    int updateTransaction(long id, double amount, long senderId, long receiverId, LocalDateTime time);

    /**
     * Deletes transaction from the database
     *
     * @param id transaction id
     *
     * @return number of affected by query rows
     */
    int deleteTransaction(long id);
}