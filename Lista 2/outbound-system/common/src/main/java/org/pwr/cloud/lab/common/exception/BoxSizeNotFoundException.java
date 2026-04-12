package org.pwr.cloud.lab.common.exception;

import org.pwr.cloud.lab.common.domain.id.OrderId;

public class BoxSizeNotFoundException extends DomainRuntimeException {
    public BoxSizeNotFoundException(OrderId orderId, String boxSizeValue) {
        super(buildException(orderId, boxSizeValue));
    }

    private static DomainRuntimeExceptionBuilder buildException(OrderId orderId, String boxSizeValue) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.BOX_SIZE_NOT_FOUND.getCode())
                .withArg("orderId", orderId)
                .withArg("boxSize", boxSizeValue)
                .withMessage(ErrorCodes.BOX_SIZE_NOT_FOUND.getMessage());
    }
}
