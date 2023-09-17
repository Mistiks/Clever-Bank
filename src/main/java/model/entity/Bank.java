package model.entity;

import lombok.*;

/**
 * A class that represents bank in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@AllArgsConstructor
public class Bank {

    /**
     * Bank id.
     */
    @Setter(AccessLevel.NONE)
    private long id;

    /**
     * Bank name
     */
    private String name;

    /**
     * Constructor without arguments with default values for class variables
     */
    public Bank() {
        id = 0;
        name = "";
    }
}