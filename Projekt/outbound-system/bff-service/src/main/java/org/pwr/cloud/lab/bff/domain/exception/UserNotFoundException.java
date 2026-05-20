package org.pwr.cloud.lab.bff.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;

public class UserNotFoundException extends DomainNotFoundException {
    public UserNotFoundException(String username) {
        super(buildException(username));
    }

    private static DomainRuntimeExceptionBuilder buildException(String username) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.USER_NOT_FOUND.getCode())
                .withArg("username", username)
                .withMessage(ErrorCodes.USER_NOT_FOUND.getMessage());
    }
}
