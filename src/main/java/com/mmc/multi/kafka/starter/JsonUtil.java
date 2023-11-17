package com.mmc.multi.kafka.starter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * json 格式化工具
 */
@Slf4j
class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ObjectMapper snackToCamelMapper = new ObjectMapper();
    private static ObjectMapper objectAllMapper = new ObjectMapper();

    static {
        //序列化的时候序列对象的所有属性
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        //反序列化的时候如果多了其他属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //忽略字段大小写
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        //如果是空对象的时候,不抛异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        snackToCamelMapper.setSerializationInclusion(Include.NON_NULL);
        snackToCamelMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        snackToCamelMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        snackToCamelMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        objectAllMapper.setSerializationInclusion(Include.ALWAYS);
        //反序列化的时候如果多了其他属性,不抛出异常
        objectAllMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //如果是空对象的时候,不抛异常
        objectAllMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String toJsonStr(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("to json exception", e);
            return "";
        }
    }

    public static <T> T parseJsonObject(String str, Class<T> clazz) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            log.error("parse exception: str={}", str, e);
            return null;
        }
    }

    public static <T> T parseJsonObject(String str, TypeReference<T> ref) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, ref);
        } catch (JsonProcessingException e) {
            log.error("parse exception: str={}", str, e);
            return null;
        }
    }

    public static <T> List<T> parseJsonArray(String jsonString, Class<T> elementType) {

        if (StringUtils.hasText(jsonString)) {

            try {

                TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
                };
                return objectMapper.readValue(jsonString, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, elementType));

            } catch(Exception e) {

                log.error("parss error {}", jsonString, e);
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> List<T> parseSnackJsonArray(String jsonString, Class<T> elementType) throws Exception {

        if (StringUtils.hasText(jsonString)) {

            TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
            };
            return snackToCamelMapper.readValue(jsonString, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType));
        } else {
            return null;
        }
    }

    public static String toSnackJson(Object object) {
        try {
            return snackToCamelMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("to json exception", e);
            return "";
        }
    }

    public static <T> T parseSnackJson(String str, Class<T> clazz) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            return snackToCamelMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            log.error("parse exception: str={}", str, e);
            return null;
        }
    }

    public static <T> T parseSnackJson(String str, TypeReference<T> ref) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            return snackToCamelMapper.readValue(str, ref);
        } catch (JsonProcessingException e) {
            log.error("parse exception: str={}", str, e);
            return null;
        }
    }

    public static String toJsonStrWithAll(Object object) {
        try {
            return objectAllMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("to json exception", e);
            return "";
        }
    }

}
