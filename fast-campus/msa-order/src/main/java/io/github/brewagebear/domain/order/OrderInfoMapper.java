package io.github.brewagebear.domain.order;

import io.github.brewagebear.domain.order.item.OrderItem;
import io.github.brewagebear.domain.order.item.OrderItemOption;
import io.github.brewagebear.domain.order.item.OrderItemOptionGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderInfoMapper {
    @Mappings({
            @Mapping(source = "order.id", target = "orderId"),
            @Mapping(source = "order.deliveryFragment", target = "deliveryInfo"),
            @Mapping(expression = "java(order.calculateTotalAmount())", target = "totalAmount"),
            @Mapping(expression = "java(order.getStatus().name())", target = "status"),
            @Mapping(expression = "java(order.getStatus().getDescription())", target = "statusDescription")
    })
    OrderInfo.Main of(Order order, List<OrderItem> orderItemList);

    @Mappings({
            @Mapping(expression = "java(orderItem.getDeliveryStatus().name())", target = "deliveryStatus"),
            @Mapping(expression = "java(orderItem.getDeliveryStatus().getDescription())", target = "deliveryStatusDescription"),
            @Mapping(expression = "java(orderItem.calculateTotalAmount())", target = "totalAmount")
    })
    OrderInfo.OrderItem of(OrderItem orderItem);

    OrderInfo.OrderItemOptionGroup of(OrderItemOptionGroup orderItemOptionGroup);

    OrderInfo.OrderItemOption of(OrderItemOption orderItemOption);
}
