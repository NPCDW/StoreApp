package com.github.npcdw.store.util;

import android.util.Log;

import com.github.npcdw.store.config.GlobalConfig;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

public class MinioUtil {

    public static String upload(String bucket, String filename) {
        try {
            String suffix = "";
            int dot = filename.lastIndexOf(".");
            if (dot != -1) {
                suffix = filename.substring(dot);
            }
            File file = new File(filename);
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
            String sha256 = Sha256Util.getSHA256Str(bytes);
            String objectName = sha256 + suffix;

            MinioClient minioClient = MinioClient.builder()
                    .endpoint(GlobalConfig.MINIO_API_BASE_URL)
                    .credentials(GlobalConfig.MINIO_USERNAME, GlobalConfig.MINIO_PASSWORD)
                    .build();

            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filename)
                    .build());
            return objectName;
        } catch (Exception e) {
            Log.e("Minio", "Error occurred: ", e);
            return null;
        }
    }

}
