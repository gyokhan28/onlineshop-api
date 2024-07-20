package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("fileNameOnServer") String fileNameOnServer,
                                             @RequestParam(value = "folderNameOnServer", required = false) String folderNameOnServer,
                                             @RequestParam("fileName") String filename) {
        try {
            String fileLocationOnServer = minioService.uploadFile(folderNameOnServer, fileNameOnServer, filename);
            return ResponseEntity.ok("File uploaded successfully: " + fileLocationOnServer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    /**
     * The idea is to store multiple images for each product.
     *
     * @param productId - product ID
     * @param filename  - File location
     * @return File uploaded successfully: product_1/431b410d-7442-4897-8c33-82212381d002.png
     */
    @PostMapping(value = "/upload_product_image")
    public ResponseEntity<String> uploadProductImage(@RequestParam(value = "productId") String productId,
                                                     @RequestParam("fileName") String filename) {
        try {
            String fileLocationOnServer = minioService.uploadProductImage(productId, filename);
            return ResponseEntity.ok("File uploaded successfully: " + fileLocationOnServer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Download a file from MinIO")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully", content = @Content(schema = @Schema(implementation = InputStreamResource.class)))
    @ApiResponse(responseCode = "500", description = "Error occurred while downloading file")
    @GetMapping("/download/{productId}/{objectName}")
    public ResponseEntity<?> downloadFile(@PathVariable String productId,
                                          @PathVariable String objectName) {
        try {
            InputStream inputStream = minioService.downloadFile(productId + "/" + objectName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/list_images_for_product_id/{productId}")
    public ResponseEntity<?> getFileNames(@PathVariable String productId) throws Exception {
        try {
            return ResponseEntity.ok(minioService.listFilesInDirectory(productId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/list_full_path_images_for_product_id/{productId}")
    public ResponseEntity<?> getFullFileNames(@PathVariable String productId) throws Exception {
        try {
            return ResponseEntity.ok(minioService.listFilesInDirectoryFullPath(productId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }


}