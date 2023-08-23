package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A class that represents user in application.
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@NoArgsConstructor
public class User {

    /**
     * User id.
     */
    private long id;

    /**
     * User accounts. User may have several accounts in different banks.
     */
    private List<Account> accounts;
}