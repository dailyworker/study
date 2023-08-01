package io.github.brewagebear.domain.order;

import io.github.brewagebear.domain.order.item.OrderItem;
import io.github.brewagebear.domain.order.item.OrderItemOption;
import io.github.brewagebear.domain.order.item.OrderItemOptionGroup;

public interface OrderStore {
    Order store(Order order);
    OrderItem store(OrderItem orderItem);
    OrderItemOptionGroup store(OrderItemOptionGroup orderItemOptionGroup);
    OrderItemOption store(OrderItemOption orderItemOption);
}
