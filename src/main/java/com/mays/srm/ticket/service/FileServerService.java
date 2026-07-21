package com.mays.srm.ticket.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileServerService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.server.url:}")
    private String fileServerUrl;

    /**
     * Stores a file locally in the uploads directory and returns its public URL.
     * @param file The file to upload.
     * @param filename The desired filename.
     * @return The URL of the stored file.
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String filename) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided for upload");
        }

        // Extract clean filename base
        String safeBaseFilename = Paths.get(filename != null ? filename : "file").getFileName().toString();
        String uniqueFilename = System.currentTimeMillis() + "_" + safeBaseFilename;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String baseUrl = (fileServerUrl != null && !fileServerUrl.trim().isEmpty())
                ? fileServerUrl.replaceAll("/+$", "")
                : ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return baseUrl + "/files/" + uniqueFilename;
    }

    /**
     * Deletes a file from the local uploads directory.
     * @param filename The unique name of the file on the server.
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }
        try {
            String safeFilename = Paths.get(filename).getFileName().toString();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(safeFilename).normalize();

            if (filePath.startsWith(uploadPath) && Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to delete file: " + e.getMessage());
        }
    }
}
