package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class that represents user's bank account in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@NoArgsConstructor
public class Account {

    /**
     * Account id.
     */
    private long id;

    /**
     * Bank where the user account is located
     */
    private Bank bank;

    /**
     * Account balance
     */
    private double balance;

    /**
     * Owner of account;
     */
    private User user;
}