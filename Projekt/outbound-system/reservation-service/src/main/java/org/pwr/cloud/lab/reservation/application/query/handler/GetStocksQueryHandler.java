package org.pwr.cloud.lab.reservation.application.query.handler;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.QueryHandler;
import org.pwr.cloud.lab.reservation.api.dto.StockDto;
import org.pwr.cloud.lab.reservation.application.query.GetStocksQuery;
import org.pwr.cloud.lab.reservation.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStocksQueryHandler implements QueryHandler<GetStocksQuery, List<StockDto>> {
    private final StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockDto> handle(GetStocksQuery query) {
        return stockRepository.findAll().stream().map(StockDto::from).toList();
    }
}
