package org.pwr.cloud.lab.common.exception;

import org.pwr.cloud.lab.common.domain.id.OrderId;

public class PickingTaskNotFoundException extends DomainRuntimeException {
    public PickingTaskNotFoundException(OrderId orderId) {
        super(buildException(orderId));
    }

    private static DomainRuntimeExceptionBuilder buildException(OrderId orderId) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.PICKING_TASK_NOT_FOUND.getCode())
                .withArg("orderId", orderId)
                .withMessage(ErrorCodes.PICKING_TASK_NOT_FOUND.getMessage());
    }
}
