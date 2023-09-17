package controller.service.api;

import model.entity.User;

/**
 * An interface that can be used for CRUD operations with users in application
 */
public interface IUserService {

    /**
     * Adds user to the database
     *
     * @param id user id
     * @param name name of user
     *
     * @return number of affected by query rows
     */
    int addUser(long id, String name);

    /**
     * Gets user from the database
     *
     * @param id user id
     *
     * @return instance of User class with specified id
     */
    User getUser(long id);

    /**
     * Updates user information in the database
     *
     * @param id user id
     * @param name new name
     *
     * @return number of affected by query rows
     */
    int updateUser(long id, String name);

    /**
     * Deletes user from the database
     * @param id user id
     *
     * @return number of affected by query rows
     */
    int deleteUser(long id);
}