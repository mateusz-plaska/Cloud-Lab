package org.pwr.cloud.lab.reservation.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.reservation.api.dto.ProductDto;
import org.pwr.cloud.lab.reservation.application.query.GetProductsQuery;
import org.pwr.cloud.lab.reservation.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProductsQueryHandler implements QueryHandler<GetProductsQuery, List<ProductDto>> {
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> handle(GetProductsQuery query) {
        return productRepository.findAll().stream().map(ProductDto::from).toList();
    }
}
