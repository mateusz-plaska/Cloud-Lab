package org.pwr.cloud.lab.bff.api.dto.sse;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Station {
    public static final String ORDER_GATEWAY = "order-gateway";
    public static final String RESERVATION = "reservation";
    public static final String PICKING = "picking";
    public static final String PACKING = "packing";
    public static final String SHIPPING = "shipping";
}
