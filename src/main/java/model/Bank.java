package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class that represents bank in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@NoArgsConstructor
public class Bank {

    /**
     * Bank id.
     */
    private long id;

    /**
     * Bank name
     */
    private String name;
}
