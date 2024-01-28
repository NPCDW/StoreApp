package com.github.npcdw.store.util

import android.util.Log
import com.github.npcdw.store.config.GlobalConfig
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileInputStream

object MinioUtil {
    fun upload(bucket: String?, filename: String): String? {
        return try {
            var suffix = ""
            val dot = filename.lastIndexOf(".")
            if (dot != -1) {
                suffix = filename.substring(dot)
            }
            val file = File(filename)
            val bytes = IOUtils.toByteArray(FileInputStream(file))
            val sha256 = Sha256Util.getSHA256Str(bytes)
            val objectName = sha256 + suffix
            val minioClient = MinioClient.builder()
                .endpoint(GlobalConfig.MINIO_API_BASE_URL)
                .credentials(GlobalConfig.MINIO_USERNAME, GlobalConfig.MINIO_PASSWORD)
                .build()
            minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(objectName)
                    .filename(filename)
                    .build()
            )
            objectName
        } catch (e: Exception) {
            Log.e("Minio", "Error occurred: ", e)
            null
        }
    }
}
