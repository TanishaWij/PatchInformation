package org.wso2.patchinformation.exceptions;

/**
 * Exceptions that occur while dealing with content from either Jira, Pmt or email creation
 */
public class ContentException extends PatchInformationException {

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     * @param cause   the cause.
     */
    public ContentException(String message, Throwable cause) {
        super(message, cause);
    }
}


