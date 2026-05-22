package org.pwr.cloud.lab.shipping.infrastructure.persistence.postgres;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.domain.model.BoxType;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.shipping.domain.model.PendingDispatch;
import org.pwr.cloud.lab.shipping.domain.repository.PendingDispatchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PendingDispatchRepositoryImpl implements PendingDispatchRepository {

    private final PendingDispatchJpaRepository pendingDispatchJpaRepository;

    @Override
    public void save(PendingDispatch dispatch) {
        pendingDispatchJpaRepository.save(toEntity(dispatch));
    }

    @Override
    public List<PendingDispatch> findAllDue() {
        return pendingDispatchJpaRepository.findAllDue().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(OrderId orderId) {
        pendingDispatchJpaRepository.deleteByOrderId(orderId.value());
    }

    private PendingDispatchEntity toEntity(PendingDispatch dispatch) {
        return PendingDispatchEntity.builder()
                .orderId(dispatch.orderId().value())
                .weight(dispatch.weight())
                .boxSize(dispatch.boxType().boxSize())
                .boxLength(dispatch.boxType().length())
                .boxWidth(dispatch.boxType().width())
                .boxHeight(dispatch.boxType().height())
                .dispatchAt(dispatch.dispatchAt())
                .build();
    }

    private PendingDispatch toDomain(PendingDispatchEntity entity) {
        var boxType = BoxType.builder()
                .boxSize(entity.getBoxSize())
                .length(entity.getBoxLength())
                .width(entity.getBoxWidth())
                .height(entity.getBoxHeight())
                .build();

        return PendingDispatch.builder()
                .orderId(OrderId.of(entity.getOrderId()))
                .weight(entity.getWeight())
                .boxType(boxType)
                .dispatchAt(entity.getDispatchAt())
                .build();
    }
}
