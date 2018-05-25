package org.wso2.patchinformation.exceptions;

/**
 * Exceptions that occur during the execution of the process from start to finish.
 */
public class PatchInformationException extends Exception {

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     * @param cause the cause.
     */
    public PatchInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     */
    public PatchInformationException(String message) {
        super(message);
    }
}

