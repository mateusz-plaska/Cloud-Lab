package org.pwr.cloud.lab.ordergateway.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.id.CustomerId;
import org.pwr.cloud.lab.common.domain.id.ProductId;
import org.pwr.cloud.lab.ordergateway.application.OrderItemConverter;
import org.pwr.cloud.lab.ordergateway.application.OrderService;
import org.pwr.cloud.lab.ordergateway.infrastructure.FileMetadataParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderItemConverter orderItemConverter;
    private final FileMetadataParser fileParser;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createOrder(
            @RequestPart("data") @Valid CreateOrderRequestDto request, @RequestPart("file") MultipartFile file) {
        var metadata = fileParser.parse(file);
        metadata.put("_filename", file.getOriginalFilename());
        var orderItems = orderItemConverter.convert(request.items());
        var orderId = orderService.createOrder(request.customerId, orderItems, metadata);
        return ResponseEntity.ok("Order registered with ID: " + orderId);
    }

    public record CreateOrderRequestDto(
            @NotNull @Valid CustomerId customerId, @NotEmpty List<@Valid @NotNull OrderItemDto> items) {}

    public record OrderItemDto(
            @NotNull @Valid ProductId productId,
            @NotNull @Min(1) @Max(1000000) Integer quantity) {}
}
