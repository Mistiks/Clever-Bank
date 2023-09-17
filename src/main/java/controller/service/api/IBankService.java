package controller.service.api;

import model.entity.Bank;

/**
 * An interface that can be used for CRUD operations with banks in application
 */
public interface IBankService {

    /**
     * Adds a new bank to the database
     *
     * @param id bank id
     * @param name bank name
     *
     * @return number of affected by query rows
     */
    int addBank(long id, String name);

    /**
     * Reads bank with specified id from database
     *
     * @param id bank id
     *
     * @return instance of Bank class with specified id
     */
    Bank getBank(long id);

    /**
     * Updates bank`s name in the database
     *
     * @param id bank id.
     * @param name new name
     *
     * @return number of affected by query rows
     */
    int updateBank(long id, String name);

    /**
     * Deletes bank from the database
     *
     * @param id bank id.
     *
     * @return number of affected by query rows
     */
    int deleteBank(long id);
}
