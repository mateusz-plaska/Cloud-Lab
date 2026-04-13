package org.pwr.cloud.lab.ordergateway.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public class OrderNotFoundException extends DomainNotFoundException {
    public OrderNotFoundException(OrderId orderId) {
        super(buildException(orderId));
    }

    private static DomainRuntimeExceptionBuilder buildException(OrderId orderId) {
        return new DomainRuntimeExceptionBuilder()
                .withCode(ErrorCodes.ORDER_NOT_FOUND.getCode())
                .withArg("orderId", orderId)
                .withMessage(ErrorCodes.ORDER_NOT_FOUND.getMessage());
    }
}
