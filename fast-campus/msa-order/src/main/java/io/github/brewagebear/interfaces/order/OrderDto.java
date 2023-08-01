package io.github.brewagebear.interfaces.order;

import io.github.brewagebear.domain.order.payment.PayMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDto {
    public record RegisterOrderRequest(
            @NotNull(message = "userId is required") Long userId,
            @NotBlank(message = "payMethod is required") String payMethod,
            @NotBlank(message = "receiverName is required") String receiverName,
            @NotBlank(message = "receiverPhone is required") String receiverPhone,
            @NotBlank(message = "receiverZipcode is required") String receiverZipcode,
            @NotBlank(message = "receiverAddress1 is required") String receiverAddress1,
            @NotBlank(message = "receiverAddress2 is required") String receiverAddress2,
            @NotBlank(message = "etcMessage is required") String etcMessage,
            List<RegisterOrderItem> orderItemList
    ) {}

    public record RegisterOrderItem(
            @NotNull(message = "orderCount is required") Integer orderCount,
            @NotBlank(message = "itemToken is required") String itemToken,
            @NotBlank(message = "itemName is required") String itemName,
            @NotNull(message = "itemPrice is required") Long itemPrice,
            List<RegisterOrderItemOptionGroupRequest> orderItemOptionGroups
    ) {}

    public record RegisterOrderItemOptionGroupRequest(
            @NotNull(message = "ordering is required") Integer ordering,
            @NotBlank(message = "itemOptionGroupName is required") String itemOptionGroupName,
            List<RegisterOrderItemOptionRequest> orderItemOptionList
    ) {}

    public record RegisterOrderItemOptionRequest(
            @NotNull(message = "ordering is required") Integer ordering,
            @NotBlank(message = "itemOptionName is required") String itemOptionName,
            @NotNull(message = "itemOptionPrice is required") Long itemOptionPrice
    ) {}

    public record RegisterResponse(String orderToken) {}

    public record PaymentRequest(
            @NotBlank(message = "orderToken is required") String orderToken,
            @NotNull(message = "payMethod is required") PayMethod payMethod,
            @NotNull(message = "amount is required") Long amount,
            @NotBlank(message = "orderDescription is required") String orderDescription
    ) {}

    public record Main(
            String orderToken,
            Long userId,
            String payMethod,
            Long totalAmount,
            DeliveryInfo deliveryInfo,
            String orderedAt,
            String status,
            String statusDescription,
            List<OrderItem> orderItemList
    ) {}

    public record DeliveryInfo(
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress1,
            String receiverAddress2,
            String etcMessage
    ) {}

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
    ) {}

    public record OrderItemOptionGroup(
            Integer ordering,
            String itemOptionGroupName,
            List<OrderItemOption> orderItemOptionList
    ) {}

    public record OrderItemOption(
            Integer ordering,
            String itemOptionName,
            Long itemOptionPrice
    ) {}
}
