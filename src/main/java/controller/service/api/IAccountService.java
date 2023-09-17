package controller.service.api;

import model.entity.Account;

/**
 * An interface that can be used for CRUD operations with accounts in application
 */
public interface IAccountService {

    /**
     * Adds a new account to the database
     *
     * @param id account id
     * @param bankId id of bank
     * @param balance account balance
     * @param userId id of account owner
     *
     * @return number of affected by query rows
     */
    int addAccount(long id, long bankId, double balance, long userId);

    /**
     * Reads account with specified id from database
     *
     * @param id account id
     *
     * @return instance of Account class with specified id
     */
    Account getAccount(long id);
}