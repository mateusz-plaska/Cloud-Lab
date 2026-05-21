package org.pwr.cloud.lab.reservation.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.Mediator;
import org.pwr.cloud.lab.common.domain.model.id.ProductId;
import org.pwr.cloud.lab.reservation.api.dto.StockDto;
import org.pwr.cloud.lab.reservation.application.command.AddStockCommand;
import org.pwr.cloud.lab.reservation.application.query.GetStocksQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final Mediator mediator;

    @GetMapping
    public ResponseEntity<List<StockDto>> getStocks() {
        var stocks = mediator.ask(new GetStocksQuery());
        return ResponseEntity.ok(stocks);
    }

    @PostMapping()
    public ResponseEntity<Void> addStock(
            @RequestParam @Valid ProductId productId, @RequestParam @Min(1) @Max(1000) Integer quantity) {
        mediator.send(new AddStockCommand(productId, quantity));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
