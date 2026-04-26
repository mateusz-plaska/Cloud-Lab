package org.pwr.cloud.lab.ordergateway.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.api.dto.CreateOrderRequestDto;
import org.pwr.cloud.lab.ordergateway.api.dto.OrderReportDto;
import org.pwr.cloud.lab.ordergateway.application.command.CreateOrderCommand;
import org.pwr.cloud.lab.ordergateway.application.query.GetOrderFileQuery;
import org.pwr.cloud.lab.ordergateway.application.query.GetOrderQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final Mediator mediator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderId> createOrder(
            @RequestPart("data") @Valid CreateOrderRequestDto request, @RequestPart("file") MultipartFile file) {
        var command = new CreateOrderCommand(request.customerId(), request.items(), file);
        var orderId = mediator.send(command);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @GetMapping("/reports/{orderId}")
    public ResponseEntity<OrderReportDto> getReport(@PathVariable @Valid OrderId orderId) {
        var query = new GetOrderQuery(orderId);
        var orderReportDto = mediator.ask(query);
        return ResponseEntity.ok(orderReportDto);
    }

    @GetMapping("/{orderId}/file")
    public ResponseEntity<byte[]> getOrderFile(@PathVariable @Valid OrderId orderId) {
        var query = new GetOrderFileQuery(orderId);
        var fileDto = mediator.ask(query);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.filename() + "\"")
                .body(fileDto.content());
    }
}
