package org.pwr.cloud.lab.common.domain.exception;

public abstract class DomainNotFoundException extends DomainRuntimeException {
    protected DomainNotFoundException(DomainRuntimeExceptionBuilder builder) {
        super(builder);
    }
}
