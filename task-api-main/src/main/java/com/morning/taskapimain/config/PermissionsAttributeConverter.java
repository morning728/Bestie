package com.morning.taskapimain.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.project.Permission;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Converter
@Slf4j
public class PermissionsAttributeConverter implements AttributeConverter<Map<Permission, Boolean>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Permission, Boolean> permissionsJson) {
        try {
            return objectMapper.writeValueAsString(permissionsJson);
        } catch (JsonProcessingException jpe) {
            log.warn("Cannot convert Address into JSON");
            return null;
        }
    }

    @Override
    public Map<Permission, Boolean> convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<Map<Permission, Boolean>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert JSON into Address");
            return null;
        }
    }
}
