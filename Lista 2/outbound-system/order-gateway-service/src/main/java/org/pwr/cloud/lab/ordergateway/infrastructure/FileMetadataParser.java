package org.pwr.cloud.lab.ordergateway.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FileMetadataParser {

    public Map<String, String> parse(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=")) {
                    var parts = line.split("=");
                    metadata.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            log.error("Error parsing metadata file", e);
        }
        return metadata;
    }
}
