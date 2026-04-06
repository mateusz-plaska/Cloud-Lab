package org.pwr.cloud.lab.ordergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.pwr.cloud.lab.ordergateway",
        "org.pwr.cloud.lab.common"
})
public class OrderGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderGatewayApplication.class, args);
    }
}