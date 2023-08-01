package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.domain.item.ItemReader;
import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderCommand;
import io.github.brewagebear.domain.order.OrderStore;
import io.github.brewagebear.domain.order.OrdersSeriesFactory;
import io.github.brewagebear.domain.order.item.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderItemsSeriesFactoryImpl implements OrdersSeriesFactory {
    private final ItemReader itemReader;
    private final OrderStore orderStore;

    @Override
    public List<OrderItem> store(Order order, OrderCommand.RegisterOrder requestOrder) {
        return requestOrder.orderItemList()
                .stream()
                .map(orderItemRequest -> {
                    var item = itemReader.getItemBy(orderItemRequest.itemToken());
                    var initOrderItem = orderItemRequest.toEntity(order, item);
                    var orderItem = orderStore.store(initOrderItem);

                    orderItemRequest.orderItemOptionGroups()
                            .stream()
                            .forEach(orderItemOptionGroupRequest -> {
                                var initOrderItemOptionGroup = orderItemOptionGroupRequest.toEntity(orderItem);
                                var orderItemOptionGroup = orderStore.store(initOrderItemOptionGroup);

                                orderItemOptionGroupRequest.orderItemOptionList().forEach(orderItemOptionRequest -> {
                                    var initOrderItemOption = orderItemOptionRequest.toEntity(orderItemOptionGroup);
                                    orderStore.store(initOrderItemOption);
                                });
                            });

                    return orderItem;
                }).collect(Collectors.toList());
    }
}
