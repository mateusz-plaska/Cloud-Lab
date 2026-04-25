package org.pwr.cloud.lab.packing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.pwr.cloud.lab.packing", "org.pwr.cloud.lab.common"})
public class PackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PackingApplication.class, args);
    }
}
