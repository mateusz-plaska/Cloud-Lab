package org.pwr.cloudlab.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config", e);
        }
    }

    public static String getRabbitUrl() {
        return properties.getProperty("rabbitmq.url");
    }

    public static int getPickingInterval() {
        return Integer.parseInt(properties.getProperty("picking.interval.seconds", "5"));
    }
}
