package org.pwr.cloud.lab.reservation.api.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.api.dto.ProductDto;
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
    private final ProductRepository productRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productRepository.findAll().stream().map(ProductDto::from).toList());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<ProductDto> createProduct(@RequestParam @NotBlank String name) {
        var product = Product.builder()
                .productId(ProductId.newInstance())
                .name(name)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductDto.from(productRepository.save(product)));
    }
}