package org.pwr.cloud.lab.ordergateway.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.ordergateway.api.converter.OrderItemConverter;
import org.pwr.cloud.lab.ordergateway.api.dto.CreateOrderRequestDto;
import org.pwr.cloud.lab.ordergateway.api.parser.FileMetadataParser;
import org.pwr.cloud.lab.ordergateway.application.service.OrderService;
import org.pwr.cloud.lab.ordergateway.domain.model.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderItemConverter orderItemConverter;
    private final FileMetadataParser fileParser;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Order> createOrder(
            @RequestPart("data") @Valid CreateOrderRequestDto request, @RequestPart("file") MultipartFile file) {
        var metadata = fileParser.parse(file);
        metadata.put("_filename", file.getOriginalFilename());
        var orderItems = orderItemConverter.convert(request.items());
        var order = orderService.createOrder(request.customerId(), orderItems, metadata);
        return ResponseEntity.ok(order);
    }
}
