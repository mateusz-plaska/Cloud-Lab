package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceProxy {

    private final ReservationClient reservationClient;

    public String getStocks() {
        try {
            return reservationClient.getStocks();
        } catch (Exception e) {
            log.warn("Reservation service unavailable (getStocks): {}", e.getMessage());
            throw e;
        }
    }

    public void addStock(String productId, int quantity) {
        try {
            reservationClient.addStock(productId, quantity);
        } catch (Exception e) {
            log.warn("Reservation service unavailable (addStock): {}", e.getMessage());
            throw e;
        }
    }

    public String getProducts() {
        try {
            return reservationClient.getProducts();
        } catch (Exception e) {
            log.warn("Reservation service unavailable (getProducts): {}", e.getMessage());
            throw e;
        }
    }

    public String createProduct(String name) {
        try {
            return reservationClient.createProduct(name);
        } catch (Exception e) {
            log.warn("Reservation service unavailable (createProduct): {}", e.getMessage());
            throw e;
        }
    }
}
