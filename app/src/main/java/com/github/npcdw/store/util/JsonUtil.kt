package com.github.npcdw.store.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

object JsonUtil {
    private val readMapper: ObjectMapper
        get() {
            val mapper = ObjectMapper()
            // 在遇到未知属性的时候不抛出异常
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            return mapper
        }
    private val writeMapper: ObjectMapper
        get() {
            val mapper = ObjectMapper()
            // 忽略为null的字段
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            return mapper
        }

    @Throws(IOException::class)
    fun parseObject(value: String): Map<String?, Any?> {
        return readMapper.readValue(
            value,
            object : TypeReference<Map<String?, Any?>>() {})
    }

    @Throws(IOException::class)
    fun <T> parseObject(value: String, clazz: Class<T>): T {
        return readMapper.readValue(value, clazz)
    }

    @Throws(IOException::class)
    fun parseArray(value: String): List<Any> {
        return parseArray(value, Any::class.java)
    }

    @Throws(IOException::class)
    fun <T> parseArray(value: String, clazz: Class<T>): List<T> {
        val type = readMapper.typeFactory.constructCollectionLikeType(
            MutableList::class.java, clazz
        )
        return readMapper.readValue(value, type)
    }

    @Throws(IOException::class)
    fun <T> parse(value: String, typeReference: TypeReference<T>): T {
        return readMapper.readValue(value, typeReference)
    }

    @Throws(IOException::class)
    fun toJsonString(value: Any?): String? {
        return if (value == null) {
            null
        } else (value as? String)?.toString()
            ?: writeMapper.writeValueAsString(value)
    }
}
