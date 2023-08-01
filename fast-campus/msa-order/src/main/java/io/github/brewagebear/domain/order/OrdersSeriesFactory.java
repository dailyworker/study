package io.github.brewagebear.domain.order;

import io.github.brewagebear.domain.order.item.OrderItem;

import java.util.List;

public interface OrdersSeriesFactory {
    List<OrderItem> store(Order order, OrderCommand.RegisterOrder requestOrder);
}
