package org.pwr.cloud.lab.common.domain.exception;

public abstract class DomainConflictException extends DomainRuntimeException {
    protected DomainConflictException(DomainRuntimeExceptionBuilder builder) {
        super(builder);
    }
}
