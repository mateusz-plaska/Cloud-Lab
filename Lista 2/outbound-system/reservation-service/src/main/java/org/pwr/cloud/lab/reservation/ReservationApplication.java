package org.pwr.cloud.lab.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.pwr.cloud.lab.reservation",
        "org.pwr.cloud.lab.common"
})
public class ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
    }
}