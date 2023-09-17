package model.entity;

import lombok.*;

/**
 * A class that represents user's bank account in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@AllArgsConstructor
public class Account {

    /**
     * Account id.
     */
    @Setter(AccessLevel.NONE)
    private long id;

    /**
     * Bank where the user account is located
     */
    private long bankId;

    /**
     * Account balance
     */
    private double balance;

    /**
     * Owner of account;
     */
    private long userId;

    /**
     * Constructor without arguments with default values for class variables
     */
    public Account() {
        id = 0;
        bankId = 0;
        balance = 0.0;
        userId = 0;
    }
}