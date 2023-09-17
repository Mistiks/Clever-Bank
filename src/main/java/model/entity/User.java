package model.entity;

import lombok.*;

import java.util.List;

/**
 * A class that represents user in application
 *
 * @author Vadim Rataiko
 */
@Getter
@Setter
@AllArgsConstructor
public class User {

    /**
     * User id
     */
    @Setter(AccessLevel.NONE)
    private long id;

    /**
     * User name
     */
    private String name;

    /**
     * Constructor without arguments with default values for class variables
     */
    public User() {
        id = 0;
        name = "";
    }
}