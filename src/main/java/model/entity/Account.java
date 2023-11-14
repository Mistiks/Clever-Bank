package model.entity;

import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

/** A class that represents user's bank account in application. */
@Getter
@Setter
@AllArgsConstructor
public class Account {

    /** Account id */
    @Setter(AccessLevel.NONE)
    private long id;

    /** Bank where the user account is located */
    private long bankId;

    /** Account balance */
    private double balance;

    /** Owner of account */
    private long userId;

    /** Account creation date */
    private Date creationDate;

    /** Constructor without arguments with default values for class variables */
    public Account() {
        id = 0;
        bankId = 0;
        balance = 0.0;
        userId = 0;
        creationDate = Date.valueOf(LocalDate.MIN);
    }
}