package org.pwr.cloud.lab.ordergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"org.pwr.cloud.lab.ordergateway", "org.pwr.cloud.lab.common"})
@EnableJpaAuditing
public class OrderGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderGatewayApplication.class, args);
    }
}
