package org.wso2.patchinformation.exceptions;

public class ConnectionException extends EmailProcessException{

    public ConnectionException(String message, Throwable cause) {

        super(message, cause);
    }

    public ConnectionException(String message) {

        super(message);
    }
}
