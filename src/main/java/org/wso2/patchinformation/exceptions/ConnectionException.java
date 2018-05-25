package org.wso2.patchinformation.exceptions;

/**
 * Exceptions that occur while trying to make a connection
 */
public class ConnectionException extends PatchInformationException {

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     * @param cause   the cause.
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     */
    public ConnectionException(String message) {
        super(message);
    }
}


