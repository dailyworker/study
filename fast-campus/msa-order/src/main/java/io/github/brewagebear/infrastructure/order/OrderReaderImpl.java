package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.common.exception.EntityNotFoundException;
import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReaderImpl implements OrderReader {
    private final OrderRepository orderRepository;

    @Override
    public Order getOrder(String orderToken) {
        return orderRepository.findByOrderToken(orderToken)
                .orElseThrow(EntityNotFoundException::new);
    }
}
