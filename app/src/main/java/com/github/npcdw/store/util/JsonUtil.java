package com.github.npcdw.store.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static ObjectMapper getReadMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 在遇到未知属性的时候不抛出异常
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    private static ObjectMapper getWriteMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略为null的字段
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public static Map<String, Object> parseObject(String value) throws IOException {
        if (StringUtils.isBlank(value)) {
            throw new IOException("JSON转对象时，传入字符串为空");
        }
        return getReadMapper().readValue(value, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> T parseObject(String value, Class<T> clazz) throws IOException {
        if (StringUtils.isBlank(value)) {
            throw new IOException("JSON转对象时，传入字符串为空");
        }
        return getReadMapper().readValue(value, clazz);
    }

    public static List<Object> parseArray(String value) throws IOException {
        return parseArray(value, Object.class);
    }

    public static <T> List<T> parseArray(String value, Class<T> clazz) throws IOException {
        if (StringUtils.isBlank(value)) {
            throw new IOException("JSON转数组时，传入字符串为空");
        }
        CollectionLikeType type = getReadMapper().getTypeFactory().constructCollectionLikeType(List.class, clazz);
        return getReadMapper().readValue(value, type);
    }

    public static <T> T parse(String value, TypeReference<T> typeReference) throws IOException {
        if (StringUtils.isBlank(value)) {
            throw new IOException("JSON转对象时，传入字符串为空");
        }
        return getReadMapper().readValue(value, typeReference);
    }

    public static String toJsonString(Object value) throws IOException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return value.toString();
        }
        return getWriteMapper().writeValueAsString(value);
    }

}
