package org.elmor.trainingsystemapi.exception;

public class KcUserNotFoundException extends KcBaseException {

    public KcUserNotFoundException(String message) {
        super(message);
    }

    public KcUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
