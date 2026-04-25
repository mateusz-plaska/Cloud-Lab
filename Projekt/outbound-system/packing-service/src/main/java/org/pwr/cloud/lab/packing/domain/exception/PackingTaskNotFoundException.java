package org.pwr.cloud.lab.packing.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public class PackingTaskNotFoundException extends DomainNotFoundException {
    public PackingTaskNotFoundException(OrderId orderId) {
        super(buildException(orderId));
    }

    private static DomainRuntimeExceptionBuilder buildException(OrderId orderId) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.PACKING_TASK_NOT_FOUND.getCode())
                .withArg("orderId", orderId)
                .withMessage(ErrorCodes.PACKING_TASK_NOT_FOUND.getMessage());
    }
}
