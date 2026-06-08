package org.pwr.cloud.lab.bff.infrastructure.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.pwr.cloud.lab.bff.api.dto.sse.OrderStatusUpdate;
import org.pwr.cloud.lab.bff.api.dto.sse.SseEventType;
import org.pwr.cloud.lab.bff.api.dto.sse.Station;
import org.pwr.cloud.lab.bff.domain.repository.OrderStatusUpdateRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "dashboard.demo-data", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DemoMetricsDataInitializer implements ApplicationRunner {

    private static final int ORDERS = 300;
    private static final Duration HISTORY = Duration.ofDays(30);
    private static final double ALLOCATION_FAIL_RATE = 0.12;
    private static final double PICK_FAIL_RATE = 0.10;

    private final OrderStatusUpdateRepository repository;
    private final Random random = new Random(42);

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (repository.count() > 0) {
            log.info("order_status_updates already populated - skipping demo metrics seed");
            return;
        }

        Instant now = Instant.now();
        long windowMs = HISTORY.toMillis();
        int shipped = 0;

        for (int i = 0; i < ORDERS; i++) {
            String orderId = UUID.randomUUID().toString();
            Instant t = now.minusMillis((long) (random.nextDouble() * windowMs));
            save(orderId, SseEventType.ORDER_CREATED, Station.ORDER_GATEWAY, t);

            // Reservation: usually milliseconds, occasionally a few seconds.
            t = t.plusMillis(50 + (long) (random.nextDouble() * (random.nextDouble() < 0.1 ? 5000 : 800)));
            if (random.nextDouble() < ALLOCATION_FAIL_RATE) {
                save(orderId, SseEventType.ALLOCATION_FAILED, Station.RESERVATION, t);
                continue;
            }
            save(orderId, SseEventType.STOCK_RESERVED, Station.RESERVATION, t);

            // Picking: 1-20 minutes.
            t = t.plusMillis(60_000 + (long) (random.nextDouble() * 19 * 60_000));
            if (random.nextDouble() < PICK_FAIL_RATE) {
                save(orderId, SseEventType.PICK_FAILED, Station.PICKING, t);
                continue;
            }
            save(orderId, SseEventType.ORDER_PICKED, Station.PICKING, t);

            // Packing: 1-10 minutes.
            t = t.plusMillis(60_000 + (long) (random.nextDouble() * 9 * 60_000));
            save(orderId, SseEventType.PACKING_FINISHED, Station.PACKING, t);

            // Shipping: 1-30 minutes.
            t = t.plusMillis(60_000 + (long) (random.nextDouble() * 29 * 60_000));
            save(orderId, SseEventType.SHIPMENT_CREATED, Station.SHIPPING, t);
            shipped++;
        }

        log.info("Seeded demo metrics: {} orders over {} days ({} fully shipped)", ORDERS, HISTORY.toDays(), shipped);
    }

    private void save(String orderId, SseEventType type, String station, Instant timestamp) {
        repository.save(new OrderStatusUpdate(orderId, type, station, timestamp));
    }
}
