package org.pwr.cloud.lab.bff.infrastructure.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.ext.javatime.deser.InstantDeserializer;
import tools.jackson.databind.ext.javatime.ser.ZonedDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class JsonMapperConfiguration {

    @Bean
    public JsonMapper jsonMapper() {
        SimpleModule zonedDateTimeModule = new SimpleModule();
        zonedDateTimeModule.addSerializer(
                ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        zonedDateTimeModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);

        return JsonMapper.builder()
                .addModule(zonedDateTimeModule)
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .defaultTimeZone(TimeZone.getDefault())
                .build();
    }
}
