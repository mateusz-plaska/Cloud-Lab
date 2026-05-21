package org.pwr.cloud.lab.reservation.api.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.api.dto.ProductDto;
import org.pwr.cloud.lab.reservation.application.command.AddProductCommand;
import org.pwr.cloud.lab.reservation.application.query.GetProductsQuery;
import org.pwr.cloud.lab.reservation.domain.model.Product;
import org.pwr.cloud.lab.reservation.domain.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final Mediator mediator;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getProducts() {
        var products = mediator.ask(new GetProductsQuery());
        return ResponseEntity.ok(products);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> createProduct(@RequestParam @NotBlank String name) {
        var savedProduct = mediator.send(new AddProductCommand(name));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
}