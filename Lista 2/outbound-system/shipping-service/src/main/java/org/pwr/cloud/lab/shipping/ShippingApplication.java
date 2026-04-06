package org.pwr.cloud.lab.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.pwr.cloud.lab.shipping",
        "org.pwr.cloud.lab.common"
})
public class ShippingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShippingApplication.class, args);
    }
}
