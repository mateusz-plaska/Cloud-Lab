package org.pwr.cloud.lab.bff.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainConflictException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public class EmailAlreadyInUseException extends DomainConflictException {
    public EmailAlreadyInUseException(String email) {
        super(buildException(email));
    }

    private static DomainRuntimeExceptionBuilder buildException(String email) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.EMAIL_ALREADY_IN_USE.getCode())
                .withArg("email", email)
                .withMessage(ErrorCodes.EMAIL_ALREADY_IN_USE.getMessage());
    }
}
