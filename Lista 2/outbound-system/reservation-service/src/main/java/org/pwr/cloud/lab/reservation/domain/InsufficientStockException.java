package org.pwr.cloud.lab.reservation.domain;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
