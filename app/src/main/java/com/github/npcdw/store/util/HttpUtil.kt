package com.github.npcdw.store.util

import com.github.npcdw.store.config.GlobalConfig
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

/**
 * Http接口工具类
 */
object HttpUtil {
    private val APPLICATION_JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    @Throws(IOException::class)
    fun post(url: String, body: String): String? {
        val requestBody: RequestBody = body.toRequestBody(APPLICATION_JSON);
        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("token", GlobalConfig.TOKEN)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            return if (response.code != 200 || response.body == null) {
                null
            } else response.body!!.string()
        }
    }

    @Throws(IOException::class)
    operator fun get(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .header("token", GlobalConfig.TOKEN)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            return if (response.code != 200 || response.body == null) {
                null
            } else response.body!!.string()
        }
    }

    @Throws(IOException::class)
    fun put(url: String, body: String): String? {
        val requestBody: RequestBody = body.toRequestBody(APPLICATION_JSON);
        val request: Request = Request.Builder()
            .url(url)
            .put(requestBody)
            .header("token", GlobalConfig.TOKEN)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            return if (response.code != 200 || response.body == null) {
                null
            } else response.body!!.string()
        }
    }

    @Throws(IOException::class)
    fun delete(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .header("token", GlobalConfig.TOKEN)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            return if (response.code != 200 || response.body == null) {
                null
            } else response.body!!
                .string()
        }
    }
}
