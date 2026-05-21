package org.pwr.cloud.lab.reservation.application.query;

import org.pwr.cloud.lab.common.application.cqs.Query;
import org.pwr.cloud.lab.reservation.api.dto.StockDto;

import java.util.List;

public record GetStocksQuery() implements Query<List<StockDto>> {}
