package org.pwr.cloud.lab.packing.domain.exception;

import org.pwr.cloud.lab.common.domain.exception.DomainNotFoundException;
import org.pwr.cloud.lab.common.domain.exception.DomainRuntimeExceptionBuilder;
import org.pwr.cloud.lab.common.domain.exception.ErrorCodes;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;

public class BoxSizeNotFoundException extends DomainNotFoundException {
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
