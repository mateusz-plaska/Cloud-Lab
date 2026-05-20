package org.pwr.cloud.lab.bff.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainConflictException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;

public class UsernameTakenException extends DomainConflictException {
    public UsernameTakenException(String username) {
        super(buildException(username));
    }

    private static DomainRuntimeExceptionBuilder buildException(String username) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.USERNAME_ALREADY_TAKEN.getCode())
                .withArg("username", username)
                .withMessage(ErrorCodes.USERNAME_ALREADY_TAKEN.getMessage());
    }
}
