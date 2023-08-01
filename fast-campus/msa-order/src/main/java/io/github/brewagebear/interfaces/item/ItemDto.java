package io.github.brewagebear.interfaces.item;

import io.github.brewagebear.domain.item.Item;

import java.util.List;

public class ItemDto {
    public record RegisterItemRequest(
        String partnerToken,
        String itemName,
        Long itemPrice,
        List<RegisterItemOptionGroupRequest> itemOptionGroupList
    ) {}

    public record RegisterItemOptionGroupRequest(
        Integer ordering,
        String itemOptionGroupName,
        List<RegisterItemOptionRequest> itemOptionList
    ) {}

    public record RegisterItemOptionRequest(
            Integer ordering,
            String itemOptionName,
            Long itemOptionPrice
    ) {}

    public record RegisterResponse(String itemToken) {}

    public record ChangeStatusItemRequest(String itemToken) {}

    public record Main(
       String itemToken,
       Long partnerId,
       String itemName,
       Long itemPrice,
       Item.Status status,
       List<ItemOptionGroupInfo> itemOptionGroupInfos
    ) {}

    public record ItemOptionGroupInfo(
        Integer ordering,
        String itemOptionGroupName,
        List<ItemOptionInfo> itemOptionInfos
    ) {}

    public record ItemOptionInfo(
        Integer ordering,
        String itemOptionName,
        Long itemOptionPrice
    ) {}

}
