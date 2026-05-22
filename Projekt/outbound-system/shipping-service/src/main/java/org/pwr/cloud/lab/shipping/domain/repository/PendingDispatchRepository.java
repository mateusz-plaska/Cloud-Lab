package org.pwr.cloud.lab.shipping.domain.repository;

import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.PendingDispatch;

import java.util.List;

public interface PendingDispatchRepository {
    void save(PendingDispatch dispatch);
    List<PendingDispatch> findAllDue();
    void delete(OrderId orderId);
}
