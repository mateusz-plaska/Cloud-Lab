package org.pwr.cloud.lab.bff.domain.exception;

public class SsoAuthenticationException extends RuntimeException {
    public SsoAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
