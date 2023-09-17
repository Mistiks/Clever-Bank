package model.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * A class that represents transaction in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@AllArgsConstructor
public class Transaction {

    /**
     * Transaction id.
     */
    @Setter(AccessLevel.NONE)
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
     * Transaction sender. May be zero in case of account replenishment by the user.
     */
    private long sender;

    /**
     * Transaction receiver. May be zero in case of withdrawal of money from the account by the user.
     */
    private long receiver;

    /**
     * Constructor without arguments with default values for class variables
     */
    public Transaction() {
        id = 0;
        amount = 0.0;
        time = LocalDateTime.MIN;
        sender = 0;
        receiver = 0;
    }
}