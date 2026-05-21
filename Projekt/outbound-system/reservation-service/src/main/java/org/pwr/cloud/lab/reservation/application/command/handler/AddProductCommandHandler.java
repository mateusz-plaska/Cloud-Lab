package org.pwr.cloud.lab.reservation.application.command.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.CommandHandler;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.api.dto.ProductDto;
import org.pwr.cloud.lab.reservation.application.command.AddProductCommand;
import org.pwr.cloud.lab.reservation.application.command.AddStockCommand;
import org.pwr.cloud.lab.reservation.domain.model.Product;
import org.pwr.cloud.lab.reservation.domain.model.Stock;
import org.pwr.cloud.lab.reservation.domain.repository.ProductRepository;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddProductCommandHandler implements CommandHandler<AddProductCommand, ProductDto> {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto handle(AddProductCommand command) {
        var product = Product.builder()
                .productId(ProductId.newInstance())
                .name(command.name())
                .build();

        var saved = productRepository.save(product);
        return ProductDto.from(saved);
    }
}
