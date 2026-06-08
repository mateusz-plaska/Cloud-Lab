package org.pwr.cloud.lab.bff;

import org.pwr.cloud.lab.bff.infrastructure.security.sso.SsoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.pwr.cloud.lab.bff", "org.pwr.cloud.lab.common"})
@EnableFeignClients(basePackages = "org.pwr.cloud.lab.bff.infrastructure.proxy")
@EnableConfigurationProperties(SsoProperties.class)
public class BffApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}
