package com.github.npcdw.store.util

import java.security.MessageDigest

object Sha256Util {
    /**
     * 利用java原生的摘要实现SHA256加密
     */
    fun getSHA256Str(bytes: ByteArray?): String {
        val messageDigest: MessageDigest
        var encodeStr = ""
        try {
            messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(bytes)
            encodeStr = byte2Hex(messageDigest.digest())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encodeStr
    }

    /**
     * 将byte转为16进制
     */
    private fun byte2Hex(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        var temp: String?
        for (aByte in bytes) {
            temp = Integer.toHexString(aByte.toInt() and 0xFF)
            if (temp.length == 1) {
                // 1得到一位的进行补0操作
                stringBuilder.append("0")
            }
            stringBuilder.append(temp)
        }
        return stringBuilder.toString()
    }
}
