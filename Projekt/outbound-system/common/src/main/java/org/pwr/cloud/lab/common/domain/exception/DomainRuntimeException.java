package org.pwr.cloud.lab.common.domain.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DomainRuntimeException extends RuntimeException {
    private final String code;
    private final Map<String, Object> args;

    public DomainRuntimeException(DomainRuntimeExceptionBuilder builder) {
        super(builder.getMessage(), builder.getCause());
        this.code = builder.getCode();
        this.args = builder.getArgs();
    }
}
