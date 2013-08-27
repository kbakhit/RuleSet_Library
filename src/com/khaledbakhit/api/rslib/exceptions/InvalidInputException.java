package com.khaledbakhit.api.rslib.exceptions;

/**
 * InvalidInputException indicates input is invalid.
 * @author Khaled Bakhit
 * @since 3.0  
 * @version 1.0
 */
public class InvalidInputException extends Exception {

    /**
     * Creates a new instance of <code>InvalidInputException</code> without detail message.
     */
    public InvalidInputException() 
    {
        super("Invalid input was detected.");
    }

    /**
     * Constructs an instance of <code>InvalidInputException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidInputException(String msg) {
        super(msg);
    }
}
