package org.elmor.trainingsystemapi.exception;

public class KcBaseException extends RuntimeException {

    public KcBaseException(String message) {
        super(message);
    }

    public KcBaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
