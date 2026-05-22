package org.pwr.cloud.lab.bff.infrastructure.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.bff.api.dto.packing.FinishPackingRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackingServiceProxy {

    private final PackingClient packingClient;

    public String finishPacking(String orderId, FinishPackingRequest request) {
        try {
            return packingClient.finishPacking(orderId, request);
        } catch (Exception e) {
            log.warn("Packing service unavailable for order [{}]: {}", orderId, e.getMessage());
            throw e;
        }
    }
}
