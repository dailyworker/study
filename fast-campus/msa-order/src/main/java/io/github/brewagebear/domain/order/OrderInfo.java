package io.github.brewagebear.domain.order;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderInfo {
    public record Main(
        Long orderId,
        String orderToken,
        Long userId,
        String payMethod,
        Long totalAmount,
        DeliveryInfo deliveryInfo,
        ZonedDateTime orderedAt,
        String status,
        String statusDescription,
        List<OrderItem>orderItemList
    ) { }

    public record DeliveryInfo(
       String receiverName,
       String receiverPhone,
       String receiverZipcode,
       String receiverAddress1,
       String receiverAddress2,
       String etcMessage
    ) { }

    public record OrderItem(
        Integer orderCount,
        Long partnerId,
        Long itemId,
        String itemName,
        Long totalAmount,
        Long itemPrice,
        String deliveryStatus,
        String deliveryStatusDescription,
        List<OrderItemOptionGroup> orderItemOptionGroupList
    ) { }

    public record OrderItemOptionGroup(
      Integer ordering,
      String itemOptionGroupName,
      List<OrderItemOption> orderItemOptionList
    ) { }

    public record OrderItemOption(
          Integer ordering,
          String itemOptionName,
          Long itemOptionPrice
    ) {}

}
