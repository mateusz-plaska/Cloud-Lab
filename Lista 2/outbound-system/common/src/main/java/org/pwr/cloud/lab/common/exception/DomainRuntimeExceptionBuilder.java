package org.pwr.cloud.lab.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class DomainRuntimeExceptionBuilder {
    private String message;
    private Throwable cause;
    private String code;
    private Map<String, Object> args;

    public DomainRuntimeExceptionBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public DomainRuntimeExceptionBuilder withCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public DomainRuntimeExceptionBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public DomainRuntimeExceptionBuilder withArgs(Map<String, Object> args) {
        this.args = args;
        return this;
    }

    public DomainRuntimeExceptionBuilder withArg(String key, Object value) {
        if (args == null) {
            args = new HashMap<>();
        }
        this.args.put(key, value);
        return this;
    }
}
