package org.pwr.cloud.lab.picking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.pwr.cloud.lab.picking", "org.pwr.cloud.lab.common"})
public class PickingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickingApplication.class, args);
    }
}
