package com.github.npcdw.store.config

import android.content.Context
import android.util.Log
import com.github.npcdw.store.util.JsonUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object GlobalConfig {
    @JvmField
    var INTERFACE_API_BASE_URL = ""

    @JvmField
    var MINIO_API_BASE_URL = ""

    @JvmField
    var MINIO_BUCKET = ""

    @JvmField
    var MINIO_USERNAME = ""

    @JvmField
    var MINIO_PASSWORD = ""

    @JvmField
    var TOKEN = ""

    /**
     * 读取配置文件
     */
    fun getConfig(context: Context) {
        try {
            context.assets.open("app_config.json").use { `in` ->
                InputStreamReader(`in`).use { isr ->
                    BufferedReader(isr).use { br ->
                        val result = StringBuilder()
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            result.append(line)
                        }
                        val map = JsonUtil.parseObject(result.toString())
                        INTERFACE_API_BASE_URL = map["interface_api_base_url"].toString()
                        MINIO_API_BASE_URL = map["minio_api_base_url"].toString()
                        MINIO_BUCKET = map["minio_bucket"].toString()
                        MINIO_USERNAME = map["minio_username"].toString()
                        MINIO_PASSWORD = map["minio_password"].toString()
                        TOKEN = map["token"].toString()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("Config", "获取配置文件出错", e)
        }
    }
}
