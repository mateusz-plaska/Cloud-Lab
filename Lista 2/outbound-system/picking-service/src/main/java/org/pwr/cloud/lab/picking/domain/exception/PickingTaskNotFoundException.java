package org.pwr.cloud.lab.picking.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public class PickingTaskNotFoundException extends DomainNotFoundException {
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
