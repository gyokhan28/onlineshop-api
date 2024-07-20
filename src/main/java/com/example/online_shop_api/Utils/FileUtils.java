package com.example.online_shop_api.Utils;

import java.util.UUID;

public class FileUtils {
    public static String generateUUIDFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(dotIndex + 1);
    }
}
