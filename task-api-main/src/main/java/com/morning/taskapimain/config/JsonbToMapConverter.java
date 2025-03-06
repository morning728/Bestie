package com.morning.taskapimain.config;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

@ReadingConverter
public class JsonbToMapConverter implements Converter<Json, Map<String, Boolean>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Boolean> convert(Json source) {
        try {
            return objectMapper.readValue(source.asString(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSONB to Map<String, Boolean>", e);
        }
    }
}
