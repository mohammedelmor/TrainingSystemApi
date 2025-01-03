package org.elmor.trainingsystemapi.exception;

public class KcUserWrongPasswordException extends KcBaseException {

    public KcUserWrongPasswordException(String message) {
        super(message);
    }

    public KcUserWrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

}
