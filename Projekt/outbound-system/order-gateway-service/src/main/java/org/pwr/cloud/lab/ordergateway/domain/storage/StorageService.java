package org.pwr.cloud.lab.ordergateway.domain.storage;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file, OrderId orderId);

    byte[] loadFile(String objectKey);
}
