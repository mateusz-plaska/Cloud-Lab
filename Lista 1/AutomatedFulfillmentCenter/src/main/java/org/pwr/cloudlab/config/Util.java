package org.pwr.cloudlab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class Util {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static int someInteger(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);
    }
}
