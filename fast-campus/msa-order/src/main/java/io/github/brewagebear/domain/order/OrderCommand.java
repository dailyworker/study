package io.github.brewagebear.domain.order;

import io.github.brewagebear.domain.item.Item;
import io.github.brewagebear.domain.order.item.OrderItem;
import io.github.brewagebear.domain.order.item.OrderItemOption;
import io.github.brewagebear.domain.order.item.OrderItemOptionGroup;
import io.github.brewagebear.domain.order.fragment.DeliveryFragment;
import io.github.brewagebear.domain.order.payment.PayMethod;
import lombok.Builder;

import java.util.List;

public class OrderCommand {
    public record RegisterOrder(
         Long userId,
         String payMethod,
         String receiverName,
         String receiverPhone,
         String receiverZipcode,
         String receiverAddress1,
         String receiverAddress2,
         String etcMessage,
         List<RegisterOrderItem> orderItemList
    ) {
        public Order toEntity() {
            var deliveryFragment = DeliveryFragment.builder()
                    .receiverName(receiverName)
                    .receiverPhone(receiverPhone)
                    .receiverZipcode(receiverZipcode)
                    .receiverAddress1(receiverAddress1)
                    .receiverAddress2(receiverAddress2)
                    .etcMessage(etcMessage)
                    .build();

            return Order.builder()
                    .userId(userId)
                    .payMethod(payMethod)
                    .deliveryFragment(deliveryFragment)
                    .build();
        }
    }

    public record RegisterOrderItem(
            Integer orderCount,
            String itemToken,
            String itemName,
            Long itemPrice,
            List<RegisterOrderItemOptionGroup> orderItemOptionGroups
    ) {
        public OrderItem toEntity(Order order, Item item) {
            return OrderItem.builder()
                    .order(order)
                    .orderCount(orderCount)
                    .partnerId(item.getPartnerId())
                    .itemId(item.getId())
                    .itemToken(itemToken)
                    .itemName(itemName)
                    .itemPrice(itemPrice)
                    .build();
        }
    }

    public record RegisterOrderItemOptionGroup(
       Integer ordering,
       String itemOptionGroupName,
       List<RegisterOrderItemOption> orderItemOptionList
    ) {
        public OrderItemOptionGroup toEntity(OrderItem orderItem) {
            return OrderItemOptionGroup.builder()
                    .orderItem(orderItem)
                    .ordering(ordering)
                    .itemOptionGroupName(itemOptionGroupName)
                    .build();
        }
    }

    public record RegisterOrderItemOption(
       Integer ordering,
       String itemOptionName,
       Long itemOptionPrice
    ) {
        public OrderItemOption toEntity(OrderItemOptionGroup orderItemOptionGroup) {
            return OrderItemOption.builder()
                    .orderItemOptionGroup(orderItemOptionGroup)
                    .ordering(ordering)
                    .itemOptionName(itemOptionName)
                    .itemOptionPrice(itemOptionPrice)
                    .build();
        }
    }

    @Builder
    public record PaymentRequest(
       String orderToken,
       Long amount,
       PayMethod payMethod
    ) { }
}
