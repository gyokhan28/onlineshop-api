package com.example.online_shop_api.Service;

import com.example.online_shop_api.Utils.FileUtils;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${server.port}")
    private String serverPort;

    public String uploadFile(String fileNameOnServer, String filePathAndName) throws Exception {
        try {
            Path path = Paths.get(filePathAndName);
            String contentType = Files.probeContentType(path);

            UploadObjectArgs uArgs = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileNameOnServer) //"image.png"
                    .filename(filePathAndName) // C:\Users\User\Pictures\picture.png
                    .contentType(contentType != null ? contentType : "application/octet-stream") // Set the type to bytes if not found. Example: "image/png"
                    .build();
            ObjectWriteResponse response = minioClient.uploadObject(uArgs);
            return response.object();
        } catch (MinioException e) {
            throw new Exception("Error occurred while uploading file to MinIO", e);
        }
    }

    public String uploadFile(String folderNameOnServer, String fileNameOnServer, String filePathAndName) throws Exception {
        if (folderNameOnServer == null) {
            return uploadFile(fileNameOnServer, filePathAndName);
        } else {
            return uploadFile(folderNameOnServer + "/" + fileNameOnServer, filePathAndName);
        }
    }

    public String uploadProductImage(String productId, String filePathAndName) throws Exception {
        String uniqueFileName = FileUtils.generateUUIDFileName(filePathAndName);
        return uploadFile(productId, uniqueFileName, filePathAndName);
    }

    public InputStream downloadFile(String objectName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (MinioException e) {
            throw new Exception("Error occurred while downloading file from MinIO", e);
        }
    }

    /**
     * Will return all file names for a product
     *
     * @param directory - 13
     * @return [
     * "13/68061e04-a63b-43a9-a3a5-1e2e5cd1ea9f.png",
     * "13/e32d8450-3615-4c26-92e7-b7359883001b.png"
     * ]
     * @throws Exception
     */
    public List<String> listFilesInDirectory(String directory) throws Exception {
        List<String> files = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(directory + "/")
                            .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();

                if (!objectName.endsWith("/")) {
                    files.add(objectName);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error occurred while listing files in directory", e);
        }
        return files;
    }

    /**
     *
     * @param directory - 13
     * @return
     * [
     *   "localhost:8082/api/image/download/13/68061e04-a63b-43a9-a3a5-1e2e5cd1ea9f.png",
     *   "localhost:8082/api/image/download/13/e32d8450-3615-4c26-92e7-b7359883001b.png"
     * ]
     * @throws Exception
     */
    public List<String> listFilesInDirectoryFullPath(String directory) throws Exception {
        List<String> files = listFilesInDirectory(directory);
        List<String> fullPathFiles = new ArrayList<>();
        for (String file : files) {
            fullPathFiles.add("localhost:" + serverPort + "/api/image/download/" + file); // TODO - change localhost ?
        }
        return fullPathFiles;
    }
}