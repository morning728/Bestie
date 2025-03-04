package com.morning.taskapimain.config;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@WritingConverter
public class MapToJsonbConverter implements Converter<Map<String, Boolean>, Json> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Json convert(Map<String, Boolean> source) {
        try {
            return Json.of(objectMapper.writeValueAsString(source));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Map<String, Boolean> to JSONB", e);
        }
    }
}
