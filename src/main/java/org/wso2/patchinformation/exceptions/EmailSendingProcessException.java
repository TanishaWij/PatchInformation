package org.wso2.patchinformation.exceptions;

/**
 * Exceptions that occur during the execution of the whole process.
 */
public class EmailSendingProcessException extends Exception {

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     * @param message The detailed message.
     * @param cause the cause.
     */
    public EmailSendingProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     * @param message The detailed message.
     */
    public EmailSendingProcessException(String message) {
        super(message);
    }
}
