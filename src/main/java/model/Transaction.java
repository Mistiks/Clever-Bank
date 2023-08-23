package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A class that represents transaction in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    /**
     * Transaction id.
     */
    private long id;

    /**
     * Transaction amount;
     */
    private double amount;

    /**
     * Transaction date and time;
     */
    private LocalDateTime time;

    /**
     * Transaction sender. May be null in case of account replenishment by the user.
     */
    private Account sender;

    /**
     * Transaction receiver. May be null in case of withdrawal of money from the account by the user.
     */
    private Account receiver;
}