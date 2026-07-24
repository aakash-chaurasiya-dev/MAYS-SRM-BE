package com.mays.srm.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.server.url:}")
    private String fileServerUrl;

    /**
     * Upload a single file.
     * Saves the file into the configured upload directory with a unique timestamp prefix.
     * 
     * @param file Multipart file uploaded by client
     * @return JSON response with file url, filename, and type
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No file uploaded"));
        }

        try {
            // Clean original filename to prevent path injection
            String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_file"
            );

            // Strip any directory traversal components from original filename
            String safeBaseFilename = Paths.get(originalFilename).getFileName().toString();

            // Generate unique filename matching the Node.js behavior (Timestamp + filename)
            String uniqueFilename = System.currentTimeMillis() + "_" + safeBaseFilename;

            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Target file destination
            Path targetLocation = uploadPath.resolve(uniqueFilename);

            // Double check target location path traversal safety
            if (!targetLocation.startsWith(uploadPath)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid file path detected"));
            }

            // Save file to disk (overwriting if collision happens)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Construct file public URL
            String baseUrl = (fileServerUrl != null && !fileServerUrl.trim().isEmpty())
                    ? fileServerUrl.replaceAll("/+$", "")
                    : ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            String fileUrl = baseUrl + "/files/" + uniqueFilename;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("type", file.getContentType());
            response.put("filename", uniqueFilename);

            return ResponseEntity.ok(response);

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store file", "details", ex.getMessage()));
        }
    }

    /**
     * Delete a file by filename.
     * Enforces path traversal protection before attempting deletion.
     * 
     * @param filename Filename to delete
     * @return JSON status message
     */
    @DeleteMapping("/delete/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Filename must be provided"));
        }

        try {
            // Path traversal protection: Extract ONLY the filename portion
            String safeFilename = Paths.get(filename).getFileName().toString();

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(safeFilename).normalize();

            // Guard check: Ensure resolved path resides strictly inside uploadDir
            if (!filePath.startsWith(uploadPath)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid filename or path traversal attempt"));
            }

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "File not found"));
            }

            Files.delete(filePath);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));

        } catch (NoSuchFileException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file", "details", ex.getMessage()));
        }
    }
}
