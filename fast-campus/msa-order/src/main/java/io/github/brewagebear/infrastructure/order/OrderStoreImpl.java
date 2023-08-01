package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderStore;
import io.github.brewagebear.domain.order.item.OrderItem;
import io.github.brewagebear.domain.order.item.OrderItemOption;
import io.github.brewagebear.domain.order.item.OrderItemOptionGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStoreImpl implements OrderStore {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final OrderItemOptionGroupRepository orderItemOptionGroupRepository;

    @Override
    public Order store(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderItem store(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItemOptionGroup store(OrderItemOptionGroup orderItemOptionGroup) {
        return orderItemOptionGroupRepository.save(orderItemOptionGroup);
    }

    @Override
    public OrderItemOption store(OrderItemOption orderItemOption) {
        return orderItemOptionRepository.save(orderItemOption);
    }
}
