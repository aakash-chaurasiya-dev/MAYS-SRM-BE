package com.mays.srm.service;

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

        ResponseEntity<Map> response = rest.postForEntity(
                fileServerUrl, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("url")) {
             return (String) response.getBody().get("url");
        }
        
        throw new RuntimeException("Failed to get URL from File Server");
    }
}
