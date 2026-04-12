package org.pwr.cloud.lab.common.exception;

import java.util.Map;

public class AttributesValidationException extends DomainRuntimeException {
    public AttributesValidationException(Map<String, Object> args) {
        super(buildException(args));
    }

    private static DomainRuntimeExceptionBuilder buildException(Map<String, Object> args) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.ATTRIBUTES_DOES_NOT_FIT_VALIDATION.getCode())
                .withArgs(args)
                .withMessage(ErrorCodes.ATTRIBUTES_DOES_NOT_FIT_VALIDATION.getMessage());
    }
}
