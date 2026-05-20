package org.pwr.cloud.lab.common.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodes {
    ORDER_NOT_FOUND("ORD-404", "orderId not found"),
    PACKING_TASK_NOT_FOUND("PACK-404", "packing task not found"),
    BOX_SIZE_NOT_FOUND("BOX-404", "box size not found"),
    PICKING_TASK_NOT_FOUND("PICK-404", "picking task not found"),
    ATTRIBUTES_DOES_NOT_FIT_VALIDATION("NOT-VALID-400", "One or more attributes not valid"),
    USER_NOT_FOUND("USR-404", "user not found"),
    USERNAME_ALREADY_TAKEN("USR-409", "username already taken"),
    EMAIL_ALREADY_IN_USE("USR-409-E", "email already in use");

    private final String code;
    private final String message;
}
