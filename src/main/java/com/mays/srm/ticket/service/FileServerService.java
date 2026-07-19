package com.mays.srm.ticket.service;
import com.mays.srm.util.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileServerService {

    @Value("${file.server.url}")
    private String fileServerUrl;

    /**
     * Uploads a file to the external file server.
     * @param file The file to upload.
     * @param filename The desired filename to be sent to the server.
     * @return The URL of the uploaded file.
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String filename) throws Exception {
        RestTemplate rest = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(
                file.getInputStream(), filename)); // Use the provided filename

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response;
        try {
            response = rest.postForEntity(
                    fileServerUrl + "/upload", request, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to File Server: " + e.getMessage(), e);
        }

        if (response.getBody() != null && response.getBody().containsKey("url")) {
             return (String) response.getBody().get("url");
        }
        
        throw new RuntimeException("Failed to get URL from File Server");
    }

    /**
     * Deletes a file from the external file server.
     * @param filename The unique name of the file on the server.
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }
        RestTemplate rest = new RestTemplate();
        // fileServerUrl typically ends with /upload, we replace it with /delete/
        String deleteUrl = fileServerUrl + "/delete/" + filename;
        try {
            rest.delete(deleteUrl);
        } catch (Exception e) {
             throw new RuntimeException("Failed to delete file from File Server: " + e.getMessage(), e);
        }
    }
}
